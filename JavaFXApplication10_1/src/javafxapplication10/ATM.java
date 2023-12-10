import java.io.File;
import java.io.IOException;
import java.util.logging.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class ATM extends Application {
    // Logger setup
    private static final Logger LOGGER = Logger.getLogger(ATM.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("src/javafxapplication10/ATM_Log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "FileHandler could not be initialized", e);
        }
    }

    // Counter for bank accounts created
    public int count = 0;
    Map<String, Double> accounts = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ATM");

        // Initial account for demonstration purposes
        accounts.put("user1", 1000.00);

        // GridPane setup
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // UI elements
        Label accountIdLabel = new Label("Account ID:");
        grid.add(accountIdLabel, 0, 1);

        TextField accountIdField = new TextField();
        grid.add(accountIdField, 1, 1);

        Label actionLabel = new Label("Select Action:");
        grid.add(actionLabel, 0, 2);

        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("Deposit", "Withdraw", "Check Balance", "Create Account");
        grid.add(actionComboBox, 1, 2);

        Label amountLabel = new Label("Amount:");
        grid.add(amountLabel, 0, 3);

        TextField amountField = new TextField();
        grid.add(amountField, 1, 3);

        Button btn = new Button("Execute");
        grid.add(btn, 1, 4);

        final Label message = new Label();
        grid.add(message, 1, 6);

        btn.setOnAction(e -> {
    String accountId = accountIdField.getText();
    String action = actionComboBox.getValue(); // This could be null if nothing is selected.
    String amountText = amountField.getText();
    double amount = 0.0;
    boolean inputValid = true;

    // Check if an action has been selected or not.
    if (action == null) {
        message.setText("Error: No action selected.");
        inputValid = false;
        LOGGER.log(Level.WARNING, "No action selected for account: " + accountId);
    } else if (!action.equals("Check Balance") && !action.equals("Create Account")) {
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            inputValid = false;
            message.setText("Error: Invalid amount");
            LOGGER.log(Level.SEVERE, "Invalid amount entered for account: " + accountId, ex);
        }
    }

    // Proceed only if input is valid (an action has been selected and amount is valid if needed).
    if (inputValid) {
        switch (action) {
            case "Deposit":
                deposit(accountId, amount, message);
                break;
            case "Withdraw":
                withdraw(accountId, amount, message);
                break;
            case "Check Balance":
                checkBalance(accountId, message);
                break;
            case "Create Account":
                createAccount(accountId, message);
                break;
            default:
                message.setText("Please select a valid action.");
                break;
        }
    }
});
        

        // Scene setup
        Scene scene = new Scene(grid, 400, 500);
        primaryStage.setScene(scene);
        scene.getStylesheets().add((new File("src/javafxapplication10/style.css")).toURI().toString());
        primaryStage.show();
    }

    private void deposit(String accountId, double amount, Label message) {
        if (accounts.containsKey(accountId)) {
            accounts.put(accountId, accounts.get(accountId) + amount);
            message.setText("Deposit successful. New balance: " + accounts.get(accountId));
            LOGGER.info("Deposit: Account ID: " + accountId + " Amount: " + amount);
        } else {
            message.setText("Error: Account does not exist");
            LOGGER.warning("Attempted deposit to non-existing account: " + accountId);
        }
    }

    private void withdraw(String accountId, double amount, Label message) {
        if (accounts.containsKey(accountId)) {
            double currentBalance = accounts.get(accountId);
            if (currentBalance >= amount) {
                accounts.put(accountId, currentBalance - amount);
                message.setText("Withdrawal successful. New balance: " + accounts.get(accountId));
                LOGGER.info("Withdrawal: Account ID: " + accountId + " Amount: " + amount);
            } else {
                message.setText("Error: Insufficient funds");
                LOGGER.warning("Attempted withdrawal from account: " + accountId + " with insufficient funds.");
            }
        } else {
            message.setText("Error: Account does not exist");
            LOGGER.warning("Attempted withdrawal from non-existing account: " + accountId);
        }
    }

    private void checkBalance(String accountId, Label message) {
        if (accounts.containsKey(accountId)) {
            message.setText("Current balance: " + accounts.get(accountId));
            LOGGER.info("Balance checked for account: " + accountId);
        } else {
            message.setText("Error: Account does not exist");
            LOGGER.warning("Attempted to check balance on non-existing account: " + accountId);
        }
    }

    private void createAccount(String accountId, Label message) {
        if (!accounts.containsKey(accountId)) {
            accounts.put(accountId, 0.0);
            count++;
            message.setText("Account created successfully with ID: " + accountId);
            LOGGER.info("New account created: Account ID: " + accountId);
        } else {
            message.setText("Error: Account already exists");
            LOGGER.warning("Attempted to create a duplicate account: " + accountId);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}