package pl.edu.agh.mwo.invoice;

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

    public String print() {
        String invoiceSummary = "Invoice Number: " + getInvoiceNumber() + "\n"
                + "Product, Amount, Tax, Neto Price, Netto Value" + "\n\n";

        String productsDetail = products.entrySet().stream()
                .map(entry -> entry.getKey().getName()
                        + ", " + entry.getValue().doubleValue()
                        + ", " + entry.getKey().getPrice().doubleValue()
                        + ", " + entry.getKey().getPrice().doubleValue()
                        * entry.getValue().doubleValue())
                .sorted()
                .collect(Collectors.joining("\n"));

        String totalProducts = "\n\n" + "Total Products: " + products.size();

        String totalAmountDetail = "Total Gross Amount: " + getGrossTotal().doubleValue() + "\n"
                + "Total Netto Amount: " + getNetTotal().doubleValue() +  "\n"
                + "Total Tax Amount: " + getTaxTotal().doubleValue();



        return invoiceSummary + productsDetail + "\n" + totalProducts + "\n" + totalAmountDetail;
    }

}