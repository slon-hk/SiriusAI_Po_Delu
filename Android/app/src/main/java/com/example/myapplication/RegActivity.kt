package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication112.DbHelper
import com.example.myapplication112.User

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val userPasswordSec: EditText = findViewById(R.id.user_password_sec)
        val userEmail: EditText = findViewById(R.id.user_email)
        val button: Button = findViewById(R.id.regButton)

        val linkToLogin: TextView = findViewById(R.id.loginLink)

        linkToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        button.setOnClickListener(){
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val passwordSec = userPasswordSec.text.toString().trim()
            val email = userEmail.text.toString().trim()


            if(login == "" || password == "" || passwordSec == "" ||email == ""){
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
            }
            else if(password != passwordSec){
                Toast.makeText(this, "Пароль не совпадает", Toast.LENGTH_SHORT).show()
            }
            else{
                val user = User(login, password, email)

                val db = DbHelper(this, null)
                db.addUser(user)
                Toast.makeText(this, "Пользоватль $login добавлен", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                userPassword.text.clear()
                userLogin.text.clear()
                userEmail.text.clear()
                userPasswordSec.text.clear()

            }
        }
    }
}