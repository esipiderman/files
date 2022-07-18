package com.example.files

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.example.files.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        var ourViewType = 0
        var ourSpan = 1
    }
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val file = getExternalFilesDir(null)!!

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameMainContainer, FileFragment(file.path))
        transaction.commit()

    }
}