package com.superkamal.service;

import com.superkamal.dto.CartItemDTO;
import com.superkamal.models.*;
import com.superkamal.repositories.CartRepository;
import com.superkamal.repositories.DiscountRepository;
import com.superkamal.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    private Cart activeCart;

    public Cart getActiveCart() {
        if (activeCart == null) {
            activeCart = new Cart();
        }
        return activeCart;
    }

    public void addProductToCart(String barcode) {
        Product product = productRepository.findById(barcode).orElse(null);
        if (product == null) return;

        Cart cart = getActiveCart();

        // חיפוש אם המוצר כבר קיים בסל
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getBarcode().equals(barcode))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(1);
            item.setCart(cart);
            cart.addItem(item);
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }

        // ערכי ברירת מחדל
        double unitPrice = product.getPrice();
        double totalPrice = unitPrice * item.getQuantity();
        item.setDiscountApplied(false);
        item.setOriginalPrice(totalPrice);
        item.setDiscountDescription(null);

        // חישוב מבצעים אם קיימים
        Optional<Discount> optionalDiscount = discountRepository.findByBarcode(barcode);

        if (optionalDiscount.isPresent()) {
            Discount discount = optionalDiscount.get();

            if (discount.getType() == DiscountType.PRICE_DROP && discount.getNewUnitPrice() > 0) {
                totalPrice = discount.getNewUnitPrice() * item.getQuantity();
                item.setDiscountApplied(true);
                item.setOriginalPrice(unitPrice * item.getQuantity());
                item.setDiscountDescription("מחיר מבצע ליחידה");
            } else if (discount.getType() == DiscountType.BUNDLE &&
                    discount.getBundleQuantity() > 0 && discount.getBundlePrice() > 0) {
                int bundles = item.getQuantity() / discount.getBundleQuantity();
                int remainder = item.getQuantity() % discount.getBundleQuantity();

                double bundlePart = discount.getBundlePrice() * bundles;
                double regularPart = unitPrice * remainder;

                totalPrice = bundlePart + regularPart;

                if (bundles > 0) {
                    item.setDiscountApplied(true);
                    item.setOriginalPrice(unitPrice * item.getQuantity());
                    item.setDiscountDescription("מבצע " + discount.getBundleQuantity() + " ב־" + discount.getBundlePrice() + " ₪");
                }
            }
        }

        item.setTotalPrice(totalPrice);

        // עדכון סך הסל
        double cartTotal = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        cart.setTotal(cartTotal);

        cartRepository.save(cart);
    }

    public void clearCart() {
        if (activeCart != null) {
            activeCart.getItems().clear();
            activeCart.setTotal(0);

            cartRepository.save(activeCart);
        }
    }

    public Cart getCurrentCart() {
        return getActiveCart();
    }

    public CartItemDTO mapToDTO(CartItem item) {
        return new CartItemDTO(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getTotalPrice()
        );
    }

    public List<CartItemDTO> getCartItemDTOs() {
        return getActiveCart().getItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }




}
