package sales;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SalesRecord {
    private List<Bill> bills;
    private ReentrantLock lock;

    public SalesRecord() {
        this.bills = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    void addBillToRecord(Bill bill) {
        lock.lock();
        try {
            bills.add(bill);
        } finally {
            lock.unlock();
        }
    }

    public List<Bill> getBills() {
        lock.lock();
        try {
            return new ArrayList<>(bills);
        } finally {
            lock.unlock();
        }
    }

    public void printSalesRecord() {
        System.out.println("Sales Record:");
        lock.lock();
        try{
            for (Bill bill : bills) {
                bill.printBill();
                System.out.println("---------------");
            }
        } finally {
            lock.unlock();
        }
    }
}
