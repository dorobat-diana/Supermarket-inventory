import products.*;
import sales.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int MIN_SALE_QUANTITY = 1;
    private static final int MAX_SALE_QUANTITY = 10;
    //private static final int MIN_SLEEP_TIME = 1;
    //private static final int MAX_SLEEP_TIME = 10;
    private static final int MAX_SIMULATED_SALES = 10;

    public static void main(String[] args) {
        List<Product> initialProducts = Arrays.asList(
                new Hat(),
                new Jacket(),
                new Pants(),
                new Shirt(),
                new Shoes()
        );

        long startTime = System.currentTimeMillis();

        printProductDetails(initialProducts);

        InventoryManager inventoryManager = new InventoryManager(initialProducts);

        inventoryManager.performInventoryCheck();

        List<Thread> saleThreads = simulateRandomConcurrentSales(inventoryManager, initialProducts);

        simulateInventoryChecks(inventoryManager);

        for (Thread thread : saleThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("Performing final inventory check:");

        inventoryManager.performInventoryCheck();

        inventoryManager.printSalesRecord();

        printProductDetails(initialProducts);

        long endTime = System.currentTimeMillis();

        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    private static List<Thread> simulateRandomConcurrentSales(InventoryManager inventoryManager, List<Product> availableProducts) {
        Random random = new Random();
        int numberOfSales = random.nextInt(MAX_SIMULATED_SALES) + MIN_SALE_QUANTITY;
        System.out.println("Simulating " + numberOfSales + " sales...");
        List<Thread> saleThreads = new ArrayList<>();

        for (int i = 0; i < numberOfSales; i++) {
            int numberOfProductsToSell = random.nextInt(availableProducts.size()) + MIN_SALE_QUANTITY;

            Map<Product, Integer> productsForSale = new HashMap<>(); // Use a map for products and their quantities

            for (int j = 0; j < numberOfProductsToSell; j++) {
                Product product = availableProducts.get(random.nextInt(availableProducts.size()));
                int quantity = random.nextInt(MAX_SALE_QUANTITY) + MIN_SALE_QUANTITY;
                productsForSale.put(product, productsForSale.getOrDefault(product, 0) + quantity);
            }

            Thread saleThread = new Thread(() -> {
                    //TimeUnit.SECONDS.sleep(random.nextInt(MAX_SLEEP_TIME) + MIN_SLEEP_TIME);
                    inventoryManager.performSale(productsForSale);
            });

            saleThread.start();
            saleThreads.add(saleThread);
        }
        return saleThreads;
    }

    private static void simulateInventoryChecks(InventoryManager inventoryManager) {
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Performing first inventory check:");
            inventoryManager.performInventoryCheck();

            TimeUnit.SECONDS.sleep(5);
            System.out.println("Performing second inventory check:");
            inventoryManager.performInventoryCheck();

        } catch (InterruptedException e) {
            System.out.println("Error during inventory check: " + e.getMessage());
        }
    }

    private static void printProductDetails(List<Product> initialProducts) {
        for (Product product : initialProducts) {
            System.out.println(product.getName() + " - Quantity: " + product.getQuantity() + ", Price: $" + product.getPrice());
        }
    }
}
