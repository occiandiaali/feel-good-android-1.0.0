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
        "You don't have to be great to start. But you have to start to be great. Zig Ziglar",
        "We may encounter many defeats but we must not be defeated. Maya Angelou",
        "Walt Disney said. The way to get started is to quit talking and get doing",
        "The pessimist sees difficulty in every opportunity. The optimist sees the opportunity in every difficulty. Winston Churchill",
        "Will Rogers said. Don't let yesterday take up too much of today",
        "It's not whether you get knocked down it's whether you get up. Vince Lombardi",
        "Rob Siltanen said. People who are crazy enough to think they can change the world are the ones who do",
        "Henry Ford said. Whether you think you can, or think you cannot. You're right.",
        "The only limit to our realization of tomorrow is our doubts of today. Franklin D Roosevelt",
        "Robert Schuller said. Todays accomplishments were yesterdays impossibilities",
        "You are never too old to set another goal. Or to dream a new dream. C S Lewis",
        "John Wooden said. Things work out best for those who make the best of how things work out"
    )
   // private lateinit var strings: Array<String>

    private lateinit var animOut: Animation
    private lateinit var animIn: Animation

    private lateinit var am: AudioManager

    var randomiser: Random = Random()

    // variables for pitch and pace controls
    var pitch: Float = 1.0f
    var pace: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val stringsTxt = applicationContext.assets.open("textfile.txt").bufferedReader().use {
//            it.readText()
//        }
//        strings = arrayOf(stringsTxt)

        mTTS = TextToSpeech(this, this)
        animIn = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in)
        animOut = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)

        am = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // see: https://www.youtube.com/watch?v=KAx5OAld8Hg

        // fab to launch spoken words
        floatingActionButton.setOnClickListener {
                spokenWords()
        } // fab listener


    } // on create


    override fun onInit(status: Int) = if (status == TextToSpeech.SUCCESS) {
        var localeLang = mTTS.setLanguage(Locale.UK)

        // listener to monitor when speech happens
        val speechListener = object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                // fab icon changes to play when speech ends
                floatingActionButton.setImageResource(android.R.drawable.ic_media_play)
            }

            override fun onError(utteranceId: String?) {}

            override fun onStart(utteranceId: String?) {
                // animation rolls and fab icon changes to pause when speech begins
                speakerImageView.startAnimation(animOut)
                speakerImageView.startAnimation(animIn)
                floatingActionButton.setImageResource(android.R.drawable.ic_media_pause)
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
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
        } // set on pitchbar change

        // listener to monitor when pace changes
        val paceBarListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pace = progress / (paceBar.max / 2).toFloat()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        } // set on pacebar change

        var volListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        } // vol listener

        // set all 3 callbacks
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



