package com.localmart.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }
}
