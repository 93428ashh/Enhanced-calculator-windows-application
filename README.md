Enhanced JavaFX Calculator
A desktop calculator application built using Java and JavaFX. It supports standard arithmetic operations and includes a calculation history panel and a basic unit converter. Features include addition, subtraction, multiplication, division, decimal input, number negation, and a clear function. The history panel allows users to view, toggle, and clear past calculations. The unit converter supports cm to mm, m to mm, and inches to mm conversions, with input validation. The calculator includes full keyboard support (numbers, operators, Enter, Backspace, and C). The application is packaged as a standalone Windows installer using jpackage.

Table of Contents
Features

Screenshot

Prerequisites

Getting Started

Usage

Building from Source

Future Enhancements

License

Screenshot


Prerequisites
Java Development Kit (JDK): Version 17 or later (Download JDK)

JavaFX SDK: Version 24.0.1 or compatible (Download JavaFX)

WiX Toolset v3.x (required for Windows installers, must be in system PATH)

Getting Started
1. Using the Packaged Application
Download the EnhancedCalculator-1.0.exe installer from the Releases section and run it to install the app.

2. Running from Source
Clone the repository:

bash
Copy code
git clone https://github.com/93428ashh/enhanced-calculator-windows-application.git
cd enhanced-javafx-calculator
Ensure all prerequisites are met. Open the project in a JavaFX-compatible IDE or run from the command line:

csharp
Copy code
javac --module-path "path/to/javafx-sdk-24.0.1/lib" --add-modules javafx.controls,javafx.graphics,javafx.base -d bin calculatorapp/CalculatorAppEnhanced.java
java --module-path "path/to/javafx-sdk-24.0.1/lib;bin" --add-modules javafx.controls,javafx.graphics,javafx.base calculatorapp.CalculatorAppEnhanced
Usage
Use buttons or keyboard for input. Toggle the history panel with the "Show/Hide History" button. Use "Clear History" to remove all records. The unit converter can be used for basic conversions between centimeters, meters, inches, and millimeters.

Keyboard Shortcuts
Key	Function
0-9	Number input
+ - * /	Arithmetic ops
Enter	Equals
Backspace	Delete last char
C	Clear input

Building from Source
Compile the source:
javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.graphics,javafx.base -d bin calculatorapp/CalculatorAppEnhanced.java

Create a manifest file MANIFEST.MF with Main-Class: calculatorapp.CalculatorAppEnhanced

Create JAR: jar cfm CalculatorApp.jar MANIFEST.MF -C bin .

Move the JAR to a dist folder

Package using jpackage:

jpackage --type exe --input dist --name EnhancedCalculator --main-jar CalculatorApp.jar --main-class calculatorapp.CalculatorAppEnhanced --module-path "C:\javafx-jmods-21" --add-modules javafx.controls,javafx.graphics,javafx.base --win-menu --win-shortcut --icon assets/icon.ico --app-version 1.0
Future Enhancements
Potential improvements include scientific functions (square root, power, trigonometry), memory functions, more unit conversions, and theming/layout customization.
