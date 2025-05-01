import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalculatorApp extends Application {

    private TextField display = new TextField();
    private double firstOperand = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    public static void main(String[] args) {
        launch(args);
    }

    
    public void start(Stage stage) {
        stage.setTitle("Simple Calculator");

        display.setAlignment(Pos.CENTER_RIGHT);
        display.setEditable(false);
        display.setPrefHeight(50);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        String[][] buttons = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", "C", "=", "+"}
        };

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String text = buttons[row][col];
                Button btn = new Button(text);
                btn.setPrefSize(60, 60);
                btn.setOnAction(e -> handleButton(text));
                grid.add(btn, col, row);
            }
        }

        VBox root = new VBox(10, display, grid);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 280, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void handleButton(String value) {
        switch (value) {
            case "C":
                display.clear();
                firstOperand = 0;
                operator = "";
                startNewNumber = true;
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                operator = value;
                firstOperand = Double.parseDouble(display.getText());
                startNewNumber = true;
                break;
            case "=":
                double secondOperand = Double.parseDouble(display.getText());
                double result = switch (operator) {
                    case "+" -> firstOperand + secondOperand;
                    case "-" -> firstOperand - secondOperand;
                    case "*" -> firstOperand * secondOperand;
                    case "/" -> secondOperand != 0 ? firstOperand / secondOperand : 0;
                    default -> secondOperand;
                };
                display.setText(String.valueOf(result));
                startNewNumber = true;
                break;
            default:
                if (startNewNumber) {
                    display.setText(value);
                    startNewNumber = false;
                } else {
                    display.appendText(value);
                }
                break;
        }
    }
}

