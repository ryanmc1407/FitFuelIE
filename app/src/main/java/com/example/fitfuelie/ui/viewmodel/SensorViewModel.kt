package com.example.fitfuelie.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.sensors.FitFuelSensorManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * SensorViewModel
 * 
 * ViewModel that manages sensor data for the FitFuel app.
 * Handles step counting, activity detection, and shake gestures.
 * 
 * Responsibilities:
 * - Collect step count data from step counter sensor
 * - Track daily step progress toward goals
 * - Monitor accelerometer for activity level detection
 * - Detect shake gestures for quick actions
 * - Persist daily step baseline for accurate daily counting
 * 
 * State is exposed via StateFlow for reactive UI updates.
 */
class SensorViewModel(context: Context) : ViewModel() {

    // Sensor manager instance
    private val sensorManager = FitFuelSensorManager(context)

    // Private mutable state
    private val _dailySteps = MutableStateFlow(0)
    private val _activityLevel = MutableStateFlow(ActivityLevel.SEDENTARY)
    private val _shakeDetected = MutableStateFlow(false)
    private val _sensorsAvailable = MutableStateFlow(
        SensorAvailability(
            hasStepCounter = sensorManager.hasStepCounter(),
            hasAccelerometer = sensorManager.hasAccelerometer()
        )
    )

    // Public read-only state
    val dailySteps: StateFlow<Int> = _dailySteps.asStateFlow()
    val activityLevel: StateFlow<ActivityLevel> = _activityLevel.asStateFlow()
    val shakeDetected: StateFlow<Boolean> = _shakeDetected.asStateFlow()
    val sensorsAvailable: StateFlow<SensorAvailability> = _sensorsAvailable.asStateFlow()

    // Baseline step count (steps at start of day)
    private var stepBaseline: Int? = null

    init {
        // Start collecting sensor data
        collectStepCount()
        collectAccelerometerData()
        collectShakeGestures()
    }

    /**
     * Collects step count data from the step counter sensor.
     * 
     * The step counter returns total steps since device reboot.
     * I calculate daily steps by subtracting the baseline value
     * (steps at the start of the day).
     */
    private fun collectStepCount() {
        viewModelScope.launch {
            sensorManager.getStepCountFlow()?.collect { totalSteps ->
                // Initialize baseline on first reading
                if (stepBaseline == null) {
                    stepBaseline = totalSteps
                }

                // Calculate daily steps (total - baseline)
                val dailyStepCount = totalSteps - (stepBaseline ?: 0)
                _dailySteps.value = dailyStepCount.coerceAtLeast(0)
            }
        }
    }

    /**
     * Collects accelerometer data to determine activity level.
     * 
     * Activity level is categorized based on sustained acceleration magnitude:
     * - SEDENTARY: Low movement (< 10 m/s²)
     * - LIGHT: Moderate movement (10-13 m/s²)
     * - MODERATE: Active movement (13-16 m/s²)
     * - VIGOROUS: High intensity movement (> 16 m/s²)
     */
    private fun collectAccelerometerData() {
        viewModelScope.launch {
            sensorManager.getAccelerometerFlow()
                ?.map { magnitude ->
                    // Categorize activity level based on magnitude
                    when {
                        magnitude < 10f -> ActivityLevel.SEDENTARY
                        magnitude < 13f -> ActivityLevel.LIGHT
                        magnitude < 16f -> ActivityLevel.MODERATE
                        else -> ActivityLevel.VIGOROUS
                    }
                }
                ?.collect { level ->
                    _activityLevel.value = level
                }
        }
    }

    /**
     * Collects shake gesture events from the accelerometer.
     * 
     * When a shake is detected, the shakeDetected state is set to true.
     * The UI can observe this state to trigger quick actions like:
     * - Quick add meal
     * - Quick add training session
     * - Refresh data
     */
    private fun collectShakeGestures() {
        viewModelScope.launch {
            sensorManager.getShakeDetectionFlow()?.collect { shakeDetected ->
                if (shakeDetected) {
                    _shakeDetected.value = true
                    
                    // Auto-reset shake detection after a short delay
                    kotlinx.coroutines.delay(500)
                    _shakeDetected.value = false
                }
            }
        }
    }

    /**
     * Resets the daily step baseline to the current total.
     * Should be called at the start of each new day.
     */
    fun resetDailySteps() {
        stepBaseline = _dailySteps.value + (stepBaseline ?: 0)
        _dailySteps.value = 0
    }

    /**
     * Manually sets the step baseline (useful for testing or manual adjustment).
     * 
     * @param baseline The new baseline step count
     */
    fun setStepBaseline(baseline: Int) {
        stepBaseline = baseline
    }
}

/**
 * Data class representing sensor availability on the device.
 * 
 * @property hasStepCounter Whether the device has a step counter sensor
 * @property hasAccelerometer Whether the device has an accelerometer sensor
 */
data class SensorAvailability(
    val hasStepCounter: Boolean,
    val hasAccelerometer: Boolean
)

/**
 * Enum representing different activity levels based on accelerometer data.
 * 
 * Used to provide users with real-time feedback about their activity intensity.
 */
enum class ActivityLevel {
    SEDENTARY,  // Minimal movement
    LIGHT,      // Light activity (walking slowly)
    MODERATE,   // Moderate activity (brisk walking)
    VIGOROUS    // High intensity activity (running, jumping)
}
