package com.localmart.retailer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RetailerPageController {

    @GetMapping("/retailer/register")
    public String retailerRegister() {
        return "retailer/register";
    }

    @GetMapping("/retailer/verify")
    public String retailerVerify(@RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "name", required = false) String name,
                                 Model model) {
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        return "retailer/verify";
    }

    @GetMapping("/retailer/dashboard")
    public String retailerDashboard(@RequestParam(value = "name", required = false) String name,
                                    Model model) {
        model.addAttribute("name", name);
        return "retailer/dashboard";
    }

    @GetMapping("/retailer/shops")
    public String retailerShops() {
        return "retailer/shops";
    }

    @GetMapping("/retailer/products")
    public String retailerProducts() {
        return "retailer/products";
    }

    @GetMapping("/retailer/offers")
    public String retailerOffers() {
        return "retailer/offers";
    }

    @GetMapping("/retailer/analytics")
    public String retailerAnalytics() {
        return "retailer/analytics";
    }
}
