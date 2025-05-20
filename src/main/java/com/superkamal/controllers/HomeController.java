package com.superkamal.controllers;

import org.springframework.ui.Model;
import com.superkamal.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@Slf4j
public class HomeController {

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("cart", cartService.getActiveCart());
        return "home";
    }
}




