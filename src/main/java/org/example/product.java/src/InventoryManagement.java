package org.example.product.java.src;
import org.example.product.java.Product;

import java.util.*;

public class InventoryManagement {
    private static List<Product> inventory = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n===== Inventory Menu =====");
                System.out.println("1. Add Item");
                System.out.println("2. Delete Item");
                System.out.println("3. Update Item");
                System.out.println("4. Search Item");
                System.out.println("5. View All Items");
                System.out.println("6. Exit Inventory");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addItem(sc);
                        break;
                    case 2:
                        deleteItem(sc);
                        break;
                    case 3:
                        updateItem(sc);
                        break;
                    case 4:
                        searchItem(sc);
                        break;
                    case 5:
                        viewItems();
                        break;
                    case 6:
                        System.out.println("Exiting Inventory...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
               catch (InputMismatchException e)
                {
                    System.out.println("Invalid choice. Please try again.");
                    sc.nextLine();
                }
                catch (Exception e) {
                    System.out.println("Unexpected Error"+e.getMessage());
                }

        }
    }

    private static void addItem(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Product Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Category: ");
        String category = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        Product p = new Product(id, name, category, quantity, price);
        inventory.add(p);
        System.out.println("Item added successfully!");
    }

    private static void deleteItem(Scanner sc) {
        System.out.print("Enter Product ID to delete: ");
        int delId = sc.nextInt();
        sc.nextLine();

        boolean removed = inventory.removeIf(prod -> prod.getId() == delId);
        if (removed) {
            System.out.println("Item deleted successfully.");
        } else {
            System.out.println("No item found with that ID.");
        }
    }

    private static void updateItem(Scanner sc) {
        System.out.print("Enter Product ID to update: ");
        int updId = sc.nextInt();
        sc.nextLine();

        for (Product prod : inventory) {
            if (prod.getId() == updId) {
                System.out.print("Enter new name: ");
                String newName = sc.nextLine();
                System.out.print("Enter new category: ");
                String newCategory = sc.nextLine();
                System.out.print("Enter new quantity: ");
                int newQuantity = sc.nextInt();
                System.out.print("Enter new price: ");
                double newPrice = sc.nextDouble();

                prod.setName(newName);
                prod.setCategory(newCategory);
                prod.setQuantity(newQuantity);
                prod.setPrice(newPrice);

                System.out.println("Item updated successfully.");
                return;
            }
        }
        System.out.println("No item found with that ID.");
    }

    private static void searchItem(Scanner sc) {
        System.out.print("Enter Product ID to search: ");
        int searchId = sc.nextInt();
        sc.nextLine();

        for (Product prod : inventory) {
            if (prod.getId() == searchId) {
                System.out.println("Found: " + prod);
                return;
            }
        }
        System.out.println("No item found with that ID.");
    }

    private static void viewItems() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            System.out.println("\n--- Inventory Items ---");
            for (Product prod : inventory) {
                System.out.println(prod);
            }
        }
    }
}

