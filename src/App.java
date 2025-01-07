import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.InventoryView;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        InventoryView view = new InventoryView(primaryStage);
        Scene scene = new Scene(view, 800, 600);
        
        // Set dark theme colors
        scene.getRoot().setStyle("-fx-background-color: #2C2F33;");
        
        // Center the window on screen
        primaryStage.centerOnScreen();
        
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 