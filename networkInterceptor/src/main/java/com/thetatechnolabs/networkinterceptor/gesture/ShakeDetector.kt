package com.thetatechnolabs.networkinterceptor.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.fragment.app.FragmentActivity
import com.thetatechnolabs.networkinterceptor.utils.TestUtils.showNetworkLog
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Gets triggered when the device's been shaken
 * @param [context] is required to initialize Sensor Manager
 * @property [registerShakeDetector] register listener once sensor manager is available.
 * Please note that registering shake detector where sensor manager is not available can throw Exception
 * @property [unRegisteredDetector] clean up resource once component no longer exists
 * @author Saumya Macwan (Created on 29th April 2022)
 */
internal class ShakeDetector constructor(private val context: Context) {
    private var sensorManager: SensorManager? = null
    private var acceleration = 10f
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            sensorEvent?.let { event ->
                lastAcceleration = currentAcceleration
                currentAcceleration =
                    sqrt((event.values[0].pow(2) + event.values[1].pow(2) + event.values[2].pow(2)).toDouble()).toFloat()
                // Adding delta i.e currentAcceleration - lastAcceleration to acceleration
                acceleration = acceleration.times(0.9F).plus(currentAcceleration - lastAcceleration)

                // If acceleration satisfies this condition we will consider that the device has been shaken
                if (acceleration > 15) {
                    Timber.tag("ShakeDetector").i("Shake is detected")
                    (context as FragmentActivity).showNetworkLog()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun registerShakeDetector() {
        // Initializing sensor service via context
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // registering sensor listener if sensor manager is available
        sensorManager?.let {
            it.registerListener(
                sensorListener,
                it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Timber.tag("ShakeDetector").d("Sensor listener is registered")
        } ?: run {
            Timber.tag("ShakeDetector").e("Sensor Manager is not found")
        }
    }

    // cleaning up resources
    fun unRegisteredDetector() {
        sensorManager?.unregisterListener(sensorListener)
        sensorManager = null
        Timber.tag("ShakeDetector").d("Sensor listener is unregistered")
    }

    companion object {
        /**
         * making [ShakeDetector] singleton here
         */
        @SuppressLint("StaticFieldLeak")
        private var sharedInstance: ShakeDetector? = null

        fun getInstance(context: Context): ShakeDetector =
            sharedInstance ?: synchronized(this) {
                return sharedInstance ?: ShakeDetector(context).also {
                    sharedInstance = it
                }
            }
    }
}