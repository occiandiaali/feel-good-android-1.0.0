package com.occian.feelgood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
    var randomiser: Random = Random()
    var paused: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTTS = TextToSpeech(this, this)
        animIn = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in)
        animOut = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)

        floatingActionButton.setOnClickListener {
           // if (paused) {
               // paused = false
                //floatingActionButton.setImageResource(android.R.drawable.ic_media_pause)
                spokenWords()
//                val animIn: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in)
//                val animOut: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)
//                speakerImageView.startAnimation(animOut)
//                speakerImageView.startAnimation(animIn)
                //paused = true
            //}
//            else {
//                    paused = true
//                    floatingActionButton.setImageResource(android.R.drawable.ic_media_play)
//            }
        } // fab listener

    } // on create

    override fun onInit(status: Int) = if (status == TextToSpeech.SUCCESS) {
        var localeLang = mTTS.setLanguage(Locale.UK)
        val speechListener = object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                floatingActionButton.setImageResource(android.R.drawable.ic_media_play)
            }

            override fun onError(utteranceId: String?) {
                TODO("Not yet implemented")
            }

            override fun onStart(utteranceId: String?) {
                speakerImageView.startAnimation(animOut)
                speakerImageView.startAnimation(animIn)
                floatingActionButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
        mTTS.setOnUtteranceProgressListener(speechListener)
        if (localeLang == TextToSpeech.LANG_MISSING_DATA || localeLang == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(applicationContext, "Language not supported", Toast.LENGTH_SHORT).show()
        } else {}
    } else {
        Toast.makeText(applicationContext, "Initialisation FAILED!", Toast.LENGTH_SHORT).show()
    } // on init



    private fun spokenWords() {
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
            //floatingActionButton.setImageResource(android.R.drawable.ic_media_play)
            mTTS.shutdown()
        }
        super.onDestroy()
    } // on destroy
} // class

