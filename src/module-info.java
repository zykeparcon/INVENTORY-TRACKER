module InventorySystem {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    
    exports model;
    exports view;
    exports controller;
    
    opens model;
    opens view;
    opens controller;
} 