package com.superkamal.service;


import com.superkamal.models.Product;
import com.superkamal.repositories.DiscountRepository;
import com.superkamal.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    public boolean addNewProduct(Product product) {
        if(product!=null) {
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public boolean existsByBarcode(String barcode) {
        return productRepository.existsById(barcode);
    }

    public Product getProductByBarcode(String barcode) {
        return productRepository.findById(barcode).orElse(null);
    }

    public Object getAllProducts() {
        return productRepository.findAll();
    }

    public boolean deleteByBarcode(String barcode) {
        if (productRepository.existsById(barcode)) {
            discountRepository.findByBarcode(barcode).ifPresent(discountRepository::delete);
            productRepository.deleteById(barcode);
            return true;
        }
        return false;
    }

}
