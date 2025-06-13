package com.superkamal.controllers;

import com.superkamal.models.Product;
import com.superkamal.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    // הצגת טופס הוספת מוצר
    @GetMapping("/addProduct")
    public String showForm(Model model,
                           @RequestParam(required = false) String message,
                           @RequestParam(required = false) String alert,
                           @RequestParam(required = false) String error,
                           @RequestParam(required = false) String barcode) {
        Product product = (barcode != null && productService.existsByBarcode(barcode)) ?
                productService.getProductByBarcode(barcode) : new Product();
        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("alert", alert);
        model.addAttribute("error", error);
        return "add-product.html";
    }

    // שמירת מוצר חדש
    @PostMapping("/addProduct")
    public String saveProduct(@ModelAttribute("product") Product product) throws UnsupportedEncodingException {
        if (productService.existsByBarcode(product.getBarcode())) {
            return "redirect:/editProduct/" + product.getBarcode();
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            String error = URLEncoder.encode("שם המוצר לא יכול להיות ריק", StandardCharsets.UTF_8);
            return "redirect:/addProduct?error=" + error + "&barcode=" + product.getBarcode();
        }

        productService.addNewProduct(product);
        String msg = URLEncoder.encode("המוצר נוסף בהצלחה!", StandardCharsets.UTF_8);
        return "redirect:/addProduct?message=" + msg + "&alert=success";
    }

    // הצגת כל המוצרים
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    // מעבר לעמוד עריכה לפי ברקוד מה-URL
    @GetMapping("/editProduct/{barcode}")
    public String editProduct(@PathVariable String barcode, Model model,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) String alert) throws UnsupportedEncodingException {
        Product product = productService.getProductByBarcode(barcode);
        if (product == null) {
            String msg = URLEncoder.encode("המוצר לא נמצא", StandardCharsets.UTF_8);
            return "redirect:/products?message=" + msg + "&alert=error";
        }

        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("alert", alert);
        return "update-product.html";
    }

    // אם הגיעו ל־editProduct ללא ברקוד – נבדוק האם קיים ונעביר הלאה
    @GetMapping("/editProduct")
    public String redirectToEditProduct(@RequestParam String barcode) throws UnsupportedEncodingException {
        if (productService.existsByBarcode(barcode)) {
            return "redirect:/editProduct/" + barcode;
        }
        String msg = URLEncoder.encode("המוצר עם ברקוד " + barcode + " לא נמצא", StandardCharsets.UTF_8);
        return "redirect:/manageProducts?notFound=true&barcode=" + barcode + "&errorMessage=" + msg;
    }

    // שמירת עדכון למוצר
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product) throws UnsupportedEncodingException {
        productService.addNewProduct(product);
        String msg = URLEncoder.encode("המוצר עודכן בהצלחה!", StandardCharsets.UTF_8);
        return "redirect:/products?message=" + msg + "&alert=success";
    }

    // עמוד ניהול מוצרים – עם טיפול במוצר שלא נמצא
    @GetMapping("/manageProducts")
    public String manageProducts(@RequestParam(required = false) String notFound,
                                 @RequestParam(required = false) String barcode,
                                 @RequestParam(required = false) String errorMessage,
                                 Model model) {
        if ("true".equals(notFound) && barcode != null) {
            model.addAttribute("errorMessage", errorMessage != null ? errorMessage : "המוצר לא נמצא");
        }
        return "manage-products";
    }

    // מחיקת מוצר לפי ברקוד
    @GetMapping("/deleteProduct/{barcode}")
    public String deleteProduct(@PathVariable String barcode) throws UnsupportedEncodingException {
        productService.deleteByBarcode(barcode);
        String msg = URLEncoder.encode("המוצר נמחק בהצלחה", StandardCharsets.UTF_8);
        return "redirect:/products?message=" + msg + "&alert=success";
    }
}
