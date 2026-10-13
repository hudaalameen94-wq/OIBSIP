# Task 3 — Calculator App

**Name:** Huda Saeed Othman Al-ameen
**Track:** Android App Development
**Program:** Oasis Infobyte Summer Internship Program (OIBSIP)

A clean, fully functional Android calculator that handles basic arithmetic with a responsive button grid, styled in a dark theme with orange operator buttons.

## Screenshot

CalculatorApp/screenshots/calculator.png

## Features

- [x] Display `TextView` showing current input and result
- [x] Number buttons (0–9) and decimal point
- [x] Operator buttons: `+`, `−`, `×`, `÷`
- [x] Equals (`=`) button to evaluate
- [x] Clear (`C`) button to reset
- [x] Backspace (`⌫`) button to delete the last character
- [x] Division-by-zero handling: displays `Error`
- [x] Button grid built with `GridLayout` in XML
- [x] No crashes on rapid or repeated button taps

**Extras:** sign toggle `(-)`, chained operations (`2 + 3 + 4 = 9`), single decimal point per number, clean result formatting (`5` instead of `5.0`, `0.3` instead of `0.30000000000000004`).

## Tech Stack

- Android Studio
- Java
- XML (`LinearLayout` + `GridLayout`)

## How It Works

- `activity_main.xml` defines the layout: a display on top and a 4-column `GridLayout` of buttons below. Layout weights make it adapt to different screen sizes.
- `themes.xml` holds shared button styles (`CalcButtonNumber`, `CalcButtonFunction`, `CalcButtonOperator`) and `colors.xml` holds the color palette.
- `MainActivity.java` finds each view with `findViewById`, listens for taps with `View.OnClickListener`, and runs the calculation logic (`handleOperator`, `calculate`, `format`).
- Division by zero returns `NaN` internally, which is caught and shown as `Error`. A `try/catch` around the click handler prevents crashes from unexpected input sequences.

## Run the Project

1. Clone this repository.
2. Open the `AndroidDev-Task3-Calculator` folder in Android Studio.
3. Wait for Gradle to sync.
4. Run on an emulator or a physical device (USB debugging enabled).

## What I Learned

- Building layouts in XML and connecting them to Java with `findViewById`
- Handling clicks with `OnClickListener`
- Reusing styles and colors across many views
- Defensive coding: guarding against empty input and division by zero
