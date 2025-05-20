// Updated DiscountController.java
package com.superkamal.controllers;

import com.superkamal.models.Discount;
import com.superkamal.models.DiscountType;
import com.superkamal.models.Product;
import com.superkamal.service.DiscountService;
import com.superkamal.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showAllDiscounts(Model model) {
        List<Discount> discounts = discountService.getAllDiscounts();
        model.addAttribute("discounts", discounts);

        // Map ברקוד → שם מוצר
        Map<String, String> barcodeToName = new HashMap<>();
        for (Discount d : discounts) {
            Product p = productService.getProductByBarcode(d.getBarcode());
            if (p != null) {
                barcodeToName.put(d.getBarcode(), p.getName());
            }
        }
        model.addAttribute("barcodeToName", barcodeToName);

        return "discounts";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("discount", new Discount());
        model.addAttribute("types", DiscountType.values());
        return "add-edit-discount";
    }

    @PostMapping("/save")
    public String saveDiscount(@ModelAttribute Discount discount) {
        discountService.prepareAndSaveDiscount(discount);
        return "redirect:/discounts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Discount discount = discountService.findById(id);
        if (discount == null) return "redirect:/discounts";

        model.addAttribute("discount", discount);
        model.addAttribute("types", DiscountType.values());

        Product product = productService.getProductByBarcode(discount.getBarcode());
        if (product != null) model.addAttribute("product", product);

        return "add-edit-discount";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {
        discountService.delete(id);
        return "redirect:/discounts";
    }

    @GetMapping("/find")
    public String findDiscountByBarcode(@RequestParam String barcode, Model model) {
        Discount discount = discountService.findByBarcodeOrCreate(barcode);
        model.addAttribute("discount", discount);
        model.addAttribute("types", DiscountType.values());

        Product product = productService.getProductByBarcode(barcode);
        if (product != null) {
            model.addAttribute("product", product);
        } else {
            model.addAttribute("productNotFound", true);
        }

        model.addAttribute("isNew", discount.getId() == null);
        return "add-edit-discount";
    }
}
