package pl.edu.agh.mwo.invoice;

import java.util.HashMap;

import java.util.Map;
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

    public Integer getTotalProductsNumber() {
        Integer totalProductsNumber = 0;
        for (Product product :products.keySet()) {
            Integer quantity = products.get(product);
            totalProductsNumber = totalProductsNumber + quantity;
        }
        return totalProductsNumber;
    }

    public String print() {
        String invoiceSummary = "Invoice Number: " + getInvoiceNumber() + "\n"
                + "Product, Amount, Tax, Netto Price, Netto Value" + "\n\n";

        Map<String, Integer> productNameToQuantity = new HashMap<>();

        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            String productName = product.getName();

            productNameToQuantity.put(productName,
                    productNameToQuantity.getOrDefault(productName, 0) + quantity);
        }

        StringBuilder detail = new StringBuilder();
        for (Map.Entry<String, Integer> entry : productNameToQuantity.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();

            double totalForProduct = getProductTotal(productName, quantity);

            detail.append(productName)
                    .append(", ").append(quantity)
                    .append(", ").append(getProductTaxPercent(productName))
                    .append(", ").append(getProductPrice(productName))
                    .append(", ").append(totalForProduct)
                    .append("\n");
        }

        String totalItems = "\n" + "Total items: " + productNameToQuantity.size();

        String totalProducts = "Total Products: " + getTotalProductsNumber();

        String totalAmountDetail = String.format("Total Gross Amount: "
                + getGrossTotal().doubleValue() + "\n"
                + "Total Netto Amount: " + getNetTotal().doubleValue() + "\n"
                + "Total Tax Amount: " + getTaxTotal().doubleValue());



        return invoiceSummary + detail + "\n" + totalItems
                + "\n" + totalProducts + "\n" + totalAmountDetail;
    }

    private double getProductTotal(String productName, int quantity) {
        for (Product product : products.keySet()) {
            if (product.getName().equals(productName)) {
                return product.getPrice().multiply(BigDecimal.valueOf(quantity)).doubleValue();
            }
        }
        return 0.0; // Handling case where the product is not found
    }

    private double getProductPrice(String productName) {
        for (Product product : products.keySet()) {
            if (product.getName().equals(productName)) {
                return product.getPrice().doubleValue();
            }
        }
        return 0.0; // Handling case where the product is not found
    }

    private double getProductTaxPercent(String productName) {
        for (Product product : products.keySet()) {
            if (product.getName().equals(productName)) {
                return product.getTaxPercent().doubleValue();
            }
        }
        return 0.0; // Handling case where the product is not found
    }

}