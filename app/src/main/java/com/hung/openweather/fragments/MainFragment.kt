package com.hung.openweather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.hung.openweather.adapters.WeatherAdapter
import com.hung.openweather.base.BaseFragment
import com.hung.openweather.databinding.FragmentMainBinding
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.viewmodels.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainFragment : BaseFragment() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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
            mainViewModel.getDailyForecast()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherResponse>() {
                    override fun onSuccess(t: WeatherResponse) {
                        t.list?.let {
                            adapter.setData(it)
                        }

                    }

                    override fun onError(e: Throwable) {

                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}