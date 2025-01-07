package view;

import controller.InventoryController;
import model.InventoryItem;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class InventoryView extends VBox {
    private final InventoryController controller;
    private final TextField itemNameField = new TextField();
    private final TextField quantityField = new TextField();
    private final ComboBox<String> unitComboBox;
    private final TextField searchField = new TextField();
    private final TableView<InventoryItem> tableView = new TableView<>();
    private final Label executionLabel = new Label();
    private HBox statsBox;

    private static final List<String> UNITS = Arrays.asList("pieces", "kg", "g", "L", "mL", "boxes");

    // Style constants
    private static final String DARK_BG = "#1a1a1a";
    private static final String DARKER_BG = "#141414";
    private static final String ACCENT_COLOR = "#007acc";
    private static final String HOVER_COLOR = "#0099ff";
    private static final String TEXT_COLOR = "#ffffff";
    private static final String ERROR_COLOR = "#ff4444";
    private static final String SUCCESS_COLOR = "#44ff44";
    private static final String WARNING_COLOR = "#ffaa44";

    public InventoryView(Stage stage) {
        controller = new InventoryController();
        unitComboBox = new ComboBox<>(FXCollections.observableArrayList(UNITS));
        
        setupUI();
        setupTableView();
        applyStyles();
        updateDisplay();

        stage.setOnCloseRequest(e -> controller.close());
    }

    private void setupUI() {
        setPadding(new Insets(20));
        setSpacing(20);

        // Header
        Label titleLabel = new Label("Inventory Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));

        // Stats panel
        statsBox = createStatsPanel();

        // Input section with modern card design
        VBox inputCard = new VBox(15);
        inputCard.setPadding(new Insets(20));
        inputCard.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);",
            DARKER_BG
        ));

        // Input fields in a grid
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(15);
        inputGrid.setVgap(10);
        inputGrid.setAlignment(Pos.CENTER);

        // Styled input fields
        styleTextField(itemNameField, "Item Name");
        styleTextField(quantityField, "Quantity");
        styleComboBox(unitComboBox);

        inputGrid.add(new Label("Item Name:"), 0, 0);
        inputGrid.add(itemNameField, 1, 0);
        inputGrid.add(new Label("Quantity:"), 2, 0);
        inputGrid.add(quantityField, 3, 0);
        inputGrid.add(new Label("Unit:"), 4, 0);
        inputGrid.add(unitComboBox, 5, 0);

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addButton = createStyledButton("Add Item", ACCENT_COLOR);
        Button removeButton = createStyledButton("Remove Item", ERROR_COLOR);
        Button deleteAllButton = createStyledButton("Delete All", ERROR_COLOR);
        Button sortButton = createStyledButton("Sort Items", ACCENT_COLOR);
        
        buttonBox.getChildren().addAll(addButton, removeButton, deleteAllButton, sortButton);

        // Search bar with icon (you can add an icon here)
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        styleTextField(searchField, "Search inventory...");
        searchBox.getChildren().add(searchField);

        inputCard.getChildren().addAll(inputGrid, buttonBox, searchBox);

        // Table with modern styling
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(20));
        tableCard.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);",
            DARKER_BG
        ));
        
        Label tableTitle = new Label("Current Inventory");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web(TEXT_COLOR));
        
        tableCard.getChildren().addAll(tableTitle, tableView);

        // Status bar
        executionLabel.setStyle(String.format(
            "-fx-text-fill: %s; -fx-font-size: 12px; -fx-padding: 5;",
            TEXT_COLOR
        ));

        // Add all components
        getChildren().addAll(titleLabel, statsBox, inputCard, tableCard, executionLabel);

        // Set up event handlers
        addButton.setOnAction(e -> handleAddItem());
        removeButton.setOnAction(e -> handleRemoveItem());
        deleteAllButton.setOnAction(e -> handleDeleteAll());
        sortButton.setOnAction(e -> handleSort());
        searchField.textProperty().addListener((obs, old, newValue) -> handleSearch(newValue));
    }

    private HBox createStatsPanel() {
        statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 10;",
            DARKER_BG
        ));

        // Add only total items and low stock stats
        statsBox.getChildren().addAll(
            createStatCard("Total Items", "0"),
            createStatCard("Low Stock", "0")
        );

        return statsBox;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10, 20, 10, 20));
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 5;",
            ACCENT_COLOR
        ));

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_COLOR));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT_COLOR));
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void styleTextField(TextField field, String promptText) {
        field.setPromptText(promptText);
        field.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-prompt-text-fill: derive(%s, -30%%);
            -fx-background-radius: 5;
            -fx-padding: 8;
            -fx-font-size: 13px;
            """,
            DARKER_BG, TEXT_COLOR, TEXT_COLOR
        ));
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-background-radius: 5;
            -fx-padding: 5;
            """,
            DARKER_BG, TEXT_COLOR
        ));
    }

    private Button createStyledButton(String text, String baseColor) {
        Button button = new Button(text);
        button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-font-size: 13px;
            -fx-padding: 8 15;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """,
            baseColor, TEXT_COLOR
        ));

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-font-size: 13px;
            -fx-padding: 8 15;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """,
            HOVER_COLOR, TEXT_COLOR
        )));

        button.setOnMouseExited(e -> button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-font-size: 13px;
            -fx-padding: 8 15;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """,
            baseColor, TEXT_COLOR
        )));

        return button;
    }

    private void setupTableView() {
        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Item Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(300);

        TableColumn<InventoryItem, String> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("%.2f", data.getValue().getQuantity())));
        quantityCol.setPrefWidth(100);

        TableColumn<InventoryItem, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnit()));
        unitCol.setPrefWidth(100);

        tableView.getColumns().addAll(nameCol, quantityCol, unitCol);
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else {
                    if (item.getQuantity() <= 10) {
                        setStyle("-fx-text-fill: #ff4d4d;"); // Red
                    } else if (item.getQuantity() <= 50) {
                        setStyle("-fx-text-fill: #ffd700;"); // Yellow
                    } else {
                        setStyle("-fx-text-fill: #00ff00;"); // Green
                    }
                }
            }
        });
    }

    private void applyStyles() {
        // VBox (main container)
        setStyle("-fx-background-color: " + DARK_BG + ";");

        // Labels
        for (javafx.scene.Node node : lookupAll("Label")) {
            node.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        }

        // TextFields
        itemNameField.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s;",
            DARKER_BG, TEXT_COLOR
        ));
        quantityField.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s;",
            DARKER_BG, TEXT_COLOR
        ));
        searchField.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s;",
            DARKER_BG, TEXT_COLOR
        ));

        // ComboBoxes
        String comboBoxStyle = String.format(
            "-fx-background-color: %s; -fx-text-fill: %s;",
            DARKER_BG, TEXT_COLOR
        );
        unitComboBox.setStyle(comboBoxStyle);

        // TableView
        tableView.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s;",
            DARKER_BG, TEXT_COLOR
        ));

        // Execution label
        executionLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
    }

    private void updateDisplay() {
        tableView.getItems().clear();
        tableView.getItems().addAll(controller.getInventory().values());
        updateStats();
    }

    private void handleAddItem() {
        try {
            String name = itemNameField.getText();
            double quantity = Double.parseDouble(quantityField.getText());
            String unit = unitComboBox.getValue();

            if (name.isEmpty() || unit == null) {
                showError("Please fill all fields");
                return;
            }

            if (quantity <= 0) {
                showError("Quantity must be positive");
                return;
            }

            controller.addItem(name, quantity, unit);
            updateDisplay();
            clearInputs();
        } catch (NumberFormatException e) {
            showError("Invalid quantity format");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void handleRemoveItem() {
        InventoryItem selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an item to remove");
            return;
        }
        controller.removeItem(selected.getName());
        updateDisplay();
    }

    private void handleDeleteAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete All");
        alert.setHeaderText("Delete all items?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteAllItems();
                updateDisplay();
            }
        });
    }

    private void handleSort() {
        System.gc();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        long startTime = System.nanoTime();
        List<InventoryItem> sorted = controller.sortInventory();
        long endTime = System.nanoTime();
        
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double memoryUsedMiB = (endMemory - startMemory) / (1024.0 * 1024.0);

        tableView.getItems().clear();
        tableView.getItems().addAll(sorted);
        updateStats();
        
        executionLabel.setText(String.format(
            "Sort Time: %,d ns | Memory Used: %.2f MiB", 
            (endTime - startTime), 
            memoryUsedMiB
        ));
    }

    private void handleSearch(String searchTerm) {
        tableView.getItems().clear();
        tableView.getItems().addAll(controller.searchItems(searchTerm));
        updateStats();
    }

    private void clearInputs() {
        itemNameField.clear();
        quantityField.clear();
        unitComboBox.setValue(UNITS.get(0));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStats() {
        if (statsBox != null && statsBox.getChildren().size() >= 2) {
            VBox totalItemsCard = (VBox) statsBox.getChildren().get(0);
            VBox lowStockCard = (VBox) statsBox.getChildren().get(1);
            
            // Update total items
            int totalItems = tableView.getItems().size();
            ((Label) totalItemsCard.getChildren().get(1)).setText(String.valueOf(totalItems));
            
            // Update low stock (items with quantity <= 10)
            long lowStockCount = tableView.getItems().stream()
                .filter(item -> item.getQuantity() <= 10)
                .count();
            ((Label) lowStockCard.getChildren().get(1)).setText(String.valueOf(lowStockCount));
        }
    }
} 