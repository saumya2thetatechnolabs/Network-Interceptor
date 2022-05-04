package com.thetatechnolabs.networkinterceptorexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thetatechnolabs.networkinterceptor.databinding.ActivityMainBinding
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.registerSensorListener
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.unRegisterSensorListener
import com.thetatechnolabs.networkinterceptorexample.utils.NetworkUtils.networkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        this.registerSensorListener()

        binding.rootLayout.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    this@MainActivity.networkService().getWeather()
                } catch (exception: Exception) {
                    Timber.e(exception.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unRegisterSensorListener()
    }
}