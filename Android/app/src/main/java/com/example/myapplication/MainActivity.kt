package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication112.DbHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)


        if (isLoggedIn) {
            val intent = Intent(this, BookActivity::class.java)
            startActivity(intent)
            finish()
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val cutout = insets.displayCutout
            if (cutout != null) {
                v.setPadding(cutout.safeInsetLeft, cutout.safeInsetTop, cutout.safeInsetRight, cutout.safeInsetBottom)
            } else {
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            insets
        }

        val linkToReg: TextView = findViewById(R.id.regLink)
        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val button: Button = findViewById(R.id.loginButton)

        userLogin.inputType = InputType.TYPE_CLASS_TEXT

        linkToReg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val errorData: TextView = findViewById(R.id.errorData)

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
            } else {
                val db = DbHelper(this, null)
                val isAuth = db.getUser(login, password)

                if (isAuth) {
                    errorData.visibility = View.GONE
                    userPassword.text.clear()
                    userLogin.text.clear()
                    with(sharedPref.edit()) {
                        putBoolean("is_logged_in", true)
                        apply()
                    }

                    val intent = Intent(this, BookActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    userPassword.text.clear()
                    userLogin.text.clear()
                    errorData.visibility = View.VISIBLE
                }
                Toast.makeText(this, "Вход", Toast.LENGTH_SHORT).show()
            }
        }
    }
}