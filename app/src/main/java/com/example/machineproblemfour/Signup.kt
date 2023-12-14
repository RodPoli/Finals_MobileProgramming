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

class Signup : AppCompatActivity() {

    lateinit var databaseReference : DatabaseReference
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth= FirebaseAuth.getInstance()

        //Edit
        var editUserSignin = findViewById<EditText>(R.id.edtUserSignUp)
        var editPasswordSignin = findViewById<EditText>(R.id.edtPasswordSignup)
        //Button
        var btnRegister = findViewById<Button>(R.id.btnRegister)
        //Text View
        var btnLogin = findViewById<TextView>(R.id.txtLogin)
        btnLogin.setOnClickListener {
            try{
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            catch (e: Exception){
                Log.e("error_goToLogin", e.message.toString())
            }
        }
        btnRegister.setOnClickListener {
            try{
                auth.createUserWithEmailAndPassword(editUserSignin.text.toString(), editPasswordSignin.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = auth.currentUser
                            val intent= Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Registration failed
                            Log.w("Registration", "createUserWithEmail:failure", task.exception)
                        }
                    }
            }
            catch (e: Exception){
                Log.e("error_Signin", e.message.toString())
            }
        }

    }
}