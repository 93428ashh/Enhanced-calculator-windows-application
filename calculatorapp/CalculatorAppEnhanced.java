package calculatorapp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.function.UnaryOperator; 

public class CalculatorAppEnhanced extends Application {

    private TextField display = new TextField("0");
    private double firstOperand = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    private ListView<String> historyList = new ListView<>();
    private VBox historyPane = new VBox();

    private ComboBox<String> unitBox = new ComboBox<>();
    private TextField unitInput = new TextField();
    
    private TextField unitOutputDisplay = new TextField();
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Calculator");

        display.setAlignment(Pos.CENTER_RIGHT);
        display.setEditable(false);
        display.setPrefHeight(50);
        display.setStyle("-fx-font-size: 18px;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(display, 0, 0, 4, 1);

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
                if (text.isEmpty()) continue;

                Button btn = new Button(text);
                btn.setPrefSize(60, 60);
                btn.setStyle("-fx-font-size: 16px;");

                if ("C/%/*-+".contains(text)) {
                    btn.setStyle("-fx-font-size: 16px; -fx-base: lightgray;");
                } else if ("=".equals(text)) {
                    btn.setStyle("-fx-font-size: 16px; -fx-base: ;");
                }

                btn.setOnAction(_ -> handleButton(text));
                grid.add(btn, col, row + 1);
            }
        }

       
        historyList.setPrefWidth(180);
        historyPane.getChildren().addAll(new Label("History:"), historyList);
        historyPane.setPadding(new Insets(10));
        historyPane.setVisible(false); 

        Button historyToggle = new Button("Show History");
        historyToggle.setOnAction(_ -> {
            boolean isVisible = !historyPane.isVisible();
            historyPane.setVisible(isVisible);
            historyToggle.setText(isVisible ? "Hide History" : "Show History");
            
        });
        historyToggle.setStyle("-fx-font-size: 16px; -fx-base: lightblue;");
        historyToggle.setPrefWidth(120);
        Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.setOnAction(_ -> historyList.getItems().clear());
        HBox historyControls = new HBox(10, historyToggle, clearHistoryButton);
        historyControls.setAlignment(Pos.CENTER_LEFT); 
        VBox unitBoxPane = new VBox(5);
        unitBoxPane.setPadding(new Insets(10));
        unitBox.getItems().addAll("cm to mm", "m to mm", "inches to mm");
        unitBox.setValue("cm to mm"); 
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            
            if (newText.isEmpty() || newText.matches("-?(\\d*\\.?\\d*)?")) {
                return change; 
            }
            return null; 
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        unitInput.setTextFormatter(textFormatter);
        unitInput.setPromptText("Enter value"); 
        unitOutputDisplay.setEditable(false);
        unitOutputDisplay.setFocusTraversable(false); 
        unitOutputDisplay.setPromptText("Result"); 
        unitOutputDisplay.setStyle("-fx-background-color: -fx-control-inner-background-alt;");

        Button convertButton = new Button("Convert");
        convertButton.setOnAction(_ -> convertUnit());

        
        unitBoxPane.getChildren().addAll(
                new Label("Unit Converter"),
                unitInput,          
                unitBox,            
                convertButton,      
                unitOutputDisplay   
        );
        unitBoxPane.setPrefWidth(180);

      
        VBox calculatorPane = new VBox(10, grid);
        calculatorPane.setPadding(new Insets(10));

        HBox mainContent = new HBox(10, calculatorPane, unitBoxPane, historyPane);

        
        VBox root = new VBox(10, historyControls, mainContent);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);

        
        scene.setOnKeyPressed(event -> {
             KeyCode code = event.getCode();
            String key = event.getText();

            if (code == KeyCode.ENTER || key.equals("=")) {
                handleButton("=");
                event.consume();
            } else if (code == KeyCode.BACK_SPACE) {
                String text = display.getText();
                if (text.length() == 1 || (text.length() == 2 && text.startsWith("-"))) {
                    display.setText("0");
                    startNewNumber = true;
                } else if (text.length() > 1) {
                    display.setText(text.substring(0, text.length() - 1));
                }
                 event.consume();
            } else {
                switch (key) {
                    case "+", "-", "*", "/" -> handleButton(key);
                    case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> handleButton(key);
                    case "." -> handleButton(".");
                    case "c", "C" -> handleButton("C");
                }
            }
        });

        stage.setScene(scene);
        stage.sizeToScene();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.show();
    }

    
    private void handleButton(String value) {
        if (display.getText().equals("Error") && !value.equals("C")) {
            return;
        }

        switch (value) {
            case "C":
                display.setText("0");
                firstOperand = 0;
                operator = "";
                startNewNumber = true;
                break;
            case "+/-":
                String currentText = display.getText();
                if (!currentText.isEmpty() && !currentText.equals("0") && !currentText.equals("Error")) {
                    if (currentText.startsWith("-")) {
                        display.setText(currentText.substring(1));
                    } else {
                        display.setText("-" + currentText);
                    }
                }
                break;
            case ".":
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else {
                    if (!display.getText().contains(".")) {
                        display.appendText(".");
                    }
                }
                break;
             case "+", "-", "*", "/": 
                if (!startNewNumber) {
                    if (!operator.isEmpty() && !display.getText().equals("Error")) {
                         handleButton("=");
                         
                         if (!display.getText().equals("Error")) {
                             firstOperand = Double.parseDouble(display.getText());
                         } else {
                             
                             operator = "";
                             startNewNumber = true;
                             return;
                         }
                    } else if (!display.getText().equals("Error")){
                         firstOperand = Double.parseDouble(display.getText());
                    }
                } 

                if (!display.getText().equals("Error")) {
                    operator = value;
                    startNewNumber = true;
                }
                break;
            case "=":
                if (!operator.isEmpty()) {
                    double secondOperand;
                    try {
                         
                         if (display.getText().equals("Error")) return;
                         secondOperand = Double.parseDouble(display.getText());
                    } catch (NumberFormatException e) {
                        display.setText("Error");
                        operator = "";
                        startNewNumber = true;
                        return;
                    }

                    double result = 0;
                    boolean error = false;

                    switch (operator) {
                        case "+": result = firstOperand + secondOperand; break;
                        case "-": result = firstOperand - secondOperand; break;
                        case "*": result = firstOperand * secondOperand; break;
                        case "/":
                            if (secondOperand == 0) {
                                error = true;
                            } else {
                                result = firstOperand / secondOperand;
                            }
                            break;
                        default:
                           error = true;
                           break;
                    }

                    String historyEntry = firstOperand + " " + operator + " " + secondOperand + " = ";

                    if (error || Double.isNaN(result) || Double.isInfinite(result)) {
                        display.setText("Error");
                        historyList.getItems().add(historyEntry + "Error");
                    } else {
                        String resultString;
                        if (result == (long) result) {
                            resultString = String.format("%d", (long) result);
                        } else {
                           
                            resultString = String.format("%s", result);
                        }
                        display.setText(resultString);
                        historyList.getItems().add(historyEntry + resultString);
                        firstOperand = result;
                    }
                    operator = "";
                    startNewNumber = true;
                }
                break;
            default: 
                if (startNewNumber) {
                    display.setText(value);
                    startNewNumber = false;
                } else {
                    
                    if (!(display.getText().equals("0") && value.equals("0"))) {
                       if (display.getText().equals("0") && !value.equals(".")) {
                           display.setText(value); 
                       } else {
                           display.appendText(value);
                       }
                    }
                }
                break;
        }
        historyList.scrollTo(historyList.getItems().size() - 1);
    }
    private void convertUnit() {
        String inputText = unitInput.getText();
        if (inputText.isEmpty() || inputText.equals("-") || inputText.equals(".")) {
             unitOutputDisplay.setText("Invalid input");
             return;
        }
        try {
            double input = Double.parseDouble(inputText);
            double output = switch (unitBox.getValue()) {
                case "cm to mm" -> input * 10;
                case "m to mm" -> input * 1000;
                case "inches to mm" -> input * 25.4;
                default -> 0; 
            };

            String outputString;
            if (output == (long) output) {
                 outputString = String.format("%d", (long) output);
            } else {
                 outputString = String.format("%.4f", output);
            }
            unitOutputDisplay.setText(outputString + " mm");

        } catch (NumberFormatException e) {
            unitOutputDisplay.setText("Invalid input"); 
        }
    }
}