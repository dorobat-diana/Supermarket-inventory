package sales;

import products.Product;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryManager {
    private Map<String, Product> products;
    private SalesRecord salesRecord;
    private double totalMoney;
    private ReentrantLock moneyLock;
    private Map<String, Integer> initialQuantities;

    public InventoryManager(List<Product> initialProducts) {
        this.products = new HashMap<>();
        this.initialQuantities = new HashMap<>();
        for (Product product : initialProducts) {
            products.put(product.getName(), product);
            initialQuantities.put(product.getName(), product.getQuantity());
        }
        this.salesRecord = new SalesRecord();
        this.totalMoney = 0.0;
        this.moneyLock = new ReentrantLock();
    }

    public void performSale(Map<Product, Integer> productsToSell) {
        //System.out.println("Sale thread started: " + Thread.currentThread().getName());
        Bill bill = new Bill();

        for (Map.Entry<Product, Integer> entry : productsToSell.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            product.lock();
            try {
                 bill.addProduct(product, quantity);
            } catch (Exception e) {
                System.out.println("Error adding product " + product.getName() + " to bill: " + e.getMessage());
            }
        }

        try {
            addBillAndUpdateMoney(bill);
        } finally {
            for (Product product : productsToSell.keySet()) {
                product.unlock();
            }
        }

        //System.out.println("Sale thread finished: " + Thread.currentThread().getName());
    }



    private void addBillAndUpdateMoney(Bill bill) {
        salesRecord.addBillToRecord(bill);
        moneyLock.lock();
        try {
            totalMoney += bill.getTotalPrice();
        } finally {
            moneyLock.unlock();
        }
    }

    public void performInventoryCheck() {
        double calculatedTotalMoney = calculateTotalMoney();
        checkTotalMoney(calculatedTotalMoney);
        checkProductQuantities();
        System.out.println("Inventory Check Complete.");
    }

    public void printSalesRecord() {
        salesRecord.printSalesRecord();
    }

    private double calculateTotalMoney() {
        double calculatedTotalMoney = 0.0;
        List<Bill> bills = salesRecord.getBills();

        for (Bill bill : bills) {
            calculatedTotalMoney += bill.getTotalPrice();
        }

        return calculatedTotalMoney;
    }

    private void checkTotalMoney(double calculatedTotalMoney) {
        if (calculatedTotalMoney != totalMoney) {
            System.out.println("Inventory Check Failed: Total money does not match.");
            System.out.println("Calculated Total Money: " + calculatedTotalMoney);
            System.out.println("Total Money: " + totalMoney);
        } else {
            System.out.println("Total money matches.");
        }
    }

    private void checkProductQuantities() {
        Map<String, Integer> totalQuantitiesSold = calculateTotalQuantitiesSold();
        validateProductQuantities(totalQuantitiesSold);
    }

    private Map<String, Integer> calculateTotalQuantitiesSold() {
        Map<String, Integer> totalQuantitiesSold = new HashMap<>();
        List<Bill> bills = salesRecord.getBills();

        for (Bill bill : bills) {
            List<Product> productsSold = bill.getProductsSold();
            List<Integer> quantitiesSold = bill.getQuantitiesSold();
            for (int i = 0; i < productsSold.size(); i++) {
                Product product = productsSold.get(i);
                int quantity = quantitiesSold.get(i);
                totalQuantitiesSold.put(
                        product.getName(),
                        totalQuantitiesSold.getOrDefault(product.getName(), 0) + quantity
                );
            }
        }

        return totalQuantitiesSold;
    }

    private void validateProductQuantities(Map<String, Integer> totalQuantitiesSold) {
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            String productName = entry.getKey();
            Product product = entry.getValue();
            int initialQuantity = initialQuantities.get(productName);
            int soldQuantity = totalQuantitiesSold.getOrDefault(productName, 0);
            int currentQuantity = initialQuantity - soldQuantity;

            if (product.getQuantity() != currentQuantity) {
                System.out.println("Inventory Check Failed for " + productName + ": Quantity mismatch.");
                System.out.println("Current Quantity: " + currentQuantity);
                System.out.println("Expected Quantity: " + product.getQuantity());
            }
        }
    }
}
