package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class FuelCanisterDT extends Product {
    public FuelCanisterDT(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.0"), new BigDecimal("0.00"));
    }
}
