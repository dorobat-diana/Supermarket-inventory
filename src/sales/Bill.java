package sales;

import products.Product;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    private List<Product> productsSold;
    private List<Integer> quantitiesSold;
    private double totalPrice;

    public Bill() {
        this.productsSold = new ArrayList<>();
        this.quantitiesSold = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        try {
            if (product.sell(quantity)) {
                addProductToBill(product, quantity);
            } else {
                System.out.println("Not enough quantity of " + product.getName() + " to sell.");
            }
        } catch (Exception e) {
            System.out.println("Error selling " + product.getName() + ": " + e.getMessage());
        }
    }

    private void addProductToBill(Product product, int quantity) {
        productsSold.add(product);
        quantitiesSold.add(quantity);
        totalPrice += product.getPrice() * quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void printBill() {
        System.out.println("Bill Summary:");
        for (int i = 0; i < productsSold.size(); i++) {
            System.out.println(quantitiesSold.get(i) + "x " + productsSold.get(i).getName() + " @ $" +
                    productsSold.get(i).getPrice() + " each");
        }
        System.out.println("Total Price: $" + totalPrice);
    }

    public List<Integer> getQuantitiesSold() {
        return new ArrayList<>(quantitiesSold);
    }

    public List<Product> getProductsSold() {
        return new ArrayList<>(productsSold);
    }
}
