package com.example.machineproblemfour

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class MonthlyBudget : AppCompatActivity() {

    private lateinit var databaseReference : DatabaseReference
    private lateinit var  currentFirebaseUser : FirebaseUser

    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_budget)
        auth= FirebaseAuth.getInstance()
        var monthlyBudget = findViewById<EditText>(R.id.edtMnthBdgt)
        var setBudget = findViewById<Button>(R.id.btnSetBudg)
        var goBack = findViewById<Button>(R.id.btnBackToMonth)
        var textMonth = findViewById<TextView>(R.id.txtMonth)
        var textYear = findViewById<TextView>(R.id.txtYear)
        var uid : String? = null
        val whatMonth = intent.getStringExtra("Month")
        val whatYear = intent.getStringExtra("Year")
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!;
        databaseReference = FirebaseDatabase.getInstance().getReference("DatabaseName")

        textMonth.text = whatMonth
        textYear.text = whatYear
        val user = Firebase.auth.currentUser
        if (user != null) {
            user?.let {
                uid = it.uid
                Log.i("UserID", "User ID: $uid")
            }
        } else {
            Toast.makeText(this,"No User", Toast.LENGTH_SHORT).show()
        }

        setBudget.setOnClickListener{
            try {

                if (whatYear.isNullOrBlank()){
                    Toast.makeText(this,"Year is empty", Toast.LENGTH_SHORT).show()
                }
                else{
                if (whatMonth.isNullOrBlank())
                {
                    Toast.makeText(this,"Month is empty", Toast.LENGTH_SHORT).show()
                } else
                {
                val id = databaseReference.push().key!!
                val budgetMonthly = Integer.parseInt(monthlyBudget.getText().toString())
                val monthBudget = DatabaseClass(budgetMonthly)


                databaseReference.child(uid!!).child(whatYear).child(whatMonth).setValue(monthBudget)
                    .addOnCompleteListener {
                        Toast.makeText(this,"Monthly Budget set", Toast.LENGTH_SHORT).show()
                        goBackIntent()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                    }
                }
                }
            } catch (e: Exception)
            {
                Log.i("setBudget", e.message.toString())
            }

        }

        goBack.setOnClickListener {
            goBackIntent()
        }
    } // End of CreateView
    fun goBackIntent(){
        val intent= Intent(this,MonthlyActivity::class.java)
        startActivity(intent)
    }
}