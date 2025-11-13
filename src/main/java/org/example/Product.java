package org.example;

import javafx.beans.property.*;

public class Product {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty sku = new SimpleStringProperty();
    private final IntegerProperty qty = new SimpleIntegerProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final StringProperty location = new SimpleStringProperty();

    public Product() {}

    public Product(String id, String name, String sku, int qty, double price, String location) {
        this.id.set(id);
        this.name.set(name);
        this.sku.set(sku);
        this.qty.set(qty);
        this.price.set(price);
        this.location.set(location);
    }

    // id
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    // name
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // sku
    public String getSku() { return sku.get(); }
    public void setSku(String sku) { this.sku.set(sku); }
    public StringProperty skuProperty() { return sku; }

    // qty
    public int getQty() { return qty.get(); }
    public void setQty(int qty) { this.qty.set(qty); }
    public IntegerProperty qtyProperty() { return qty; }

    // price
    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public DoubleProperty priceProperty() { return price; }

    // location
    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }
    public StringProperty locationProperty() { return location; }
}
