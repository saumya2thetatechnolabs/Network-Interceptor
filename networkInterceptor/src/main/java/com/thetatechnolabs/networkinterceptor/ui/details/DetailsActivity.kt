package com.thetatechnolabs.networkinterceptor.ui.details

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.ActivityDetailsBinding
import com.thetatechnolabs.networkinterceptor.ui.details.adapters.DetailsAdapters
import com.thetatechnolabs.networkinterceptor.ui.details.menu.ShareBottomSheet
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.getPage
import timber.log.Timber
import java.io.File

internal class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var detailsAdapters: DetailsAdapters

    private var clickItem: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        clickItem = intent?.extras?.getParcelable(NetworkCallListFragment.TAG)
        setUpLayoutTheme()

        detailsAdapters =
            DetailsAdapters(clickItem, supportFragmentManager, lifecycle)
        binding.viewPager.adapter = detailsAdapters
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->
            tab.text = position.getPage().name
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuShare -> {
                ShareBottomSheet.newInstance(clickItem)
                    .show(supportFragmentManager, ShareBottomSheet.TAG)
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteFile(getString(R.string.file_name))
        Handler(Looper.getMainLooper()).post {
            if (File(this.cacheDir, getString(R.string.file_name)).exists()) {
                Timber.e("Log File has not been deleted")
            } else {
                Timber.d("Log File has been deleted")
            }
        }
    }

    private fun setUpLayoutTheme() {
        val activeColor = ContextCompat.getColor(
            this@DetailsActivity,
            if (clickItem?.response?.isSuccessful == true) {
                R.color.green_emerald
            } else {
                R.color.red_fire_engine
            }
        )
        supportActionBar?.apply {
            title = "${clickItem?.info?.url}"
            setBackgroundDrawable(ColorDrawable(activeColor))
        }
        binding.tabLayout.apply {
            setSelectedTabIndicatorColor(activeColor)
            tabRippleColor = ColorStateList.valueOf(activeColor)
            setTabTextColors(
                ContextCompat.getColor(this@DetailsActivity, R.color.grey),
                activeColor
            )
        }
    }
}