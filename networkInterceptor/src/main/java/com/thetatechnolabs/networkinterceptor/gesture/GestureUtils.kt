package com.thetatechnolabs.networkinterceptor.gesture

import android.app.Activity
import androidx.annotation.Keep

@Keep
object GestureUtils {
    /**
     * Call [registerSensorListener] on an ActivityContext to make the BottomSheet to show on motion detection
     */
    fun Activity.registerSensorListener() {
        ShakeDetector.getInstance(this).registerShakeDetector()

    }

    /**
     * Call [unRegisterSensorListener] on an ActivityContext to make the BottomSheet to no appear on motion detection
     */
    fun Activity.unRegisterSensorListener() {
        ShakeDetector.getInstance(this).unRegisteredDetector()
    }
}