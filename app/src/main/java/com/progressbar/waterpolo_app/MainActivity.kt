package com.progressbar.waterpolo_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Directly launch GamesActivity
        val intent = Intent(this, GamesActivity::class.java)
        startActivity(intent)
        // Close MainActivity
        finish()
    }
}
