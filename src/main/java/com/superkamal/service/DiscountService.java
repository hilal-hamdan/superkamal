// DiscountService.java
package com.superkamal.service;

import com.superkamal.models.Discount;
import com.superkamal.models.DiscountType;
import com.superkamal.models.Product;
import com.superkamal.repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public void prepareAndSaveDiscount(Discount discount) {
        if (discount.getType() == DiscountType.PRICE_DROP) {
            discount.setBundleQuantity(null);
            discount.setBundlePrice(null);
        } else if (discount.getType() == DiscountType.BUNDLE) {
            discount.setNewUnitPrice(null);
        }
        discountRepository.save(discount);
    }

    public Discount findById(Long id) {
        return discountRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        discountRepository.deleteById(id);
    }

    public Discount findByBarcodeOrCreate(String barcode) {
        return discountRepository.findByBarcode(barcode)
                .orElseGet(() -> {
                    Discount d = new Discount();
                    d.setBarcode(barcode);
                    return d;
                });
    }
}
