## Author

Huda Saeed Othman Al-Amin

# Unit Converter (Android)

**Track:** Android App Development | **Task 1** | Oasis Infobyte Internship Program (OIBSIP)

A simple Android app that converts a value between common units of measurement. The user types a number, chooses a category, picks the source and target units, and taps **Convert** to see the result with unit labels.

## Features

- Numeric input field (decimal and negative numbers allowed)
- Category selector: **Length, Weight, Volume, Temperature**
- Two dropdowns (Spinners) for the source unit and the target unit
- Changing the category reloads both unit dropdowns and clears the old result
- Convert button that calculates and displays the result, e.g. `12.5 cm = 4.92126 in`
- Input validation with Toast messages for:
  - an empty input
  - an input that is not a valid number
  - a temperature below absolute zero

## Supported units

| Category | Units |
|---|---|
| Length | Millimetre, Centimetre, Metre, Kilometre, Inch, Foot, Yard, Mile |
| Weight | Milligram, Gram, Kilogram, Tonne, Ounce, Pound |
| Volume | Millilitre, Litre, Cubic metre, Teaspoon (US), Tablespoon (US), Cup (US), Gallon (US) |
| Temperature | Celsius, Fahrenheit, Kelvin |

## How it works

- **Length, weight and volume:** every unit stores a factor relative to a base unit (metre, gram, litre). The value is converted to the base unit, then to the target unit.
- **Temperature:** converted through Celsius using the standard formulas.
- Unit names, symbols and factors live in tables at the top of `MainActivity.java`, so new units are easy to add.

## Tech stack

- Android Studio
- Java
- XML layouts

## Project structure

```
app/src/main/java/com/example/unitconverter/MainActivity.java   // app logic
app/src/main/res/layout/activity_main.xml                       // screen layout
app/src/main/res/values/strings.xml                             // text resources
```

## Screenshots

AndroidDev-Task1-UnitConverter\unitconverter\Screenshots

## How to run

1. Clone the repository and open the `AndroidDev-Task1-UnitConverter` folder in Android Studio.
2. Wait for Gradle sync to finish.
3. Run the app on an emulator or a connected Android device.


