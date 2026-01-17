package com.jpriva.orders.domain.model.vo;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.MoneyErrorCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Currency {
    COP("COP", "Colombian Peso", "$"),
    USD("USD", "US Dollar", "$"),
    EUR("EUR", "Euro", "€"),
    GBP("GBP", "British Pound", "£"),
    JPY("JPY", "Japanese Yen", "¥");

    private final String code;
    private final String name;
    private final String symbol;

    public static Currency fromString(String code) {
        return Arrays.stream(Currency.values())
                .filter(currency ->
                        currency.code.equals(code)
                ).findFirst()
                .orElseThrow(() ->
                        new DomainException(
                                MoneyErrorCodes.MONEY_ERROR_CURRENCY_NOT_SUPPORTED,
                                "Currency not supported: " + code
                        )
                );
    }
}
