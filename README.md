Inventory Management System
Overview
This application simulates an inventory management system with multiple concurrent sales transactions, inventory checks, and product sales logging. The simulation handles multiple threads performing sales operations while ensuring thread safety and consistency across shared resources using mutex locks.

Mutex Locks and Invariants
The system uses ReentrantLocks to ensure that shared resources (like product quantities and total sales money) are accessed in a thread-safe manner. The following describes which locks (mutex) are used and the invariants they protect:

1. Product Lock (for each product)
   Lock Object: ReentrantLock within each Product instance.
   Protected Invariants:
   Quantity: The quantity of each product is protected from race conditions during concurrent sales. This lock ensures that no two threads can sell the same product simultaneously, preventing incorrect reductions of product stock.
   Lock Usage:
   Acquired when a product's quantity is being checked or updated during a sale (Product.sell() method).
   Released after the sale operation is complete or when an exception is thrown.
2. InventoryManager's Total Money Lock
   Lock Object: moneyLock in the InventoryManager class.
   Protected Invariants:
   Total Money: The totalMoney field in InventoryManager holds the cumulative value of all sales. This lock ensures that only one thread can update the total money at a time, avoiding inconsistencies during concurrent sales.
   Lock Usage:
   Acquired when updating the total sales money after completing a sale (InventoryManager.addBillAndUpdateMoney()).
   Released after the update is complete.
3. SalesRecord Lock
   Lock Object: lock in the SalesRecord class.
   Protected Invariants:
   Sales List (Bills): The list of all bills (records of completed sales) is protected by this lock to prevent race conditions during additions or when the sales record is being read.
   Lock Usage:
   Acquired when adding a new sale record (bill) to the sales history (SalesRecord.addBillToRecord()).
   Acquired when reading sales history (SalesRecord.getBills() and SalesRecord.printSalesRecord()).
   Released after the respective operation (add or read) is complete.
4. Global Product Quantity Validation
   Invariants:
   The initial quantity of products must match the current quantity minus the total quantity sold across all transactions. This is checked during the inventory check, which doesn't require its own mutex but relies on accurate and synchronized updates via the product and sales locks.
   Usage Notes
   Each Product has its own individual lock, ensuring that sales on different products can proceed concurrently without interference. However, sales on the same product must be serialized.
   The moneyLock and SalesRecord lock prevent conflicting updates to shared resources during sales transactions.
   The system is designed to handle multiple concurrent threads safely, ensuring no race conditions when performing sales, updating inventory, or logging sales data.

Number of Threads	 Time Taken (ms)
10	                 10079
50                   10100
100	                 10120
200	                 10150
500	                 10200
1000	             10300
1500	             10380
2000	             10430
2500	             10500
3000	             10587