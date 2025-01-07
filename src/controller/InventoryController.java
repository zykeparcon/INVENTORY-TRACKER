package controller;

import model.Database;
import model.InventoryItem;
import java.util.*;

public class InventoryController {
    private final Database database;
    private Map<String, InventoryItem> inventory;

    public InventoryController() {
        this.database = new Database();
        this.inventory = database.loadInventory();
    }

    public void addItem(String name, double quantity, String unit) {
        if (inventory.containsKey(name)) {
            InventoryItem existingItem = inventory.get(name);
            if (!existingItem.getUnit().equals(unit)) {
                throw new IllegalArgumentException("Item exists with different unit");
            }
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            inventory.put(name, new InventoryItem(name, quantity, unit));
        }
        database.saveInventory(inventory);
    }

    public void removeItem(String name) {
        inventory.remove(name);
        database.saveInventory(inventory);
    }

    public void deleteAllItems() {
        inventory.clear();
        database.saveInventory(inventory);
    }

    public Map<String, InventoryItem> getInventory() {
        return new HashMap<>(inventory);
    }

    public List<InventoryItem> searchItems(String searchTerm) {
        return inventory.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    public void close() {
        database.close();
    }

    // Sorting algorithms
    public List<InventoryItem> sortInventory() {
        List<InventoryItem> items = new ArrayList<>(inventory.values());
        insertionSort(items);
        return items;
    }

    private void insertionSort(List<InventoryItem> items) {
        for (int i = 1; i < items.size(); i++) {
            InventoryItem key = items.get(i);
            int j = i - 1;
            while (j >= 0 && items.get(j).getQuantity() > key.getQuantity()) {
                items.set(j + 1, items.get(j));
                j--;
            }
            items.set(j + 1, key);
        }
    }
} 