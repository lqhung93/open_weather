package com.hung.openweather.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Base for every Fragment in the App.
 */
abstract class BaseFragment : Fragment() {

    open fun hideKeyboard() {
        val view = if (activity?.currentFocus == null) View(context) else activity?.currentFocus
        view?.let {
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (inputManager?.isActive == true) {
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}