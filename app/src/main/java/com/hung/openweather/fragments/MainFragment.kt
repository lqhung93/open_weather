package com.hung.openweather.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.hung.openweather.R
import com.hung.openweather.adapters.WeatherAdapter
import com.hung.openweather.base.BaseFragment
import com.hung.openweather.databinding.FragmentMainBinding
import com.hung.openweather.utils.Constants
import com.hung.openweather.viewmodels.MainViewModel

class MainFragment : BaseFragment() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var recyclerViewState: Parcelable? = null
    private var progressDialog: AlertDialog? = null

    companion object {
        private const val RECYCLER_VIEW_STATE = "RECYCLER_VIEW_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this, MainViewModel.builder(requireContext())).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mainViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoadingView()

        binding.rvWeatherForecast.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.rvWeatherForecast.adapter = WeatherAdapter()

        mainViewModel.onGetWeatherState.observe(viewLifecycleOwner, Observer {
            when (it.first) {
                Constants.ApiState.STARTED -> {
                    hideKeyboard()
                    showLoadingView(true)
                }
                Constants.ApiState.COMPLETED -> {
                    showLoadingView(false)
                }
                Constants.ApiState.ERROR -> {
                    showLoadingView(false)
                    Toast.makeText(requireContext(), it.second.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.rvWeatherForecast.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        recyclerViewState = savedInstanceState?.getParcelable(RECYCLER_VIEW_STATE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        recyclerViewState = binding.rvWeatherForecast.layoutManager?.onSaveInstanceState()
        outState.putParcelable(RECYCLER_VIEW_STATE, recyclerViewState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        mainViewModel.disposable.clear()
        showLoadingView(false)
        super.onDestroyView()
        _binding = null
    }

    private fun initLoadingView() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        progressDialog = builder.create()
    }

    private fun showLoadingView(show: Boolean) {
        if (show) {
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }
}