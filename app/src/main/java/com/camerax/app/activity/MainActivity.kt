package com.camerax.app.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.camerax.app.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initItem()
    }
    fun initItem() {
        val btnFirebase = findViewById<Button>(R.id.btnFirebase)
        val btnInstapp = findViewById<Button>(R.id.btnInstapp)
        val btnKeluar = findViewById<Button>(R.id.btnKeluar)

        btnFirebase.setOnClickListener() {
            val intent = Intent(this, FirebaseActivity::class.java)
            startActivity(intent)
        }

        btnInstapp.setOnClickListener() {
            val intent = Intent(this, InstappActivity::class.java)
            startActivity(intent)
        }
    }
}