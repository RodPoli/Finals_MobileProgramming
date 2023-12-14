package com.example.machineproblemfour

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.view.View


class MainActivity : AppCompatActivity() {

    lateinit var databaseReference : DatabaseReference
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth= FirebaseAuth.getInstance()


        //Edit
        var editEmailLogin = findViewById<EditText>(R.id.edtEmailLogin)
        var editPasswordLogin = findViewById<EditText>(R.id.edtPasswordLogin)
        //Button
        var buttonLogin = findViewById<Button>(R.id.btnLogin)
        //Text View
        var btnSignUp = findViewById<TextView>(R.id.txtSignup)

        btnSignUp.setOnClickListener {
            val intent = Intent(this,Signup::class.java)
            // start your next activity
            startActivity(intent)
            finish()
        }

        fun loginUser(email: String, password: String) {

        }

        //Account Login
        buttonLogin.setOnClickListener {
            try {
                var email = editEmailLogin.text.toString()
                var password = editPasswordLogin.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login successful
                            val user = auth.currentUser
                            val intent= Intent(this,MonthlyActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Login failed
                            Log.w("Login", "signInWithEmail:failure", task.exception)
                        }
                    }
            }
            catch (e: Exception){
                Log.e("error_Login", e.message.toString())
            }
        }
    }
}