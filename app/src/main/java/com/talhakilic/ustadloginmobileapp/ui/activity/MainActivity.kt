package com.talhakilic.ustadloginmobileapp.ui.activity


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Switch
import android.content.SharedPreferences
import com.talhakilic.ustadloginmobileapp.databinding.ActivityMainBinding
import java.util.Locale
import com.talhakilic.ustadloginmobileapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE)
        val langCode = sharedPreferences.getString("AppLanguage", "tr") ?: "tr"
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonEnglish.setOnClickListener {
            changeLanguageAndContinue("en")
        }

        binding.buttonTurkish.setOnClickListener {
            changeLanguageAndContinue("tr")
        }
    }

    private fun changeLanguageAndContinue(language: String) {
        val sharedPreferences = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("AppLanguage", language).apply()

        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
