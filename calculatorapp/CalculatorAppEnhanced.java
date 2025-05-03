package calculatorapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.function.UnaryOperator;

public class CalculatorAppEnhanced extends Application {

    // --- UI Elements ---
    private Label operationDisplayLabel = new Label(""); // Shows pending operation
    private TextField display = new TextField("0");      // Main number display
    private ListView<String> historyList = new ListView<>();
    private VBox historyPane = new VBox();               // Container for history
    private ComboBox<String> unitBox = new ComboBox<>();
    private TextField unitInput = new TextField();
    private TextField unitOutputDisplay = new TextField();

    // --- Calculation State ---
    private double firstOperand = 0;
    private String operator = "";
    private boolean startNewNumber = true; // Flag to indicate if the next digit should start a new number

    // --- Formatting ---
    private static final DecimalFormat numberFormat = new DecimalFormat("#.##########"); // Avoid scientific notation

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Calculator with History and Unit Conversion");

        // --- Display Setup ---
        operationDisplayLabel.setAlignment(Pos.CENTER_RIGHT);
        operationDisplayLabel.setPrefWidth(Double.MAX_VALUE);
        operationDisplayLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        operationDisplayLabel.setPadding(new Insets(0, 5, 0, 5));

        display.setAlignment(Pos.CENTER_RIGHT);
        display.setEditable(false);
        display.setPrefHeight(50);
        display.setStyle("-fx-font-size: 24px; -fx-font-weight:bold;");

        VBox displayStack = new VBox(0, operationDisplayLabel, display);
        displayStack.setAlignment(Pos.CENTER_RIGHT);

        // --- Button Grid Setup ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        String[][] buttons = {
                {"C", "+/-", "%", "/"},
                {"7", "8", "9", "*"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"0", ".", "=", ""}
        };

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String text = buttons[row][col];
                if (!text.isEmpty()) {
                    Button btn = createButton(text);
                    grid.add(btn, col, row + 1);
                }
            }
        }

        // --- History Panel Setup ---
        historyList.setPrefWidth(200);
        historyPane.getChildren().addAll(new Label("History:"), historyList);
        historyPane.setPadding(new Insets(10));
        historyPane.setVisible(false);

        Button historyToggle = new Button("Show History");
        historyToggle.setOnAction(_ -> {
            boolean isVisible = !historyPane.isVisible();
            historyPane.setVisible(isVisible);
            historyToggle.setText(isVisible ? "Hide History" : "Show History");
        });

        Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.setOnAction(_ -> historyList.getItems().clear());

        HBox historyControls = new HBox(10, historyToggle, clearHistoryButton);
        historyControls.setAlignment(Pos.CENTER_LEFT);

        // --- Unit Converter Setup ---
        VBox unitBoxPane = new VBox(5);
        setupUnitConverter(unitBoxPane);
        unitBoxPane.setPadding(new Insets(10));
        unitBoxPane.setPrefWidth(200);

        unitBox.getItems().addAll("cm to mm", "m to mm", "inches to mm");
        unitBox.setValue("cm to mm");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return (newText.isEmpty() || newText.matches("-?(\\d*\\.?\\d*)?")) ? change : null;
        };
        unitInput.setTextFormatter(new TextFormatter<>(filter));
        unitInput.setPromptText("Enter value");

        unitOutputDisplay.setEditable(false);
        unitOutputDisplay.setPromptText("Result");
        unitOutputDisplay.setStyle("-fx-background-color: -fx-control-inner-background-alt;");

        Button convertButton = new Button("Convert");
        

        unitBoxPane.getChildren().addAll(new Label("Unit Converter"), unitInput, unitBox, convertButton, unitOutputDisplay);
        unitBoxPane.setPrefWidth(180);

        // --- Main Layout ---
        VBox calculatorPane = new VBox(10, displayStack, grid); // Add displayStack here
calculatorPane.setPadding(new Insets(0, 10, 10, 10));
calculatorPane.setPrefWidth(300);

        HBox mainContent = new HBox(10, calculatorPane, unitBoxPane, historyPane);
        VBox root = new VBox(10, historyControls, mainContent);
        mainContent.setPadding(new Insets(10));
