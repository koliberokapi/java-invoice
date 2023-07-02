package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoicesForTheTest() {

        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertEquals(BigDecimal.ZERO, invoice.getNetTotal());
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertEquals(BigDecimal.ZERO, invoice.getTaxTotal());
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertEquals(BigDecimal.ZERO, invoice.getGrossTotal());
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("15"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertEquals(new BigDecimal("25"), invoice.getNetTotal());
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertEquals(new BigDecimal("1000"), invoice.getNetTotal());
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertEquals(invoice.getNetTotal(), invoice.getGrossTotal());
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertEquals(new BigDecimal("310"),invoice.getNetTotal());
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertEquals(new BigDecimal("10.30"), invoice.getTaxTotal());
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertEquals(new BigDecimal("320.30"), invoice.getGrossTotal());
    }

    @Test
    public void testInvoiceHasProperSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertEquals(new BigDecimal("50.00"), invoice.getNetTotal());
    }

    @Test
    public void testInvoiceHasProperTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertEquals(new BigDecimal("54.7000"),invoice.getGrossTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceNumber() {
        Assert.assertTrue(invoice.getInvoiceNumber() >= 0);
    }
    @Test
    public void testDifferenceBetweenTwoInvoicesNumbers() {
        Invoice secondInvoice = new Invoice();
        Assert.assertSame(1, secondInvoice.getInvoiceNumber() - invoice.getInvoiceNumber());
    }
    @Test
    public void testInvoicePrintingWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("15"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertEquals("Invoice Number: 13\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Warzywa, 1, 0.0, 0.0, 10.0, 10.0\n" +
                "Owoce, 1, 0.0, 0.0, 15.0, 15.0\n" +
                "\n" +
                "\n" +
                "Total items: 2\n" +
                "Total Products: 2\n" +
                "Total Gross Amount: 25.0\n" +
                "Total Netto Amount: 25.0\n" +
                "Total Tax Amount: 0.0", invoice.print());
    }

    @Test
    public void testInvoicePrintingWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertEquals("Invoice Number: 26\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Warzywa, 100, 0.0, 0.0, 10.0, 1000.0\n" +
                "\n" +
                "\n" +
                "Total items: 1\n" +
                "Total Products: 100\n" +
                "Total Gross Amount: 1000.0\n" +
                "Total Netto Amount: 1000.0\n" +
                "Total Tax Amount: 0.0", invoice.print());
    }

    @Test
    public void testInvoicePrintigWithTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertEquals("Invoice Number: 15\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Warzywa, 1, 0.0, 0.0, 199.99, 199.99\n" +
                "\n" +
                "\n" +
                "Total items: 1\n" +
                "Total Products: 1\n" +
                "Total Gross Amount: 199.99\n" +
                "Total Netto Amount: 199.99\n" +
                "Total Tax Amount: 0.0", invoice.print());
    }

    @Test
    public void testInvoicePrintingForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertEquals("Invoice Number: 27\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Maslanka, 1, 0.08, 0.0, 100.0, 100.0\n" +
                "Owoce, 1, 0.0, 0.0, 200.0, 200.0\n" +
                "Wino, 1, 0.23, 0.0, 10.0, 10.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 3\n" +
                "Total Gross Amount: 320.3\n" +
                "Total Netto Amount: 310.0\n" +
                "Total Tax Amount: 10.3",invoice.print());
    }

    @Test
    public void testInvoicePrintingWithProperValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertEquals("Invoice Number: 21\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Pampersy, 1, 0.0, 0.0, 200.0, 200.0\n" +
                "Kefir, 1, 0.08, 0.0, 100.0, 100.0\n" +
                "Piwko, 1, 0.23, 0.0, 10.0, 10.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 3\n" +
                "Total Gross Amount: 320.3\n" +
                "Total Netto Amount: 310.0\n" +
                "Total Tax Amount: 10.3", invoice.print());
    }

    @Test
    public void testInvoicePrintingWithTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertEquals("Invoice Number: 16\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Maskotki, 1, 0.0, 0.0, 200.0, 200.0\n" +
                "Maslo, 1, 0.08, 0.0, 100.0, 100.0\n" +
                "Chipsy, 1, 0.23, 0.0, 10.0, 10.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 3\n" +
                "Total Gross Amount: 320.3\n" +
                "Total Netto Amount: 310.0\n" +
                "Total Tax Amount: 10.3", invoice.print());
    }

    @Test
    public void testInvoicePrintingSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertEquals("Invoice Number: 19\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Kozi Serek, 3, 0.08, 0.0, 10.0, 30.0\n" +
                "Kubek, 2, 0.0, 0.0, 5.0, 10.0\n" +
                "Pinezka, 1000, 0.23, 0.0, 0.01, 10.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 1005\n" +
                "Total Gross Amount: 54.7\n" +
                "Total Netto Amount: 50.0\n" +
                "Total Tax Amount: 4.7", invoice.print());
    }

    @Test
    public void testInvoicePrintingWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertEquals("Invoice Number: 1\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Chleb, 2, 0.0, 0.0, 5.0, 10.0\n" +
                "Chedar, 3, 0.08, 0.0, 10.0, 30.0\n" +
                "Pinezka, 1000, 0.23, 0.0, 0.01, 10.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 1005\n" +
                "Total Gross Amount: 54.7\n" +
                "Total Netto Amount: 50.0\n" +
                "Total Tax Amount: 4.7", invoice.print());
    }

    @Test
    public void testInvoicePrintingWithDoubledProductsOfQuantityMoreThanOne() {
            // 500x pinezka - price with tax: 12.30
            invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 500);
            // 7x chedar - price with tax: 32.40
            invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 7);
            // 3x chleb - price with tax: 10
            invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 3);
            // 3x chedar - price with tax: 32.40
            invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
            // 1000x pinezka - price with tax: 12.30
            invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
            // 3x chleb - price with tax: 10
            invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
            Assert.assertEquals("Invoice Number: 24\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                    "\n" +
                "Chleb, 5, 0.0, 0.0, 5.0, 25.0\n" +
                "Chedar, 10, 0.08, 0.0, 10.0, 100.0\n" +
                "Pinezka, 1500, 0.23, 0.0, 0.01, 15.0\n" +
                "\n" +
                "\n" +
                "Total items: 3\n" +
                "Total Products: 1515\n" +
                "Total Gross Amount: 151.45\n" +
                "Total Netto Amount: 140.0\n" +
                "Total Tax Amount: 11.45",invoice.print());
    }
    @Test
    public void testInvoicePrintingWithAkcyzaDT() {
        //10x oil
        invoice.addProduct(new FuelCanisterDT("Oil", new BigDecimal("5.5")),10);
        Assert.assertEquals("Invoice Number: 23\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Oil, 10, 0.0, 0.0, 5.5, 55.0\n" +
                "\n" +
                "\n" +
                "Total items: 1\n" +
                "Total Products: 10\n" +
                "Total Gross Amount: 55.0\n" +
                "Total Netto Amount: 55.0\n" +
                "Total Tax Amount: 0.0",invoice.print());
    }
    @Test
    public void testInvoicePrintingWithAkcyzaTwoProducts() {
        //10x oil
        invoice.addProduct(new FuelCanister("Oil", new BigDecimal("5.5")),10);
        //1x Champgne
        invoice.addProduct(new BottleofWine("Champagne", new BigDecimal("100")),1);
        Assert.assertEquals("Invoice Number: 17\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                "\n" +
                "Oil, 10, 0.8, 5.56, 5.5, 55.0\n" +
                "Champagne, 1, 0.23, 5.56, 100.0, 100.0\n" +
                "\n" +
                "\n" +
                "Total items: 2\n" +
                "Total Products: 11\n" +
                "Total Gross Amount: 283.16\n" +
                "Total Netto Amount: 155.0\n" +
                "Total Tax Amount: 128.16",invoice.print());
    }

    @Test
    public void testInvoicePrintingWithDoubledProductsOfQuantityMoreThanOneWithAkcyza() {
            //10x merlot
            invoice.addProduct(new BottleofWine("Merlot", new BigDecimal("20.00")), 10);
            //10x oil
            invoice.addProduct(new FuelCanister("Oil", new BigDecimal("5.5")),10);
            // 500x pinezka - price with tax: 12.30
            invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 500);
            // 7x chedar - price with tax: 32.40
            invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 7);
            // 3x chleb - price with tax: 10
            invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 3);
            // 3x chedar - price with tax: 32.40
            invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
            // 1000x pinezka - price with tax: 12.30
            invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
            // 3x chleb - price with tax: 10
            invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
            Assert.assertEquals("Invoice Number: 28\n" +
                "Product, Amount, Tax, Akcyza, Netto Price, Netto Value\n" +
                    "\n" +
                "Oil, 10, 0.8, 5.56, 5.5, 55.0\n" +
                "Chleb, 5, 0.0, 0.0, 5.0, 25.0\n" +
                "Chedar, 10, 0.08, 0.0, 10.0, 100.0\n" +
                "Pinezka, 1500, 0.23, 0.0, 0.01, 15.0\n" +
                "Merlot, 10, 0.23, 5.56, 20.0, 200.0\n" +
                "\n" +
                "\n" +
                "Total items: 5\n" +
                "Total Products: 1535\n" +
                "Total Gross Amount: 607.65\n" +
                "Total Netto Amount: 395.0\n" +
                "Total Tax Amount: 212.65",invoice.print());
    }

}
