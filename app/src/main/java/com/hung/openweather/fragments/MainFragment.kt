package com.hung.openweather.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.hung.openweather.R
import com.hung.openweather.adapters.WeatherAdapter
import com.hung.openweather.base.BaseFragment
import com.hung.openweather.databinding.FragmentMainBinding
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.viewmodels.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MainFragment : BaseFragment() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var recyclerViewState: Parcelable? = null

    companion object {
        private const val MIN_SEARCH_CHARACTER = 3
        private const val RECYCLER_VIEW_STATE = "RECYCLER_VIEW_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this, MainViewModel.builder(requireContext())).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = WeatherAdapter()

        binding.rvWeatherForecast.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.rvWeatherForecast.adapter = adapter

        binding.btnGetWeather.setOnClickListener {
            mainViewModel.getDailyForecast(binding.etPlace.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<WeatherResponse>() {

                    override fun onNext(t: WeatherResponse) {
                        t.list?.let {
                            adapter.setData(it)
                        }
                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
        }

        binding.etPlace.addTextChangedListener {
            enableGetWeatherButton(binding.etPlace)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rvWeatherForecast.layoutManager?.onRestoreInstanceState(recyclerViewState)
        enableGetWeatherButton(binding.etPlace)
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
        super.onDestroyView()
        _binding = null
    }

    private fun enableGetWeatherButton(view: EditText) {
        val enabled = !view.text.isNullOrEmpty() && view.text.length >= MIN_SEARCH_CHARACTER
        if (enabled) {
            binding.btnGetWeather.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        } else {
            binding.btnGetWeather.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }

        binding.btnGetWeather.isEnabled = enabled
    }
}