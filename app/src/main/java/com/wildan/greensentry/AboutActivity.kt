package com.wildan.greensentry

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wildan.greensentry.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
    }
    private fun playAnimation() {


        val fadeInAnimation = ObjectAnimator.ofFloat(binding.pengertian, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }

        val fadeInAnimation2 = ObjectAnimator.ofFloat(binding.headbarText, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }

        val fadeInAnimation3 = ObjectAnimator.ofFloat(binding.greensentry, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }
        val fadeInAnimation4 = ObjectAnimator.ofFloat(binding.visi, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }
        val fadeInAnimation5 = ObjectAnimator.ofFloat(binding.misi, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }
        val fadeInAnimation6 = ObjectAnimator.ofFloat(binding.ourTeam, View.ALPHA, 0f, 1f).apply {
            duration = 500 // Durasi animasi dalam milidetik (ms)

        }

        AnimatorSet().apply {
            playSequentially(
                fadeInAnimation,
                fadeInAnimation2,
                fadeInAnimation3,
                fadeInAnimation4,
                fadeInAnimation5,
                fadeInAnimation6
            )
            startDelay = 50 // Delay sebelum animasi pertama dimulai
            start()
        }
    }
}