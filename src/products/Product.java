package products;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Product {
    private String name;
    private int quantity;
    private final double price;
    private ReentrantLock lock;

    public Product(String name) {
        this.name = name;
        this.quantity = new Random().nextInt(1000);
        this.price = new Random().nextInt(1000);
        this.lock = new ReentrantLock();
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        lock.lock();
        try {
            return quantity;
        } finally {
            lock.unlock();
        }
    }

    public double getPrice() {
        return price;
    }

    public boolean sell(int quantityToSell) {
            if (quantity >= quantityToSell) {
                quantity -= quantityToSell;
                return true;
            } else {
                return false;
            }
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
