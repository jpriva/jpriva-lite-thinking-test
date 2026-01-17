package com.jpriva.orders.domain.model.vo;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.MoneyErrorCodes;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(
        Currency currency,
        BigDecimal amount
) {

    public static RoundingMode defaultRoundingMode = RoundingMode.HALF_UP;

    public Money {
        if (currency == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_CURRENCY);
        }
        if (amount == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT, "Amount cannot be negative");
        }
    }

    public static Money zero(Currency currency) {
        if (currency == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_CURRENCY);
        }
        return new Money(currency, BigDecimal.ZERO).round(2);
    }

    public static Money from(Currency currency, BigDecimal amount){
        return new Money(currency,amount).round(2);
    }

    public static Money fromString(String currencyCode, String amount) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_CURRENCY);
        }
        if (amount == null || amount.isBlank()) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        Currency currency = Currency.fromString(currencyCode);
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT, "Invalid amount format: " + amount, e);
        }
        return new Money(currency, amountValue).round(2);
    }

    public static Money fromString(Currency currency, String amount) {
        if (currency == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_CURRENCY);
        }
        if (amount == null || amount.isBlank()) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT, "Invalid amount format: " + amount, e);
        }
        return new Money(currency, amountValue).round(2);
    }

    public Money add(Money money) {
        if (money == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        if (!this.currency.equals(money.currency)) {
            throw new DomainException(
                    MoneyErrorCodes.MONEY_ERROR_CURRENCY,
                    "Currencies do not match: " + this.currency + " and " + money.currency
            );
        }
        return new Money(this.currency, this.amount.add(money.amount));
    }

    public Money subtract(Money money) {
        if (money == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        if (!this.currency.equals(money.currency)) {
            throw new DomainException(
                    MoneyErrorCodes.MONEY_ERROR_CURRENCY,
                    "Currencies do not match: " + this.currency + " and " + money.currency
            );
        }
        return new Money(this.currency, this.amount.subtract(money.amount));
    }

    public Money multiply(BigDecimal multiplier){
        if (multiplier == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        return new Money(this.currency, this.amount.multiply(multiplier)).round(2);
    }

    public Money multiply(int multiplier){
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public Money divide(BigDecimal divisor){
        if (divisor == null) {
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT);
        }
        if (divisor.compareTo(BigDecimal.ZERO) == 0){
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT, "Cannot divide by zero");
        }
        return new Money(this.currency, this.amount.divide(divisor, 2, defaultRoundingMode));
    }

    public Money round(int scale){
        if (scale < 0){
            throw new DomainException(MoneyErrorCodes.MONEY_ERROR_AMOUNT, "Scale must be greater or equal than 0");
        }
        return new Money(this.currency, this.amount.setScale(scale, defaultRoundingMode));
    }

    public Money changeAmount(BigDecimal amount){
        return new Money(this.currency, amount);
    }

}
