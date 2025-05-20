package com.superkamal.controllers;

import com.superkamal.models.Product;
import com.superkamal.repositories.ProductRepository;
import com.superkamal.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/addProduct")
    public String showForm(Model model,
                           @RequestParam(required = false) String message,
                           @RequestParam(required = false) String alert,
                           @RequestParam(required = false) String barcode) {
        Product product = (barcode != null && productService.existsByBarcode(barcode)) ?
                productService.getProductByBarcode(barcode) : new Product();
        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("alert", alert); // "success" or "error"
        return "add-product.html";
    }

    @PostMapping("/addProduct")
    public String saveProduct(@ModelAttribute("product") Product product) {
        if (productService.existsByBarcode(product.getBarcode())) {
            return "redirect:/editProduct/" + product.getBarcode();
        }
        productService.addNewProduct(product);
        return "redirect:/addProduct?message=המוצר נוסף בהצלחה!&alert=success";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/editProduct/{barcode}")
    public String editProduct(@PathVariable String barcode, Model model,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) String alert) {
        Product product = productService.getProductByBarcode(barcode);
        if (product == null) {
            return "redirect:/products?message=המוצר לא נמצא&alert=error";
        }
        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("alert", alert);
        return "update-product.html";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product) {
        productService.addNewProduct(product);
        return "redirect:/products?message=המוצר עודכן בהצלחה!&alert=success";
    }

    @GetMapping("/manageProducts")
    public String manageProducts() {
        return "manage-products";
    }

    @GetMapping("/deleteProduct/{barcode}")
    public String deleteProduct(@PathVariable String barcode) {
        productService.deleteByBarcode(barcode);
        return "redirect:/products?message=Product deleted successfully&alert=success";
    }

}
