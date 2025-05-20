package com.superkamal.controllers;

import com.superkamal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchase")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cart", cartService.getCurrentCart());
        return "home";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String barcode) {
        cartService.addProductToCart(barcode);
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/";
    }
}
