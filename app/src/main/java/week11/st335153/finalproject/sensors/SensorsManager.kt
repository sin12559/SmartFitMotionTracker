package week11.st335153.finalproject.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

/**
 * Handles step counter, step detector, accelerometer (movement) and light sensor.
 * All sensors are optional – if the emulator/device doesn't have them,
 * this will just keep values at 0 and never crash.
 */
class SensorsManager(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val accelSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val lightSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _movement = MutableStateFlow(0f)
    val movement: StateFlow<Float> = _movement.asStateFlow()

    private val _light = MutableStateFlow(0f)
    val light: StateFlow<Float> = _light.asStateFlow()

    private var stepBase: Float? = null

    fun start() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {

            Sensor.TYPE_STEP_COUNTER -> {
                val total = event.values.firstOrNull() ?: return
                if (stepBase == null) {
                    stepBase = total
                }
                val diff = total - (stepBase ?: total)
                _steps.value = diff.toInt().coerceAtLeast(0)
            }

            Sensor.TYPE_STEP_DETECTOR -> {
                val step = event.values.firstOrNull() ?: return
                if (step > 0f) {
                    _steps.value = _steps.value + step.toInt()
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)

                val gravity = SensorManager.GRAVITY_EARTH
                val movementForce = (magnitude - gravity).coerceAtLeast(0f)

                _movement.value = movementForce
            }

            Sensor.TYPE_LIGHT -> {
                _light.value = event.values.firstOrNull() ?: 0f
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
