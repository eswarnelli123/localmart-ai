package com.localmart.retailer;

import com.localmart.auth.AuthResponse;
import com.localmart.auth.AuthService;
import com.localmart.auth.EmailService;
import com.localmart.auth.OtpRepository;
import com.localmart.inventory.Inventory;
import com.localmart.inventory.InventoryRepository;
import com.localmart.category.CategoryRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductImage;
import com.localmart.product.ProductImageRepository;
import com.localmart.product.ProductRepository;
import com.localmart.shop.Shop;
import com.localmart.offer.OfferRepository;
import com.localmart.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerRepository retailerRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final OfferRepository offerRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerRetailer(@RequestBody RetailerRequest request) {
        if (retailerRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Email already exists", null));
        }
        if (!authService.isPasswordStrong(request.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.", null));
        }

        Retailer retailer = new Retailer();
        retailer.setCompanyName(request.getName());
        retailer.setContactName(request.getName());
        retailer.setEmail(request.getEmail());
        retailer.setPassword(passwordEncoder.encode(request.getPassword()));
        retailer.setVerified(false);
        retailer.setStatus(Retailer.Status.pending);
        retailerRepository.save(retailer);

        String otp = generateOtp();
        com.localmart.auth.OtpVerification otpVerification = new com.localmart.auth.OtpVerification();
        otpVerification.setUserType(com.localmart.auth.OtpVerification.UserType.RETAILER);
        otpVerification.setUserId(retailer.getId());
        otpVerification.setEmail(request.getEmail());
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpVerification.setUsed(false);
        otpRepository.save(otpVerification);

        emailService.sendOtpEmail(request.getEmail(), otp);
        return ResponseEntity.ok(new AuthResponse(true, "Retailer account created. Please verify your email with the retailer OTP sent to your inbox.", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyRetailer(@RequestBody RetailerVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @GetMapping("/shops")
    public ResponseEntity<List<Shop>> getShops() {
        Retailer retailer = getCurrentRetailer();
        List<Shop> shops = shopRepository.findByOwnerId(retailer.getId());
        return ResponseEntity.ok(shops);
    }

    @PostMapping("/shop")
    public ResponseEntity<?> createShop(@Validated @RequestBody ShopRequest request) {
        Retailer retailer = getCurrentRetailer();

        Shop shop = new Shop();
        shop.setName(request.getName());
        shop.setSlug(generateSlug(request.getName()));
        shop.setDescription(request.getDescription());
        shop.setAddressLine1(request.getLocation());
        shop.setAddressLine2(request.getAddressLine2());
        shop.setCity(request.getCity() != null && !request.getCity().isBlank() ? request.getCity() : "Unknown");
        shop.setState(request.getState() != null && !request.getState().isBlank() ? request.getState() : "Unknown");
        shop.setCountry(request.getCountry() != null && !request.getCountry().isBlank() ? request.getCountry() : "Unknown");
        shop.setPhone(request.getPhone());
        shop.setPostalCode(request.getPostalCode());
        shop.setOwner(retailer);
        
        Shop savedShop = shopRepository.save(shop);
        return ResponseEntity.ok(savedShop);
    }

    private String generateSlug(String name) {
        String baseSlug = name == null ? "shop" : name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return baseSlug + "-" + System.currentTimeMillis();
    }

    @GetMapping("/products")
    public ResponseEntity<List<com.localmart.product.Product>> getProducts() {
        Retailer retailer = getCurrentRetailer();
        List<Shop> shops = shopRepository.findByOwnerId(retailer.getId());
        List<Product> products = productRepository.findByShopIn(shops);
        populateStock(products);
        populateProductImages(products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/shop/{shopId}/products")
    public ResponseEntity<List<com.localmart.product.Product>> getProductsByShop(@PathVariable Long shopId) {
        Retailer retailer = getCurrentRetailer();
        Shop shop = shopRepository.findById(shopId)
                .filter(s -> s.getOwner() != null && s.getOwner().getId().equals(retailer.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop not found or unauthorized"));
        List<Product> products = productRepository.findByShopIn(List.of(shop));
        populateStock(products);
        populateProductImages(products);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/shop/{shopId}/logo")
    public ResponseEntity<?> uploadLogo(@PathVariable Long shopId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        return uploadImage(shopId, file, true);
    }

    @PostMapping("/shop/{shopId}/banner")
    public ResponseEntity<?> uploadBanner(@PathVariable Long shopId,
                                          @RequestParam("file") MultipartFile file) throws IOException {
        return uploadImage(shopId, file, false);
    }

    @PostMapping(value = "/shop/{shopId}/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductWithImage(@PathVariable Long shopId,
                                                    @RequestPart("request") @Valid ProductRequest request,
                                                    @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        Product product = createAndSaveProduct(shopId, request);
        if (file != null && !file.isEmpty()) {
            saveProductImage(product, file);
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/shop/{shopId}/product")
    public ResponseEntity<?> createProduct(@PathVariable Long shopId,
                                           @Valid @RequestBody ProductRequest request) {
        Product product = createAndSaveProduct(shopId, request);
        return ResponseEntity.ok(product);
    }

    private Product createAndSaveProduct(Long shopId, ProductRequest request) {
        Retailer retailer = getCurrentRetailer();
        if (retailer == null || retailer.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Retailer not found");
        }
        
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found"));
        
        // Verify retailer owns this shop
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(retailer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to add products to this shop");
        }

        Product product = new Product();
        product.setShop(shop);
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(resolveCategoryId(request.getCategory()));
        product.setSku(generateProductSku(request.getName()));
        product.setActive(true);
        productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setStoreId(shop.getId());
        inventory.setQuantity(request.getStock() != null ? request.getStock() : 0);
        inventory.setAvailableQuantity(inventory.getQuantity());
        if (request.getDiscountPercent() != null && request.getDiscountPercent() > 0 && request.getPrice() != null) {
            double discount = Math.max(0.0, Math.min(request.getDiscountPercent(), 100.0));
            double discountedPrice = request.getPrice() * (1 - discount / 100.0);
            inventory.setPriceOverride(Math.round(discountedPrice * 100.0) / 100.0);
        }
        inventoryRepository.save(inventory);
        product.setStock(inventory.getQuantity());
        product.setPriceOverride(inventory.getPriceOverride());
        return product;
    }

    private void saveProductImage(Product product, MultipartFile file) throws IOException {
        String filePath = storeFile(file);
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(filePath);
        productImage.setAltText(file.getOriginalFilename());
        int nextOrder = product.getImages() == null ? 1 : product.getImages().size() + 1;
        productImage.setDisplayOrder(nextOrder);
        productImageRepository.save(productImage);
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestBody ProductRequest request) throws IOException {
        Product product = updateProductEntity(productId, request, null);
        return ResponseEntity.ok(product);
    }

    @PutMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProductWithImage(@PathVariable Long productId,
                                                    @RequestPart("request") ProductRequest request,
                                                    @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        Product product = updateProductEntity(productId, request, file);
        return ResponseEntity.ok(product);
    }

    private Product updateProductEntity(Long productId, ProductRequest request, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        verifyProductOwner(product);

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(resolveCategoryId(request.getCategory()));
        product.setActive(request.isActive());
        if (product.getSku() == null || product.getSku().isBlank()) {
            product.setSku(generateProductSku(request.getName()));
        }
        productRepository.save(product);

        Shop shop = product.getShop();
        if (shop != null) {
            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product.getId(), shop.getId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setProductId(product.getId());
                        newInventory.setStoreId(shop.getId());
                        return newInventory;
                    });
            inventory.setQuantity(request.getStock() != null ? request.getStock() : 0);
            inventory.setAvailableQuantity(inventory.getQuantity());
            if (request.getDiscountPercent() != null && request.getDiscountPercent() > 0 && request.getPrice() != null) {
                double discount = Math.max(0.0, Math.min(request.getDiscountPercent(), 100.0));
                double discountedPrice = request.getPrice() * (1 - discount / 100.0);
                inventory.setPriceOverride(Math.round(discountedPrice * 100.0) / 100.0);
            } else {
                inventory.setPriceOverride(null);
            }
            inventoryRepository.save(inventory);
            product.setStock(inventory.getQuantity());
            product.setPriceOverride(inventory.getPriceOverride());
        }

        if (file != null && !file.isEmpty()) {
            saveProductImage(product, file);
        }

        return product;
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        verifyProductOwner(product);
        productRepository.delete(product);
        return ResponseEntity.ok("Product deleted");
    }

    @PostMapping("/product/{productId}/image")
    public ResponseEntity<?> uploadProductImage(@PathVariable Long productId,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        verifyProductOwner(product);

        String filePath = storeFile(file);
        saveProductImage(product, file);
        return ResponseEntity.ok(Collections.singletonMap("imagePath", filePath));
    }

    @PostMapping("/shop/{shopId}/offer")
    public ResponseEntity<?> createOffer(@PathVariable Long shopId,
                                         @RequestBody OfferRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop not found"));
        verifyShopOwner(shop);

        com.localmart.offer.Offer offer = new com.localmart.offer.Offer();
        offer.setStore(shop);
        offer.setShopName(shop.getName());
        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        // Determine offer type: product if productId provided, else category or store
        if (request.getProductId() != null) {
            offer.setOfferType(com.localmart.offer.Offer.OfferType.product);
        } else if (request.getCategory() != null && !request.getCategory().isBlank()) {
            offer.setOfferType(com.localmart.offer.Offer.OfferType.category);
        } else {
            offer.setOfferType(com.localmart.offer.Offer.OfferType.store);
        }
        offer.setDiscountType(resolveDiscountType(request.getDiscountType()));
        offer.setDiscount(request.getDiscount());
        offer.setMinPurchaseAmount(request.getMinPurchaseAmount());
        offer.setStartDate(request.getStartDate() != null ? request.getStartDate() : offer.getStartDate());
        offer.setEndDate(request.getEndDate() != null ? request.getEndDate() : offer.getEndDate());
        offer.setActive(request.getActive() != null ? request.getActive() : offer.isActive());
        offer.setCategoryId(resolveCategoryId(request.getCategory()));
        offer.setLocation(shop.getLocation());

        if (offer.getOfferType() == com.localmart.offer.Offer.OfferType.product) {
            if (request.getProductId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product offer must include a selected product.");
            }
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected product not found"));
            if (product.getShop() == null || !shop.getId().equals(product.getShop().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected product does not belong to this shop.");
            }
            offer.setProductId(product.getId());
        } else {
            offer.setProductId(null);
        }
        return ResponseEntity.ok(offerRepository.save(offer));
    }

    @GetMapping("/products/by-category")
    public ResponseEntity<List<com.localmart.product.Product>> getProductsByCategory(@RequestParam String category) {
        Retailer retailer = getCurrentRetailer();
        Long categoryId = resolveCategoryId(category);
        if (categoryId == null) {
            return ResponseEntity.ok(List.of());
        }
        List<com.localmart.product.Product> products = productRepository.findByCategoryIdAndActiveTrue(categoryId);
        // filter to only products belonging to current retailer's shops
        List<Shop> shops = shopRepository.findByOwnerId(retailer.getId());
        List<Long> shopIds = new ArrayList<>();
        for (Shop s : shops) if (s.getId() != null) shopIds.add(s.getId());
        List<com.localmart.product.Product> filtered = new ArrayList<>();
        for (com.localmart.product.Product p : products) {
            if (p.getShop() != null && p.getShop().getId() != null && shopIds.contains(p.getShop().getId())) {
                filtered.add(p);
            }
        }
        populateStock(filtered);
        populateProductImages(filtered);
        return ResponseEntity.ok(filtered);
    }

    private Long resolveCategoryId(String categoryName) {
        // If category not provided or not found, fall back to default category id 1
        if (categoryName == null || categoryName.isBlank()) {
            return 1L;
        }
        return categoryRepository.findByNameIgnoreCase(categoryName.trim())
                .map(com.localmart.category.Category::getId)
                .orElse(1L);
    }

    private com.localmart.offer.Offer.OfferType resolveOfferType(String type) {
        if (type == null || type.isBlank()) {
            return com.localmart.offer.Offer.OfferType.store;
        }
        try {
            return com.localmart.offer.Offer.OfferType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            return com.localmart.offer.Offer.OfferType.store;
        }
    }

    private com.localmart.offer.Offer.DiscountType resolveDiscountType(String type) {
        if (type == null || type.isBlank()) {
            return com.localmart.offer.Offer.DiscountType.percentage;
        }
        try {
            return com.localmart.offer.Offer.DiscountType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            return com.localmart.offer.Offer.DiscountType.percentage;
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<List<com.localmart.offer.Offer>> getOffers() {
        Retailer retailer = getCurrentRetailer();
        List<Shop> shops = shopRepository.findByOwnerId(retailer.getId());
        List<com.localmart.offer.Offer> offers = offerRepository.findAllByStoreIn(shops);
        for (com.localmart.offer.Offer offer : offers) {
            if (offer.getProductId() != null) {
                productRepository.findById(offer.getProductId())
                        .ifPresent(product -> offer.setProductName(product.getName()));
            }
        }
        return ResponseEntity.ok(offers);
    }

    private void verifyShopOwner(Shop shop) {
        Retailer retailer = getCurrentRetailer();
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(retailer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to manage this shop");
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Retailer retailer = retailerRepository.findByEmail(email).orElseThrow();
        List<Shop> shops = shopRepository.findByOwnerId(retailer.getId());
        List<Product> products = productRepository.findByShopIn(shops);
        populateStock(products);
        AnalyticsResponse response = new AnalyticsResponse();
        response.setTotalShops(shops.size());
        response.setTotalProducts(products.size());
        response.setTotalStock(products.stream().mapToInt(p -> p.getStock() != null ? p.getStock() : 0).sum());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> uploadImage(Long shopId, MultipartFile file, boolean isLogo) throws IOException {
        Optional<Shop> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            return ResponseEntity.badRequest().body("Shop not found");
        }

        Shop shop = optionalShop.get();
        String filePath = storeFile(file);
        if (isLogo) {
            shop.setLogoPath(filePath);
        } else {
            shop.setBannerPath(filePath);
        }
        shopRepository.save(shop);
        return ResponseEntity.ok(shop);
    }

    private String storeFile(MultipartFile file) throws IOException {
        String uploadsPath = com.localmart.config.WebConfig.getUploadsDirectory();
        Path uploadDir = Paths.get(uploadsPath);
        Files.createDirectories(uploadDir);
        String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        file.transferTo(target);
        return "/uploads/" + filename;
    }

    private String generateProductSku(String productName) {
        String base = productName == null || productName.isBlank() ? "prod" : productName.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        return base + "-" + System.currentTimeMillis();
    }

    private void populateProductImages(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        for (Product product : products) {
            if (product == null || product.getId() == null) {
                continue;
            }
            List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId());
            if (images != null && !images.isEmpty()) {
                product.setImages(images);
                String imageUrl = images.stream()
                        .filter(image -> image != null && image.getImageUrl() != null && !image.getImageUrl().isBlank())
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null);
                product.setImagePath(imageUrl);
            }
        }
    }

    private void populateStock(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = new ArrayList<>();
        for (Product product : products) {
            if (product.getId() != null) {
                productIds.add(product.getId());
            }
        }
        if (productIds.isEmpty()) {
            return;
        }
        List<Inventory> inventories = inventoryRepository.findByProductIdIn(productIds);
        Map<Long, Inventory> inventoryByProduct = new HashMap<>();
        for (Inventory inventory : inventories) {
            inventoryByProduct.put(inventory.getProductId(), inventory);
        }
        for (Product product : products) {
            Inventory inventory = inventoryByProduct.get(product.getId());
            if (inventory != null) {
                product.setStock(inventory.getQuantity());
                product.setPriceOverride(inventory.getPriceOverride());
            }
        }
    }

    private Retailer getCurrentRetailer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String email = auth.getName();
        return retailerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private void verifyProductOwner(Product product) {
        Retailer retailer = getCurrentRetailer();
        if (product.getShop() == null || product.getShop().getOwner() == null || !product.getShop().getOwner().getId().equals(retailer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to modify this product");
        }
    }

    private String generateOtp() {
        return String.valueOf(100000 + new java.util.Random().nextInt(900000));
    }
}
