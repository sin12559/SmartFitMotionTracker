package week11.st335153.finalproject.dashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorsManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _steps = MutableStateFlow(0f)
    val steps = _steps.asStateFlow()

    private val _light = MutableStateFlow(0f)
    val light = _light.asStateFlow()

    private val _movement = MutableStateFlow(0f)
    val movement = _movement.asStateFlow()

    private var lastAccel = FloatArray(3) { 0f }

    fun start() {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> _steps.value = event.values[0]
            Sensor.TYPE_LIGHT -> _light.value = event.values[0]

            Sensor.TYPE_ACCELEROMETER -> {
                val dx = event.values[0] - lastAccel[0]
                val dy = event.values[1] - lastAccel[1]
                val dz = event.values[2] - lastAccel[2]

                val movementValue = (dx * dx + dy * dy + dz * dz)
                _movement.value = movementValue

                lastAccel = event.values.clone()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
