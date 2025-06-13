package com.superkamal.controllers;

import com.superkamal.models.Product;
import com.superkamal.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/addProduct")
    public String showForm(Model model,
                           @RequestParam(required = false) String barcode) {
        Product product = (barcode != null && productService.existsByBarcode(barcode)) ?
                productService.getProductByBarcode(barcode) : new Product();

        model.addAttribute("product", product);
        return "add-product.html";
    }

    @PostMapping("/addProduct")
    public String addOrUpdateProduct(@ModelAttribute @Valid Product product,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors() || product.getPrice() <= 0 || product.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("message", "⚠️ שגיאה בהזנת שדות – יש לוודא שמחיר וכמות גדולים מ־0");
            redirectAttributes.addFlashAttribute("alert", "error");
            return "redirect:/addProduct";
        }

        boolean exists = productService.existsByBarcode(product.getBarcode());
        productService.addNewProduct(product);

        if (exists) {
            redirectAttributes.addFlashAttribute("message", "✅ המוצר עודכן בהצלחה");
        } else {
            redirectAttributes.addFlashAttribute("message", "✅ המוצר נוסף בהצלחה");
        }

        redirectAttributes.addFlashAttribute("alert", "success");
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(Model model,
                               @ModelAttribute("message") String message,
                               @ModelAttribute("alert") String alert) {
        model.addAttribute("products", productService.getAllProducts());

        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
            model.addAttribute("alert", alert);
        }

        return "products";
    }

    @GetMapping("/editProduct/{barcode}")
    public String editProduct(@PathVariable String barcode,
                              Model model,
                              RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        Product product = productService.getProductByBarcode(barcode);
        if (product == null) {
            redirectAttributes.addFlashAttribute("message", "⚠️ המוצר לא נמצא");
            redirectAttributes.addFlashAttribute("alert", "error");
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        return "add-product.html";
    }

    @GetMapping("/deleteProduct/{barcode}")
    public String deleteProduct(@PathVariable String barcode,
                                RedirectAttributes redirectAttributes) {
        productService.deleteByBarcode(barcode);
        redirectAttributes.addFlashAttribute("message", "🗑️ המוצר נמחק בהצלחה");
        redirectAttributes.addFlashAttribute("alert", "success");
        return "redirect:/products";
    }
}
