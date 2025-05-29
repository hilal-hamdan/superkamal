package com.superkamal.controllers;

import com.superkamal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchase")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/")
    public String showCart(Model model) {
        model.addAttribute("cart", cartService.getCurrentCart());
        return "home";
    }

    @GetMapping("/view")
    public String viewCart(Model model) {
        model.addAttribute("cart", cartService.getCurrentCart());
        return "cart-view";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String barcode) {
        cartService.addProductToCart(barcode);
        messagingTemplate.convertAndSend("/topic/cart", cartService.getCartItemDTOs());
        return "redirect:/";
    }



    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/purchase/view";
    }
}
