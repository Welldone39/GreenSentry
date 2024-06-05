package com.wildan.greensentry

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DonasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donasi)

        val buttonDonate = findViewById<Button>(R.id.konfirmasi)
        buttonDonate.setOnClickListener{
            val url = "https://docs.google.com/forms/d/e/1FAIpQLSdYYmwQ0xJbSnf-vIkEClP1T5F90xJtOIfgPYCE2htgUP4dgA/viewform"
            val intent =Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}