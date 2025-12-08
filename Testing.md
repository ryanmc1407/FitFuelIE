# Testing Overview

This project includes unit tests and instrumentation tests to verify the core features of the application. The focus of the testing was to ensure that the ViewModels, repository layer, and Room database behave as expected and produce consistent results.

## Unit Tests

### MealRepositoryTest
This test class checks the logic in the MealRepository without using a real database. The MealDao is mocked to isolate repository behaviour.
The tests cover:
- Inserting, updating, and deleting meals
- Retrieving meals and lists of meals
- Filtering meals between date ranges
- Summing calories, protein, carbohydrates, and fat

### DashboardViewModelTest
This class tests the DashboardViewModel, which combines data from multiple repositories.
Covered areas include:
- Calculating nutrition summaries
- Calculating training statistics
- Loading the user profile
- Handling empty or missing data
- Verifying StateFlow updates

### OnboardingViewModelTest
These tests focus on onboarding logic and user input handling.
Tested behaviour includes:
- Updating and validating weight input
- Selecting goals
- Selecting training frequency

## Instrumentation Tests

### FitFuelDatabaseTest
This test suite uses an in-memory Room database to verify the database layer.
The tests check:
- Inserting and retrieving meals
- Updating existing rows
- Deleting meals
- Querying meals between two dates
- Summing nutrition values over a date range
- Testing Date and Enum TypeConverters

## Summary
The tests in this project focus on the most important areas:
- ViewModel logic
- Repository interactions
- Room database queries and storage

These tests help confirm that the core functionality of the app works reliably and consistently.
