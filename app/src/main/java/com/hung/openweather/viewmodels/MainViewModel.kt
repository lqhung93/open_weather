package com.hung.openweather.viewmodels

import android.content.Context
import android.text.Editable
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hung.openweather.R
import com.hung.openweather.adapters.WeatherAdapter
import com.hung.openweather.data.disk.DiskDataSource
import com.hung.openweather.data.memory.MemoryDataSource
import com.hung.openweather.data.network.NetworkDataSource
import com.hung.openweather.models.WeatherData
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.repository.MainRepository
import com.hung.openweather.utils.Constants
import com.hung.openweather.utils.testing.OpenForTesting
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

@OpenForTesting
class MainViewModel(private val repository: MainRepository) : ViewModel() {

    val disposable = CompositeDisposable()

    val isButtonEnabled = MutableLiveData<Boolean>()
    val weatherData = MutableLiveData<List<WeatherData>?>()
    val onGetWeatherState = MutableLiveData<Pair<String, String?>>()

    private var editTextValue: String = ""

    val queryEditTextLiveData = MutableLiveData<String>()
    val hintEditTextLiveData = MutableLiveData<String>()
    val onSpeakButtonClicked = MutableLiveData<View>()

    fun onSpeakButtonClicked(view: View) {
        onSpeakButtonClicked.postValue(view)
    }

    fun onQueryTextChanged(text: Editable?) {
        editTextValue = text.toString()
        isButtonEnabled.postValue(!text.isNullOrEmpty() && text.length >= MIN_SEARCH_CHARACTER)
    }

    fun onGetWeather(view: View) {
        disposable.add(
            getDailyForecast(editTextValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<WeatherResponse>() {

                    override fun onStart() {
                        onGetWeatherState.postValue(Pair(Constants.ApiState.STARTED, null))
                    }

                    override fun onNext(t: WeatherResponse) {
                        weatherData.postValue(t.list)
                    }

                    override fun onComplete() {
                        onGetWeatherState.postValue(Pair(Constants.ApiState.COMPLETED, null))
                    }

                    override fun onError(e: Throwable) {
                        weatherData.postValue(arrayListOf())
                        onGetWeatherState.postValue(Pair(Constants.ApiState.ERROR, e.localizedMessage))
                    }
                })
        )
    }

    fun getDailyForecast(query: String): Observable<WeatherResponse> {
        return repository.getDailyForecast(query)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    companion object {

        fun builder(context: Context): ViewModelProvider.Factory {
            val memoryDataSource = MemoryDataSource(context)
            val diskDataSource = DiskDataSource(context)
            val networkDataSource = NetworkDataSource(context)

            val repository = MainRepository(memoryDataSource, diskDataSource, networkDataSource)
            return MainViewModelFactory(repository)
        }

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

        @JvmStatic
        @BindingAdapter("data")
        fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, items: List<WeatherData>?) {
            if (recyclerView.adapter is WeatherAdapter) {
                (recyclerView.adapter as WeatherAdapter).setData(items ?: arrayListOf())
            }
        }
    }

}