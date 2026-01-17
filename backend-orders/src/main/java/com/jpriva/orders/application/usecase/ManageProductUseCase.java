package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ProductDto;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.ports.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageProductUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public ProductDto.Response createProduct(ProductDto.CreateRequest request) {
        Product product = request.toDomain();
        Product savedProduct = productRepository.save(product);
        return ProductDto.Response.fromDomain(savedProduct);
    }

    @Transactional
    public ProductDto.Response updatePrice(UUID productId, ProductDto.UpdatePriceRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(ProductErrorCodes.PRODUCT_ID_NULL));
        
        Money money = Money.fromString(request.currencyCode(), request.price().toString());
        product.changePrice(money);
        
        Product savedProduct = productRepository.save(product);
        return ProductDto.Response.fromDomain(savedProduct);
    }

    @Transactional
    public ProductDto.Response increaseStock(UUID productId, int amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(ProductErrorCodes.PRODUCT_ID_NULL));

        product.increaseStock(amount);
        
        Product savedProduct = productRepository.save(product);
        return ProductDto.Response.fromDomain(savedProduct);
    }
    
    @Transactional(readOnly = true)
    public ProductDto.Response getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(ProductErrorCodes.PRODUCT_ID_NULL));
        return ProductDto.Response.fromDomain(product);
    }
}
