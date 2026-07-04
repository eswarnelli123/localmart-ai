package com.localmart.web;

import com.localmart.auth.AuthResponse;
import com.localmart.auth.AuthService;
import com.localmart.admin.AdminRepository;
import com.localmart.category.Category;
import com.localmart.category.CategoryRepository;
import com.localmart.coupon.Coupon;
import com.localmart.coupon.CouponRepository;
import com.localmart.history.SearchHistory;
import com.localmart.history.SearchHistoryRepository;
import com.localmart.inventory.Inventory;
import com.localmart.inventory.InventoryRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductImageRepository;
import com.localmart.product.ProductRepository;
import com.localmart.retailer.RetailerRepository;
import com.localmart.shop.Shop;
import com.localmart.rating.Rating;
import com.localmart.rating.RatingRepository;
import com.localmart.review.Review;
import com.localmart.review.ReviewRepository;
import com.localmart.wishlist.Wishlist;
import com.localmart.wishlist.WishlistRepository;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AuthService authService;
    private final RetailerRepository retailerRepository;
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final com.localmart.offer.OfferRepository offerRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CouponRepository couponRepository;
    private final WishlistRepository wishlistRepository;
    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "verified", required = false) String verified,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "message", required = false) String message,
                        Model model) {
        if (verified != null) {
            model.addAttribute("message", "Account verified successfully. Please log in.");
            model.addAttribute("success", true);
        } else if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
            model.addAttribute("success", true);
        } else if (message != null) {
            model.addAttribute("message", message);
            model.addAttribute("success", true);
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse httpServletResponse) {
        Cookie jwtCookie = new Cookie("JWT", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        httpServletResponse.addCookie(jwtCookie);
        return "redirect:/login?logout=true";
    }

    @PostMapping("/login")
    public String submitLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              Model model,
                              HttpServletResponse httpServletResponse) {
        try {
            AuthResponse response = authService.login(email, password);
            if (response.isSuccess()) {
                if (response.getToken() != null) {
                    Cookie cookie = new Cookie("JWT", response.getToken());
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(24 * 60 * 60);
                    httpServletResponse.addCookie(cookie);
                }
                return adminRepository.findByEmail(email)
                        .map(admin -> "redirect:/admin/dashboard")
                        .or(() -> retailerRepository.findByEmail(email)
                                .map(retailer -> "redirect:/retailer/dashboard?name=" + URLEncoder.encode(retailer.getContactName(), StandardCharsets.UTF_8)))
                        .orElse("redirect:/dashboard");
            }
            model.addAttribute("message", response.getMessage());
            model.addAttribute("success", false);
        } catch (Exception ex) {
            model.addAttribute("message", ex.getMessage());
            model.addAttribute("success", false);
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String userDashboard(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "category", required = false) String categorySlug,
                                Model model) {
        List<Category> categories = buildDashboardCategories();
        List<Product> products;

        if (query != null && !query.isBlank()) {
            String q = query.trim();
            List<Product> all = productRepository.findByActiveTrue();
            products = new ArrayList<>();
            for (Product p : all) {
                if (p == null) continue;
                if (com.localmart.product.ProductSearchMatcher.matches(p, q)) {
                    products.add(p);
                }
            }
            model.addAttribute("query", q);
            recordSearchHistory(q, products == null ? 0 : products.size());
        } else if (categorySlug != null && !categorySlug.isBlank()) {
            Optional<Category> categoryOpt = categoryRepository.findBySlug(categorySlug.trim());
            if (categoryOpt.isPresent()) {
                products = productRepository.findByCategoryIdAndActiveTrue(categoryOpt.get().getId());
            } else {
                products = categoryRepository.findByNameIgnoreCase(categorySlug.trim())
                        .map(category -> productRepository.findByCategoryIdAndActiveTrue(category.getId()))
                        .orElseGet(() -> productRepository.findByActiveTrue());
            }
            if (products == null) {
                products = Collections.emptyList();
            }
            model.addAttribute("selectedCategory", categorySlug.trim());
        } else {
            products = productRepository.findByActiveTrue();
        }

        populateStock(products);
        populateProductImages(products);
        applyOffers(products);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("coupons", loadActiveCoupons());
        model.addAttribute("customer", getCurrentCustomer().orElse(null));
        model.addAttribute("recentOrders", Collections.emptyList());
        return "dashboard";
    }

    @GetMapping("/product/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        populateStock(List.of(product));
        populateProductImages(List.of(product));
        applyOffers(List.of(product));

        List<Coupon> coupons = loadActiveCoupons();
        Optional<User> currentCustomer = getCurrentCustomer();
        boolean wishlisted = currentCustomer
                .flatMap(customer -> wishlistRepository.findByCustomerIdAndProductId(customer.getId(), productId))
                .isPresent();

        List<Product> relatedProducts = productRepository.findByCategoryIdAndActiveTrue(product.getCategoryId()).stream()
                .filter(p -> p.getId() != null && !p.getId().equals(productId))
                .limit(4)
                .collect(Collectors.toList());
        if (relatedProducts.isEmpty() && product.getShop() != null) {
            relatedProducts = productRepository.findByShop(product.getShop()).stream()
                    .filter(p -> p.getId() != null && !p.getId().equals(productId))
                    .limit(4)
                    .collect(Collectors.toList());
        }
        populateStock(relatedProducts);
        populateProductImages(relatedProducts);
        applyOffers(relatedProducts);

        List<Review> reviews = reviewRepository.findByProductIdAndStatus(productId, Review.Status.published);
        List<Long> reviewRatingIds = reviews.stream()
                .map(Review::getRatingId)
                .filter(id -> id != null)
                .toList();
        Map<Long, Integer> reviewRatings = reviewRatingIds.isEmpty() ? Collections.emptyMap() : ratingRepository.findAllById(reviewRatingIds).stream()
                .collect(Collectors.toMap(Rating::getId, Rating::getRatingValue));

        List<Rating> productRatings = ratingRepository.findByProductId(productId);
        Double averageRating = null;
        if (!productRatings.isEmpty()) {
            averageRating = Math.round(productRatings.stream().mapToInt(Rating::getRatingValue).average().orElse(0.0) * 10) / 10.0;
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewRatings", reviewRatings);
        model.addAttribute("ratingCount", productRatings.size());
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("product", product);
        model.addAttribute("coupons", coupons);
        model.addAttribute("wishlisted", wishlisted);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("customer", currentCustomer.orElse(null));
        model.addAttribute("googleMapsUrl", buildGoogleMapsLink(product.getShop()));
        return "product-detail";
    }

    @GetMapping("/wishlist")
    public String wishlistPage(Model model) {
        User customer = getCurrentCustomer()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to view your wishlist"));

        List<Wishlist> wishlistItems = wishlistRepository.findByCustomerId(customer.getId());
        List<Long> productIds = wishlistItems.stream().map(Wishlist::getProductId).toList();
        List<Product> products = productIds.isEmpty() ? Collections.emptyList() : productRepository.findAllById(productIds).stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .collect(Collectors.toList());

        populateStock(products);
        populateProductImages(products);
        applyOffers(products);

        model.addAttribute("products", products);
        model.addAttribute("customer", customer);
        return "wishlist";
    }

    @GetMapping("/coupons")
    public String couponList(Model model) {
        model.addAttribute("coupons", loadActiveCoupons());
        // `customer` is also populated via @ModelAttribute for all requests.
        return "coupons";
    }

    @ModelAttribute("customer")
    public User currentCustomerModel() {
        return getCurrentCustomer().orElse(null);
    }

    private Optional<User> getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        if ((username == null || username.isBlank()) && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails principal) {
            username = principal.getUsername();
        }
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(username);
    }

    private List<Coupon> loadActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(coupon -> coupon.isActive()
                        && !coupon.getValidFrom().isAfter(LocalDateTime.now())
                        && !coupon.getValidUntil().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    private String buildGoogleMapsLink(Shop shop) {
        if (shop == null) {
            return null;
        }
        // If we have coordinates, use them
        if (shop.getLatitude() != null && shop.getLongitude() != null) {
            String lat = String.valueOf(shop.getLatitude());
            String lng = String.valueOf(shop.getLongitude());
            return "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(lat + "," + lng, StandardCharsets.UTF_8);
        }
        // Fallback: use address to search on Google Maps
        if (shop.getAddressLine1() != null && !shop.getAddressLine1().isBlank()) {
            String address = shop.getAddressLine1();
            if (shop.getCity() != null && !shop.getCity().isBlank()) {
                address += ", " + shop.getCity();
            }
            if (shop.getState() != null && !shop.getState().isBlank()) {
                address += ", " + shop.getState();
            }
            if (shop.getPostalCode() != null && !shop.getPostalCode().isBlank()) {
                address += " " + shop.getPostalCode();
            }
            return "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(address, StandardCharsets.UTF_8);
        }
        return null;
    }

    private List<Category> buildDashboardCategories() {
        List<Category> categories = categoryRepository.findAll();
        Map<String, Category> slugMap = new HashMap<>();
        for (Category category : categories) {
            if (category.getSlug() != null) {
                slugMap.put(category.getSlug().toLowerCase(), category);
            }
            if (category.getName() != null) {
                slugMap.put(category.getName().trim().toLowerCase(), category);
            }
        }

        List<Category> defaultCategories = new ArrayList<>();
        defaultCategories.add(ensureCategoryExists(slugMap, "Groceries", "groceries"));
        defaultCategories.add(ensureCategoryExists(slugMap, "Electronics", "electronics"));
        defaultCategories.add(ensureCategoryExists(slugMap, "Fashion", "fashion"));
        defaultCategories.add(ensureCategoryExists(slugMap, "Health", "health"));

        return defaultCategories;
    }

    private Category ensureCategoryExists(Map<String, Category> slugMap, String name, String slug) {
        if (slugMap.containsKey(slug.toLowerCase())) {
            return slugMap.get(slug.toLowerCase());
        }
        if (slugMap.containsKey(name.toLowerCase())) {
            return slugMap.get(name.toLowerCase());
        }
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        // Persist missing default categories so they appear across sessions
        try {
            return categoryRepository.save(category);
        } catch (Exception ex) {
            // If save fails (e.g., DB connectivity), return the transient object so UI can still render
            return category;
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String submitRegister(@RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("password") String password,
                                 Model model) {
        try {
            AuthResponse response = authService.register(name, email, phone, password);
            if (response.isSuccess()) {
                return "redirect:/verify-otp?email=" + email + "&name=" + name;
            }
            model.addAttribute("message", response.getMessage());
            model.addAttribute("success", false);
        } catch (Exception ex) {
            model.addAttribute("message", ex.getMessage());
            model.addAttribute("success", false);
        }
        return "register";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(@RequestParam(value = "email", required = false) String email,
                            @RequestParam(value = "name", required = false) String name,
                            Model model) {
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        return "verify-otp";
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

    private void populateProductImages(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        for (Product product : products) {
            if (product == null || product.getId() == null) {
                continue;
            }
            List<com.localmart.product.ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId());
            if (images != null && !images.isEmpty()) {
                product.setImages(images);
                String imageUrl = images.stream()
                        .filter(image -> image != null && image.getImageUrl() != null && !image.getImageUrl().isBlank())
                        .map(com.localmart.product.ProductImage::getImageUrl)
                        .findFirst()
                        .orElse(null);
                product.setImagePath(imageUrl);
            } else if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            }
        }
    }

    private void applyOffers(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<com.localmart.offer.Offer> offers = offerRepository.findByActiveTrue();
        if (offers == null || offers.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Product product : products) {
            if (product == null || product.getId() == null) continue;
            Double bestOverride = product.getPriceOverride();
            for (com.localmart.offer.Offer offer : offers) {
                if (!offer.isActive()) continue;
                if (offer.getStartDate() != null && offer.getStartDate().isAfter(now)) continue;
                if (offer.getEndDate() != null && offer.getEndDate().isBefore(now)) continue;

                boolean applies = false;
                if (offer.getOfferType() == com.localmart.offer.Offer.OfferType.product && offer.getProductId() != null) {
                    applies = offer.getProductId().equals(product.getId());
                } else if (offer.getOfferType() == com.localmart.offer.Offer.OfferType.store && offer.getStore() != null && product.getShop() != null) {
                    applies = offer.getStore().getId().equals(product.getShop().getId());
                } else if (offer.getOfferType() == com.localmart.offer.Offer.OfferType.category && offer.getCategoryId() != null && product.getCategoryId() != null) {
                    applies = offer.getCategoryId().equals(product.getCategoryId());
                }

                if (!applies) continue;

                Double original = product.getPrice();
                if (original == null) continue;
                Double candidate;
                if (offer.getDiscountType() == com.localmart.offer.Offer.DiscountType.percentage) {
                    candidate = original * (1 - (offer.getDiscount() == null ? 0.0 : offer.getDiscount() / 100.0));
                } else {
                    candidate = original - (offer.getDiscount() == null ? 0.0 : offer.getDiscount());
                }
                if (candidate < 0) candidate = 0.0;
                if (bestOverride == null || candidate < bestOverride) {
                    bestOverride = Math.round(candidate * 100.0) / 100.0;
                }
            }
            product.setPriceOverride(bestOverride);
        }
    }

    private void recordSearchHistory(String query, int resultCount) {
        Optional<User> currentCustomer = getCurrentCustomer();
        if (currentCustomer.isEmpty()) {
            return;
        }
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setCustomerId(currentCustomer.get().getId());
        searchHistory.setSearchQuery(query);
        searchHistory.setFilters(null);
        searchHistory.setResultCount(resultCount);
        searchHistoryRepository.save(searchHistory);
    }

    @PostMapping("/verify-otp")
    public String submitVerifyOtp(@RequestParam("email") String email,
                                  @RequestParam("otp") String otp,
                                  Model model) {
        try {
            AuthResponse response = authService.verifyOtp(email, otp);
            if (response.isSuccess()) {
                return "redirect:/login?verified=true";
            }
            model.addAttribute("message", response.getMessage());
            model.addAttribute("success", false);
            model.addAttribute("email", email);
        } catch (Exception ex) {
            model.addAttribute("message", ex.getMessage());
            model.addAttribute("success", false);
            model.addAttribute("email", email);
        }
        return "verify-otp";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }
}
