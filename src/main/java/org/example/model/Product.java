package org.example.model;

import org.example.exception.InvalidInputException;

public class Product {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;

    // Constructor with validations
    public Product(int id, String name, String category, int quantity, double price) {
        setId(id);
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setPrice(price);
    }

    // Default constructor
    public Product() {}

    // Getters and Setters with validations
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new InvalidInputException("❌ Product ID must be greater than 0.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("❌ Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidInputException("❌ Category cannot be empty.");
        }
        this.category = category.trim();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidInputException("❌ Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new InvalidInputException("❌ Price cannot be negative.");
        }
        this.price = price;
    }

    // For printing product details
    @Override
    public String toString() {
        return "📦 Product {" +
                "ID=" + id +
                ", Name='" + name + '\'' +
                ", Category='" + category + '\'' +
                ", Quantity=" + quantity +
                ", Price=" + price +
                '}';
    }
}
