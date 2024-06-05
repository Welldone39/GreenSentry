package com.wildan.greensentry

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.wildan.greensentry.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var aboutButton: Button
    private lateinit var aboutClassification: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        aboutButton = findViewById(R.id.aboutButton)
        aboutButton.setOnClickListener(this)

        aboutClassification = findViewById(R.id.classificationButton)
        aboutClassification.setOnClickListener(this)
        playAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.language -> {
                Toast.makeText(applicationContext, "Language", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.Donasi -> {
                val donasiIntent = Intent(this, DonasiActivity::class.java)
                startActivity(donasiIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.aboutButton -> {
                val aboutAct = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(aboutAct)
            }

            R.id.classificationButton -> {
                val Classification = Intent(this@MainActivity, ClassificationActivity::class.java)
                startActivity(Classification)
            }
        }
    }
    private fun playAnimation() {

        ObjectAnimator.ofFloat(binding.previewImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val fadeInAnimation = ObjectAnimator.ofFloat(binding.aboutButton, View.ALPHA, 0f, 1f).apply {
            duration = 1000 // Durasi animasi dalam milidetik (ms)

        }

        val fadeInAnimation2 = ObjectAnimator.ofFloat(binding.classificationButton, View.ALPHA, 0f, 1f).apply {
            duration = 1000 // Durasi animasi dalam milidetik (ms)

        }

        fadeInAnimation.start() // Mulai animasi
        fadeInAnimation2.start() // Mulai animasi
    }

}


