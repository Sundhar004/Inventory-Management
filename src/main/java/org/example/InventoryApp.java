package org.example;

import javafx.application.Application;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryApp extends Application {

    private final ObservableList<Product> data = FXCollections.observableArrayList();
    private final TableView<Product> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final File storeFile = Paths.get(System.getProperty("user.home"), ".inventory", "products.csv").toFile();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Inventory Management - JavaFX");

        createTable();
        loadFromStore();

        searchField.setPromptText("Search name, SKU or location");
        searchField.setMinWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> showAddDialog());

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Product sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) showEditDialog(sel);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Product sel = table.getSelectionModel().getSelectedItem();
            if (sel != null && confirm("Delete product " + sel.getName() + "?")) {
                data.remove(sel);
                saveToStore();
            }
        });

        Button importBtn = new Button("Import CSV");
        importBtn.setOnAction(e -> importCSV(stage));

        Button exportBtn = new Button("Export CSV");
        exportBtn.setOnAction(e -> exportCSV(stage));

        Button resetBtn = new Button("Reset to sample");
        resetBtn.setOnAction(e -> {
            if (confirm("Reset local store to sample data?")) {
                data.clear();
                data.addAll(sampleData());
                saveToStore();
            }
        });

        HBox topBar = new HBox(8, searchField, addBtn, editBtn, deleteBtn, importBtn, exportBtn, resetBtn);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(8));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        Label status = new Label("Products: " + data.size());
        data.addListener((ListChangeListener<Product>) c -> status.setText("Products: " + data.size()));
        bottomBar.getChildren().add(status);

        VBox root = new VBox(8, topBar, table, bottomBar);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 900, 560);
        stage.setScene(scene);
        stage.show();
    }

    private void createTable() {
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());

        TableColumn<Product, String> skuCol = new TableColumn<>("SKU");
        skuCol.setCellValueFactory(cell -> cell.getValue().skuProperty());

        TableColumn<Product, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cell -> cell.getValue().qtyProperty());

        TableColumn<Product, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());
        priceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("%.2f", price.doubleValue()));
            }
        });

        TableColumn<Product, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(cell -> cell.getValue().locationProperty());

        table.getColumns().addAll(nameCol, skuCol, qtyCol, priceCol, locCol);
    }

    private void showAddDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = productForm(null);
        dialog.getDialogPane().setContent(g);

        dialog.setResultConverter(bt -> bt == ButtonType.OK ? readProductFromForm(g, null) : null);
        dialog.showAndWait().ifPresent(p -> {
            p.setId(UUID.randomUUID().toString());
            data.add(0, p);
            saveToStore();
        });
    }

    private void showEditDialog(Product existing) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = productForm(existing);
        dialog.getDialogPane().setContent(g);

        dialog.setResultConverter(bt -> bt == ButtonType.OK ? readProductFromForm(g, existing) : null);
        dialog.showAndWait().ifPresent(p -> {
            existing.setName(p.getName());
            existing.setSku(p.getSku());
            existing.setQty(p.getQty());
            existing.setPrice(p.getPrice());
            existing.setLocation(p.getLocation());
            table.refresh();
            saveToStore();
        });
    }

    private GridPane productForm(Product prefill) {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.setPadding(new Insets(10));

        TextField name = new TextField(prefill == null ? "" : prefill.getName());
        TextField sku = new TextField(prefill == null ? "" : prefill.getSku());
        TextField qty = new TextField(prefill == null ? "0" : Integer.toString(prefill.getQty()));
        TextField price = new TextField(prefill == null ? "0.00" : Double.toString(prefill.getPrice()));
        TextField location = new TextField(prefill == null ? "" : prefill.getLocation());

        g.addRow(0, new Label("Name:"), name);
        g.addRow(1, new Label("SKU:"), sku);
        g.addRow(2, new Label("Qty:"), qty);
        g.addRow(3, new Label("Price:"), price);
        g.addRow(4, new Label("Location:"), location);

        g.setUserData(Map.of("name", name, "sku", sku, "qty", qty, "price", price, "location", location));
        return g;
    }

    private Product readProductFromForm(GridPane g, Product existing) {
        @SuppressWarnings("unchecked")
        Map<String, TextField> map = (Map<String, TextField>) g.getUserData();
        String name = map.get("name").getText().trim();
        String sku = map.get("sku").getText().trim();
        int qty = parseIntSafe(map.get("qty").getText().trim(), 0);
        double price = parseDoubleSafe(map.get("price").getText().trim(), 0.0);
        String location = map.get("location").getText().trim();
        Product p = existing == null ? new Product() : new Product(existing.getId(), name, sku, qty, price, location);
        p.setName(name); p.setSku(sku); p.setQty(qty); p.setPrice(price); p.setLocation(location);
        return p;
    }

    private int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    private void loadFromStore() {
        try {
            if (!storeFile.exists()) {
                storeFile.getParentFile().mkdirs();
                data.addAll(sampleData());
                saveToStore();
                return;
            }
            List<String> lines = Files.readAllLines(storeFile.toPath());
            List<Product> list = lines.stream()
                    .filter(l -> !l.isBlank())
                    .map(this::productFromCsv)
                    .collect(Collectors.toList());
            data.clear();
            data.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
            data.addAll(sampleData());
        }
    }

    private void saveToStore() {
        try {
            storeFile.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(storeFile))) {
                bw.write("id,name,sku,qty,price,location\n");
                for (Product p : data) {
                    bw.write(csvEscape(p.getId()) + "," + csvEscape(p.getName()) + "," + csvEscape(p.getSku()) + ","
                            + p.getQty() + "," + p.getPrice() + "," + csvEscape(p.getLocation()) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to save store: " + e.getMessage());
        }
    }

    private Product productFromCsv(String line) {
        String[] parts = splitCsv(line);
        String id = parts.length > 0 ? unescape(parts[0]) : UUID.randomUUID().toString();
        String name = parts.length > 1 ? unescape(parts[1]) : "";
        String sku = parts.length > 2 ? unescape(parts[2]) : "";
        int qty = parts.length > 3 ? parseIntSafe(parts[3], 0) : 0;
        double price = parts.length > 4 ? parseDoubleSafe(parts[4], 0.0) : 0.0;
        String location = parts.length > 5 ? unescape(parts[5]) : "";
        return new Product(id, name, sku, qty, price, location);
    }

    private List<Product> sampleData() {
        return List.of(
                new Product(UUID.randomUUID().toString(), "Pen", "PEN001", 100, 1.50, "Store A"),
                new Product(UUID.randomUUID().toString(), "Notebook", "NB001", 50, 3.00, "Store B"),
                new Product(UUID.randomUUID().toString(), "Stapler", "STP001", 20, 5.75, "Store A")
        );
    }

    private void importCSV(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File f = fc.showOpenDialog(stage);
        if (f == null) return;
        try {
            List<String> lines = Files.readAllLines(f.toPath());
            if (lines.isEmpty()) return;
            List<Product> imported = lines.stream().skip(1)
                    .filter(s -> !s.isBlank())
                    .map(this::productFromCsv)
                    .collect(Collectors.toList());
            data.addAll(0, imported);
            saveToStore();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Failed to import CSV: " + ex.getMessage());
        }
    }

    private void exportCSV(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fc.setInitialFileName("products_export.csv");
        File f = fc.showSaveDialog(stage);
        if (f == null) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write("id,name,sku,qty,price,location\n");
            for (Product p : data) {
                bw.write(csvEscape(p.getId()) + "," + csvEscape(p.getName()) + "," + csvEscape(p.getSku()) + ","
                        + p.getQty() + "," + p.getPrice() + "," + csvEscape(p.getLocation()) + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Failed to export CSV: " + ex.getMessage());
        }
    }

    private void applyFilter(String q) {
        if (q == null || q.isBlank()) {
            table.setItems(data);
            return;
        }
        String lower = q.toLowerCase();
        FilteredList<Product> filt = new FilteredList<>(data);
        filt.setPredicate(p ->
                (p.getName() + " " + p.getSku() + " " + p.getLocation()).toLowerCase().contains(lower)
        );
        table.setItems(filt);
    }

    private boolean confirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.showAndWait();
    }

    // CSV helpers
    private static String csvEscape(String v) {
        if (v == null) return "";
        String s = v.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) return "\"" + s + "\"";
        return s;
    }

    private static String unescape(String v) {
        if (v == null) return "";
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
            String inner = v.substring(1, v.length() - 1);
            return inner.replace("\"\"", "\"");
        }
        return v;
    }

    private static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"'); i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
