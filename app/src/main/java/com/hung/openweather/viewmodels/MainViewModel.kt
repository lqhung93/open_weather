package com.hung.openweather.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hung.openweather.App
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
import java.util.*

@OpenForTesting
class MainViewModel(private val repository: MainRepository) : ViewModel() {

    val disposable = CompositeDisposable()

    val isButtonEnabled = MutableLiveData<Boolean>()
    val weatherData = MutableLiveData<List<WeatherData>?>()
    val onGetWeatherState = MutableLiveData<Pair<String, String?>>()

    private var editTextValue: String = ""

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(App.instance)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    val queryEditTextLiveData = MutableLiveData<String>()
    val hintEditTextLiveData = MutableLiveData<String>()

    val onTouchListener = OnTouchListener { view, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            speechRecognizer.stopListening()
            hintEditTextLiveData.postValue(App.instance.getString(R.string.place))
        }
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            speechRecognizer.startListening(speechRecognizerIntent)
            hintEditTextLiveData.postValue(App.instance.getString(R.string.listening))
        }
        false
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

    fun onCreate() {
        hintEditTextLiveData.postValue(App.instance.getString(R.string.place))

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
                queryEditTextLiveData.postValue("")
            }

            override fun onRmsChanged(p0: Float) {

            }

            override fun onBufferReceived(p0: ByteArray?) {

            }

            override fun onEndOfSpeech() {


            }

            override fun onError(p0: Int) {


            }

            override fun onResults(bundle: Bundle?) {
                val data: ArrayList<String>? = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                queryEditTextLiveData.postValue(data?.get(0) ?: "")
            }

            override fun onPartialResults(bundle: Bundle?) {

            }

            override fun onEvent(p0: Int, p1: Bundle?) {

            }

        })
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

        @JvmStatic
        @BindingAdapter("onTouchListener")
        fun setOnTouchListener(view: View, onTouchListener: OnTouchListener?) {
            if (onTouchListener != null) {
                view.setOnTouchListener(onTouchListener)
            }
        }
    }

}