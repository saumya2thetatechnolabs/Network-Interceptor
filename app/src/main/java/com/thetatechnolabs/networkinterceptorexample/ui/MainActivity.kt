package com.thetatechnolabs.networkinterceptorexample.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.thetatechnolabs.foundation.BaseActivity
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.registerSensorListener
import com.thetatechnolabs.networkinterceptor.gesture.GestureUtils.unRegisterSensorListener
import com.thetatechnolabs.networkinterceptor.utils.TestUtils.showNetworkLog
import com.thetatechnolabs.networkinterceptorexample.R
import com.thetatechnolabs.networkinterceptorexample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        this.registerSensorListener()

        lifecycleScope.launch {
            mainViewModel.getWeather.observe(this@MainActivity) {
                binding {
                    textResponse.text = it
                }
            }
        }

        binding.rootLayout.setOnClickListener {
            showNetworkLog()
            mainViewModel.makeRetrofitRequest()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unRegisterSensorListener()
    }
}