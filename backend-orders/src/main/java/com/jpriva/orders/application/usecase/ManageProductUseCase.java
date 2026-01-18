package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ProductDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ProductErrorCodes;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.model.Product;
import com.jpriva.orders.domain.model.vo.Money;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import com.jpriva.orders.domain.ports.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageProductUseCase {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public ProductDto.Response createProduct(ProductDto.CreateRequest request) {
        Company company = companyRepository.findByTaxId(request.companyId())
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        Product product = Product.create(company.getId(), request.categoryId(), request.name(), request.sku(), request.description());
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

    @Transactional(readOnly = true)
    public List<ProductDto.Response> getAllProduct(String taxId) {
        Company company = companyRepository.findByTaxId(taxId)
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        List<Product> products = productRepository.findByCompanyId(company.getId());
        return products.stream().map(ProductDto.Response::fromDomain).toList();
    }
}