mainContent.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 650, 450);

        // --- Keyboard Handling ---
        setKeyboardHandler(scene);

        // --- Stage Finalization ---
        stage.setScene(scene);
        stage.setMinWidth(450);
        stage.setMinHeight(400);
        stage.show();
    }

    // --- Helper Methods ---
    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(60, 60);
        btn.setStyle("-fx-font-size: 16px;");
        if ("C/%".contains(text) || text.equals("+/-")) {
            btn.setStyle("-fx-font-size: 16px; -fx-base: lightgray;");
        } else if ("/*-+".contains(text)) {
            btn.setStyle("-fx-font-size: 16px; -fx-base: #f0f0f0;");
        } else if ("=".equals(text)) {
            btn.setStyle("-fx-font-size: 16px; -fx-base: lightblue; -fx-font-weight: bold;");
        }
        btn.setOnAction(_ -> handleButton(text));
        return btn;
    }
                private void setupUnitConverter(VBox unitBoxPane) {
            // Dropdowns for source and target units
            ComboBox<String> sourceUnitBox = new ComboBox<>();
            ComboBox<String> targetUnitBox = new ComboBox<>();
            sourceUnitBox.getItems().addAll("mm", "cm", "m", "inches");
            targetUnitBox.getItems().addAll("mm", "cm", "m", "inches");
            sourceUnitBox.setValue("mm");
            targetUnitBox.setValue("cm");
        
            // Editable text fields for source and target values
            TextField sourceInput = new TextField();
            TextField targetInput = new TextField();
            sourceInput.setPromptText("Enter value");
            targetInput.setPromptText("Converted value");
        
            // Ensure only valid numeric input
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                return (newText.isEmpty() || newText.matches("-?(\\d*\\.?\\d*)?")) ? change : null;
            };
            sourceInput.setTextFormatter(new TextFormatter<>(filter));
            targetInput.setTextFormatter(new TextFormatter<>(filter));
        
            // Add listeners for two-way conversion
                                                            sourceInput.textProperty().addListener((_, _, newValue) -> {
                                            if (!newValue.isEmpty()) {
                                                try {
                                                    double sourceValue = Double.parseDouble(newValue);
                                                    double convertedValue = convertUnit(sourceValue, sourceUnitBox.getValue(), targetUnitBox.getValue());
                                                    targetInput.setText(numberFormat.format(convertedValue));
                                                } catch (NumberFormatException e) {
                                                    targetInput.setText("Invalid input");
                                                }
                                            } else {
                                                targetInput.clear();
                                            }
                                        });
            targetInput.textProperty().addListener((_, _, newValue) -> {
                if (!newValue.isEmpty()) {
                    try {
                        double targetValue = Double.parseDouble(newValue);
                        double convertedValue = convertUnit(targetValue, targetUnitBox.getValue(), sourceUnitBox.getValue());
                        sourceInput.setText(numberFormat.format(convertedValue));
                    } catch (NumberFormatException e) {
                        sourceInput.setText("Invalid input");
                    }
                } else {
                    sourceInput.clear();
                }
            });
        
            // Layout for the unit converter
            unitBoxPane.getChildren().addAll(
                new Label("Unit Converter"),
                new HBox(10, new Label("From:"), sourceUnitBox, sourceInput),
                new HBox(10, new Label("To:"), targetUnitBox, targetInput)
            );
            unitBoxPane.setPadding(new Insets(10));
            unitBoxPane.setPrefWidth(250);
        }

    private void setKeyboardHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            String keyText = event.getText();

            if (code.isDigitKey() || code.isKeypadKey()) {
                handleButton(keyText);
            } else if (code == KeyCode.DECIMAL || code == KeyCode.PERIOD) {
                handleButton(".");
            } else if (code == KeyCode.ENTER) {
                handleButton("=");
            } else if (code == KeyCode.BACK_SPACE) {
                handleBackspace();
            } else if (code == KeyCode.ESCAPE) {
                handleButton("C");
            } else if ("+-*/".contains(keyText)) {
                handleButton(keyText);
            }
        });
    }

    private void handleBackspace() {
        String currentText = display.getText();
        if (currentText.length() > 1) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            display.setText("0");
            startNewNumber = true;
        }
    }

    private void handleButton(String value) {
        switch (value) {
            case "C" -> handleClear();
            case "+/-" -> handleNegate();
            case "." -> handleDecimal();
            case "%" -> handlePercent();
            case "+", "-", "*", "/" -> handleOperator(value);
            case "=" -> handleEquals();
            default -> handleDigit(value);
        }
    }

    private void handleClear() {
        display.setText("0");
        operationDisplayLabel.setText("");
        firstOperand = 0;
        operator = "";
        startNewNumber = true;
    }

    private void handleNegate() {
        String currentText = display.getText();
        if (!currentText.equals("0")) {
            display.setText(currentText.startsWith("-") ? currentText.substring(1) : "-" + currentText);
        }
    }

    private void handleDecimal() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.appendText(".");
        }
    }

    private void handleDigit(String digit) {
        if (startNewNumber) {
            display.setText(digit);
            startNewNumber = false;
        } else {
            display.appendText(digit);
        }
    }

    private void handleOperator(String currentOperator) {
        firstOperand = Double.parseDouble(display.getText());
        operator = currentOperator;
        operationDisplayLabel.setText(firstOperand + " " + operator);
        startNewNumber = true;
    }

       private void handleEquals() {
        if (!operator.isEmpty()) {
            double secondOperand = Double.parseDouble(display.getText());
            double result = switch (operator) {
                case "+" -> firstOperand + secondOperand;
                case "-" -> firstOperand - secondOperand;
                case "*" -> firstOperand * secondOperand;
                case "/" -> secondOperand != 0 ? firstOperand / secondOperand : Double.NaN;
                default -> 0;
            };
    
            // Update the operation display to show the full operation
            operationDisplayLabel.setText(firstOperand + " " + operator + " " + secondOperand + " =");
    
            // Display the result in the main display
            display.setText(Double.isNaN(result) ? "Error" : numberFormat.format(result));
    
            // Add the operation to the history
            historyList.getItems().add(firstOperand + " " + operator + " " + secondOperand + " = " + result);
    
            // Reset for the next operation
            operator = "";
            startNewNumber = true;
        }
    }

    private void handlePercent() {
        double currentValue = Double.parseDouble(display.getText());
        display.setText(numberFormat.format(currentValue / 100));
    }

    private double convertUnit(double value, String fromUnit, String toUnit) {
        // Conversion factors to mm
        double toMM = switch (fromUnit) {
            case "mm" -> value;
            case "cm" -> value * 10;
            case "m" -> value * 1000;
            case "inches" -> value * 25.4;
            default -> 0;
        };
    
        // Convert from mm to the target unit
        return switch (toUnit) {
            case "mm" -> toMM;
            case "cm" -> toMM / 10;
            case "m" -> toMM / 1000;
            case "inches" -> toMM / 25.4;
            default -> 0;
        };
    }
}