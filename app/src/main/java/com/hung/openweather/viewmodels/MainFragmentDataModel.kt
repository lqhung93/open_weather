package com.hung.openweather.viewmodels

import android.text.Editable
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.hung.openweather.R

class MainFragmentDataModel {

    val isButtonEnabled = MutableLiveData<Boolean>()

    fun onQueryTextChanged(text: Editable?) {
        isButtonEnabled.postValue(!text.isNullOrEmpty() && text.length >= MIN_SEARCH_CHARACTER)
    }

    companion object {

        private const val MIN_SEARCH_CHARACTER = 3

        @JvmStatic
        @BindingAdapter("enableButton")
        fun enableGetWeatherButton(button: Button, enabled: Boolean) {
            if (enabled) {
                button.setBackgroundColor(ContextCompat.getColor(button.context, R.color.purple_500))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(button.context, R.color.gray))
            }

            button.isEnabled = enabled
        }
    }
}