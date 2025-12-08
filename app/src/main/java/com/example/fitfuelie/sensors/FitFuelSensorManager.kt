package com.example.fitfuelie.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

/**
 * FitFuelSensorManager
 *
 * Manages two primary sensors for the FitFuel app:
 * 1. Step Counter - Tracks total steps taken by the user
 * 2. Accelerometer - Detects device movement and shake gestures
 */
class FitFuelSensorManager(context: Context) {

    // Android's system sensor manager
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as AndroidSensorManager
    
    // Sensor references
    private val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /**
     * Checks if the device has a step counter sensor.
     * 
     * @return true if step counter is available, false otherwise
     */
    fun hasStepCounter(): Boolean = stepCounterSensor != null

    /**
     * Checks if the device has an accelerometer sensor.
     * 
     * @return true if accelerometer is available, false otherwise
     */
    fun hasAccelerometer(): Boolean = accelerometerSensor != null

    /**
     * Provides a Flow of step count data from the step counter sensor.
     * 
     * The step counter returns the total number of steps taken since the
     * last device reboot.
     * @return Flow<Int> step count values
     */
    fun getStepCountFlow(): Flow<Int>? {
        // Return null if step counter is not available
        if (stepCounterSensor == null) return null

        return callbackFlow {
            // Create sensor event listener
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        // Step counter returns total steps as a float
                        val steps = it.values[0].toInt()
                        trySend(steps)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Step counter accuracy changes are not critical for our use case
                }
            }

            // Register the listener with SENSOR_DELAY_NORMAL (suitable for step counting)
            sensorManager.registerListener(
                listener,
                stepCounterSensor,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )

            // Unregister listener when Flow is cancelled
            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    /**
     * Provides a Flow of accelerometer data for detecting device movement.
     * 
     *  acceleration  values that can be used to detect:
     * - Shake gestures (high magnitude spikes)
     * - Activity level (sustained high magnitude)
     * - Device orientation changes
     * 
     * @return Flow<Float>  acceleration magnitude
     */
    fun getAccelerometerFlow(): Flow<Float>? {
        // Return null if accelerometer is not available
        if (accelerometerSensor == null) return null

        return callbackFlow {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        // Calculate acceleration magnitude from x, y, z components
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]
                        
                        // Magnitude = sqrt(x² + y² + z²)
                        val magnitude = sqrt(x * x + y * y + z * z)
                        trySend(magnitude)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Accelerometer accuracy changes are not critical
                }
            }

            // Register with SENSOR_DELAY_UI (suitable for gesture detection)
            sensorManager.registerListener(
                listener,
                accelerometerSensor,
                AndroidSensorManager.SENSOR_DELAY_UI
            )

            // Unregister listener when Flow is cancelled
            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    /**
     * Detects shake gestures from accelerometer data.
     * 
     * A shake is detected when the acceleration magnitude exceeds a threshold
     *. This can be used to trigger quick actions like
     * adding a meal or starting a workout.
     * 
     * @param threshold Acceleration threshold for shake detection
     * @return Flow<Boolean> emitting true when shake is detected
     */
    fun getShakeDetectionFlow(threshold: Float = 18.0f): Flow<Boolean>? {
        val accelerometerFlow = getAccelerometerFlow() ?: return null

        return callbackFlow {
            var lastShakeTime = 0L
            val shakeDebounceMs = 1000L // Prevent multiple shake detections within 1 second

            accelerometerFlow.collect { magnitude ->
                val currentTime = System.currentTimeMillis()
                
                // Detect shake if magnitude exceeds threshold and debounce period has passed
                if (magnitude > threshold && (currentTime - lastShakeTime) > shakeDebounceMs) {
                    lastShakeTime = currentTime
                    trySend(true)
                }
            }

            awaitClose { }
        }
    }
}
