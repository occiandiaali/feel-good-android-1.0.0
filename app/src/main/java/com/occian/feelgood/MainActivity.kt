package com.occian.feelgood

import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var mTTS: TextToSpeech
    private val strings = arrayOf(
        "Look before you leap",
        "A good name is worth more than silver or gold",
        "A stitch in time saves nine",
        "Many are mad few are roaming",
        "Reach for the skies",
        "You can do it"
    )
    private lateinit var animOut: Animation
    private lateinit var animIn: Animation

    private lateinit var am: AudioManager

    var randomiser: Random = Random()

    // variables for seekbar controls
    var pitch: Float = 1.0f
    var pace: Float = 1.0f
    var vol: Float = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTTS = TextToSpeech(this, this)
        animIn = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in)
        animOut = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)

        am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var maxVolume: Int = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var currentVolume: Int = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        //var volumeBar = findViewById<SeekBar>(R.id.volBar)
        // see: https://www.youtube.com/watch?v=KAx5OAld8Hg

        // fab to launch spoken words
        floatingActionButton.setOnClickListener {
                spokenWords()
        } // fab listener

        // controls seekbars
        //pitchBar.setOnSeekBarChangeListener(seekBarListener)

    } // on create


    override fun onInit(status: Int) = if (status == TextToSpeech.SUCCESS) {
        var localeLang = mTTS.setLanguage(Locale.UK)

        // listener to monitor when speech happens
        val speechListener = object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                floatingActionButton.setImageResource(android.R.drawable.ic_media_play)
            }

            override fun onError(utteranceId: String?) {}

            override fun onStart(utteranceId: String?) {
                speakerImageView.startAnimation(animOut)
                speakerImageView.startAnimation(animIn)
                floatingActionButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        } // speech listener set onutteranceprogresslistener
        mTTS.setOnUtteranceProgressListener(speechListener)

        // listener to monitor pitch change
        val pitchBarListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pitch = progress / (pitchBar.max / 2).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        } // set on seekbar change

        // listener to monitor when pace changes
        val paceBarListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pace = progress / (paceBar.max / 2).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        } // set on seekbar change


        var volListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        pitchBar.setOnSeekBarChangeListener(pitchBarListener)
        paceBar.setOnSeekBarChangeListener(paceBarListener)
        volBar.setOnSeekBarChangeListener(volListener)

        if (localeLang == TextToSpeech.LANG_MISSING_DATA || localeLang == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(applicationContext, "Language not supported", Toast.LENGTH_SHORT).show()
        } else {}
    } else {
        Toast.makeText(applicationContext, "Initialisation FAILED!", Toast.LENGTH_SHORT).show()
    } // on init


    private fun spokenWords() {
        mTTS.setPitch(pitch)
        mTTS.setSpeechRate(pace)


        var randS = strings.get(randomiser.nextInt(strings.size))
        mTTS.speak(randS, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
    } // spoken words

    override fun onPause() {
        if (mTTS.isSpeaking) {
            //
            mTTS.stop()
        }
        super.onPause()
    } // on pause

    override fun onDestroy() {
        if (mTTS != null) {
            mTTS.stop()
            mTTS.shutdown()
        }
        super.onDestroy()
    } // on destroy

} // class



