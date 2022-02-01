package com.hung.openweather

import android.os.Bundle
import com.hung.openweather.base.BaseActivity
import com.hung.openweather.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}