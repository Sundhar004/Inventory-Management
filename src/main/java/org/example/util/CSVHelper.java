package org.example.util;

import org.example.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    // Save products to a specified CSV file
    public static void saveProducts(List<Product> products, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Name,Category,Quantity,Price");
            writer.newLine();

            // Write each product
            for (Product p : products) {
                writer.write(String.format("%d,%s,%s,%d,%.2f",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice()));
                writer.newLine();
            }
        }
//        System.out.println("📄 CSV report saved successfully at: " + new File(filePath).getAbsolutePath());
    }

    // Overloaded method — default save to "products.csv"
    public static void saveProducts(List<Product> products) throws IOException {
        saveProducts(products, "products.csv");
    }

    // Load products from CSV file
    public static List<Product> loadProducts(String filePath) throws IOException {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // skip header
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    int qty = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());

                    products.add(new Product(id, name, category, qty, price));
                }
            }
        }

        System.out.println("📦 Loaded " + products.size() + " products from CSV: " + filePath);
        return products;
    }

    // Overloaded default loader
    public static List<Product> loadProducts() throws IOException {
        return loadProducts("products.csv");
    }
}
