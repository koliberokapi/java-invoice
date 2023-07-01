package pl.edu.agh.mwo.invoice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private static int invoiceCounter = 0; // to generate unique invoice numbers
    private int invoiceNumber = ++invoiceCounter;
    private Map<Product, Integer> products = new HashMap<>();

    public int getInvoiceNumber() {
        if (invoiceNumber <= 0) {
            invoiceNumber = ++invoiceCounter;
        }
        return invoiceNumber;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.put(product, quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public BigDecimal getTotalProductsNumber() {
        BigDecimal totalProductsNumber = BigDecimal.ZERO;
        for (Product product :products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalProductsNumber = totalProductsNumber.add(quantity);
        }
        return totalProductsNumber;
    }

    public  Map<Product, Integer> uniqueProducts() {
        return new HashMap<>();
    }



    public String print() {
        String invoiceSummary = "Invoice Number: " + getInvoiceNumber() + "\n"
                + "Product, Amount, Tax, Netto Price, Netto Value" + "\n\n";


        String productsDetail = products.entrySet().stream()
                .map(entry -> entry.getKey().getName()
                        + ", " + entry.getValue()
                        + ", " + entry.getKey().getTaxPercent().doubleValue()
                        + ", " + entry.getKey().getPrice().doubleValue()
                        + ", " + entry.getKey().getPrice().doubleValue()
                                * entry.getValue().doubleValue())
                .sorted()
                .collect(Collectors.joining("\n"));

        String totalItems = "\n\n" + "Total items: " + products.size();

        String totalProducts = "Total Products: " + getTotalProductsNumber().intValueExact();

        String totalAmountDetail = String.format("Total Gross Amount: "
                + getGrossTotal().doubleValue() + "\n"
                + "Total Netto Amount: " + getNetTotal().doubleValue() + "\n"
                + "Total Tax Amount: " + getTaxTotal().doubleValue());



        return invoiceSummary + productsDetail + "\n" + totalItems
                + "\n" + totalProducts + "\n" + totalAmountDetail;
    }

}