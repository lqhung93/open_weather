package com.hung.openweather.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.hung.openweather.R
import com.hung.openweather.adapters.WeatherAdapter
import com.hung.openweather.base.BaseFragment
import com.hung.openweather.databinding.FragmentMainBinding
import com.hung.openweather.utils.Constants
import com.hung.openweather.viewmodels.MainViewModel
import java.util.*

class MainFragment : BaseFragment() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var recyclerViewState: Parcelable? = null
    private var progressDialog: AlertDialog? = null

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    companion object {
        private const val RECYCLER_VIEW_STATE = "RECYCLER_VIEW_STATE"
        private const val RECORD_AUDIO_REQUEST_CODE = 12345
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

        initVoiceRecognition()

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

        mainViewModel.onSpeakButtonClicked.observe(viewLifecycleOwner, Observer {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                requestRecordAudioPermission();
            }
        })
    }

    private fun requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
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

    private fun initVoiceRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
                mainViewModel.queryEditTextLiveData.postValue("")
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
                mainViewModel.queryEditTextLiveData.postValue(data?.get(0) ?: "")
            }

            override fun onPartialResults(bundle: Bundle?) {

            }

            override fun onEvent(p0: Int, p1: Bundle?) {

            }

        })

        binding.btnSpeak.setOnTouchListener { view, motionEvent ->
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening()
                    mainViewModel.hintEditTextLiveData.postValue(getString(R.string.place))
                } else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    speechRecognizer.startListening(speechRecognizerIntent)
                    mainViewModel.hintEditTextLiveData.postValue(getString(R.string.listening))
                }
            }
            false
        }

        mainViewModel.hintEditTextLiveData.postValue(getString(R.string.place))
    }
}