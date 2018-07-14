package com.anwesh.uiprojects.decreasingcircsweepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.dcsview.DCSView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : DCSView = DCSView.create(this)
        fullScreen()
        view.addOnCompletionListener {
            Toast.makeText(this, "${it} animation completed", Toast.LENGTH_SHORT).show()
        }
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}