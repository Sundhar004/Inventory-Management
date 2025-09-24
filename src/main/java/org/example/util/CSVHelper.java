package org.example.util;

import org.example.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private static final String FILE_NAME = "products.csv";
    private static final String DELIMITER = " | ";

    // Save products to CSV
    public static void saveProducts(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Write header
            writer.write(String.format("%-5s%s%-15s%s%-10s%s%-8s%s%-10s",
                    "ID", DELIMITER, "Name", DELIMITER, "Category", DELIMITER, "Qty", DELIMITER, "Price"));
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();

            // Write each product
            for (Product p : products) {
                writer.write(String.format("%-5d%s%-15s%s%-10s%s%-8d%s%-10.2f",
                        p.getId(), DELIMITER, p.getName(), DELIMITER, p.getCategory(),
                        DELIMITER, p.getQuantity(), DELIMITER, p.getPrice()));
                writer.newLine();
            }
        }
    }

    // Load products from CSV
    public static List<Product> loadProducts() throws IOException {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean firstTwoLines = true;

            while ((line = reader.readLine()) != null) {
                // Skip header + separator line
                if (firstTwoLines) {
                    firstTwoLines = false;
                    continue;
                }
                if (line.startsWith("-")) continue;

                String[] parts = line.split("\\s*\\|\\s*"); // split by " | "
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
        return products;
    }
}
