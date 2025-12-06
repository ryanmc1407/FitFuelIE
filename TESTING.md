# FitFuelIE Testing Documentation

## Overview

This document describes the testing strategy and test cases for the FitFuelIE Android application.

## Test Structure

### Unit Tests (`src/test/`)
Unit tests verify individual components in isolation using mocked dependencies.

**Location**: `app/src/test/java/com/example/fitfuelie/`

**Test Files**:
- `ui/viewmodel/DashboardViewModelTest.kt` - Tests for DashboardViewModel
- `data/repository/MealRepositoryTest.kt` - Tests for MealRepository

**Dependencies**:
- JUnit 4 - Test framework
- Mockito - Mocking framework
- Kotlin Coroutines Test - Testing coroutines and Flows
- Turbine - Testing Kotlin Flows

### Instrumentation Tests (`src/androidTest/`)
Instrumentation tests run on Android devices/emulators to test Android-specific components.

**Location**: `app/src/androidTest/java/com/example/fitfuelie/`

**Test Files**:
- `data/local/FitFuelDatabaseTest.kt` - Tests for Room database operations

**Dependencies**:
- AndroidX Test - Android testing framework
- Room Testing - Room database testing utilities
- Espresso - UI testing framework

## Running Tests

### Run All Unit Tests
```bash
./gradlew test
```

### Run All Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Run Specific Test Class
```bash
# Unit test
./gradlew test --tests DashboardViewModelTest

# Instrumentation test
./gradlew connectedAndroidTest --tests FitFuelDatabaseTest
```

### Run Tests from Android Studio
1. Right-click on test file or test method
2. Select "Run 'TestName'"

## Test Coverage

### DashboardViewModelTest
Tests the main dashboard ViewModel functionality:
- ✅ Nutrition summary calculation from multiple meals
- ✅ Training statistics aggregation
- ✅ User profile loading
- ✅ Empty data state handling
- ✅ StateFlow emissions

**Key Test Cases**:
- `test nutrition summary calculates correctly` - Verifies calorie, protein, carbs, and fat totals
- `test training stats calculates correctly` - Verifies session count and time aggregation
- `test user profile loads correctly` - Verifies profile data retrieval
- `test empty nutrition data returns zeros` - Verifies graceful handling of no data

### MealRepositoryTest
Tests the meal repository CRUD operations:
- ✅ Create - Insert new meals
- ✅ Read - Retrieve meals by ID, date range, all meals
- ✅ Update - Modify existing meals
- ✅ Delete - Remove meals by entity or ID
- ✅ Aggregate - Calculate nutrition totals

**Key Test Cases**:
- `test getAllMeals returns all meals from DAO` - Verifies retrieval of all meals
- `test getMealsBetweenDates filters by date range` - Verifies date filtering
- `test insertMeal calls DAO insert` - Verifies meal creation
- `test updateMeal calls DAO update` - Verifies meal modification
- `test deleteMeal calls DAO delete` - Verifies meal deletion
- `test getTotalCaloriesBetweenDates aggregates correctly` - Verifies calorie summation

### FitFuelDatabaseTest
Tests the Room database and DAOs:
- ✅ Database creation and schema
- ✅ DAO insert, query, update, delete operations
- ✅ Type converters (Date, Enum)
- ✅ Query correctness (date ranges, aggregations)
- ✅ Data persistence

**Key Test Cases**:
- `testInsertAndRetrieveMeal` - Verifies basic CRUD operations
- `testGetAllMeals` - Verifies retrieval of multiple meals
- `testUpdateMeal` - Verifies meal updates persist
- `testDeleteMeal` - Verifies meal deletion
- `testGetMealsBetweenDates` - Verifies date range queries
- `testGetTotalCaloriesBetweenDates` - Verifies aggregation queries
- `testTypeConvertersForDate` - Verifies Date type converter
- `testTypeConvertersForEnum` - Verifies Enum type converter

## Test Best Practices

### Unit Tests
1. **Isolation**: Use mocks to isolate the system under test
2. **Fast**: Unit tests should run quickly (< 1 second each)
3. **Deterministic**: Tests should produce the same result every time
4. **Clear naming**: Use descriptive test names that explain what is being tested

### Instrumentation Tests
1. **In-memory database**: Use Room's in-memory database for fast, isolated tests
2. **Clean state**: Create fresh database before each test
3. **Real Android components**: Test actual Android framework behavior
4. **Realistic data**: Use realistic test data that matches production scenarios

## Expected Test Results

All tests should pass with 100% success rate:

```
Unit Tests: 13 tests passed
Instrumentation Tests: 10 tests passed
Total: 23 tests passed
```

## Continuous Integration

Tests are designed to run in CI/CD pipelines:
- Unit tests run on every commit
- Instrumentation tests run on pull requests
- Test reports generated in `app/build/reports/tests/`

## Future Test Additions

Planned test coverage expansions:
- [ ] MealPlannerViewModel tests
- [ ] TrainingCalendarViewModel tests
- [ ] TrainingSessionRepository tests
- [ ] UI tests for Dashboard screen
- [ ] Navigation tests
- [ ] WorkManager worker tests
- [ ] Sensor integration tests
