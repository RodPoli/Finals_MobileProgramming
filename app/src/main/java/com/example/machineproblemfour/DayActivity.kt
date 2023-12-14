package com.example.machineproblemfour

import DatabaseClass2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DayActivity : AppCompatActivity(), AddExpenseDialog.DialogListener {

    override fun sendInput(inputName: String, inputPrice: String) {
        Log.d("ExpenseTracker", "sendInput: got the input: $inputName and $inputPrice")

        expenseArrayList =  setInputToListView(inputName, inputPrice)
        eItemAdapter = ExpenseItemAdapter(this@DayActivity, uid!!, whatYear, whatMonth, whatDay, expenseArrayList!!)
        lstTracker?.adapter = eItemAdapter

    }
    private lateinit var whatMonth: String
    private lateinit var whatYear: String
    private lateinit var whatDay: String
    private var lstTracker: ListView? = null
    private var expenseArrayList:ArrayList<ExpenseList>? = null
    private var eItemAdapter:ExpenseItemAdapter? = null
    private var totalPrice:Int = 0
    lateinit var databaseReference: DatabaseReference
    private lateinit var  currentFirebaseUser : FirebaseUser
    private lateinit var  auth: FirebaseAuth

    var uid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        val btnAddExpense = findViewById<ImageButton>(R.id.buttonAddExpense)
        var btnBack = findViewById<ImageButton>(R.id.buttonMenu)
        lstTracker = findViewById(R.id.listTracker)
        expenseArrayList = ArrayList()
        auth= FirebaseAuth.getInstance()

        whatMonth = intent.getStringExtra("Month").toString()
        whatYear = intent.getStringExtra("Year").toString()
        whatDay = intent.getStringExtra("Day").toString()

        var LabelDate = findViewById<TextView>(R.id.labelDate)
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!;
        val user = Firebase.auth.currentUser



        if (user != null) {
            user?.let {
                uid = it.uid
                Log.i("UserID", "User ID: $uid")
            }
        } else {
            Toast.makeText(this,"No User", Toast.LENGTH_SHORT).show()
        }


        fetchExistingExpenses()
        LabelDate.text = whatMonth + " " + whatDay + ", " + whatYear
        btnBack.setOnClickListener{
            val intent= Intent(this,MonthlyActivity::class.java)
            startActivity(intent)
        }
        btnAddExpense.setOnClickListener {

            val fragmentObject = AddExpenseDialog()
            fragmentObject.isCancelable = false

            fragmentObject.show(supportFragmentManager, "Add_Expense")

        }

    }
    private fun setInputToListView(iName: String, iPrice: String): ArrayList<ExpenseList>{
        expenseArrayList?.add(ExpenseList(iName, iPrice.toInt()))
        addToDatabase(iName, iPrice)
        return expenseArrayList!!
    }

    private fun addToDatabase(inpName: String, inpPrice: String): Void? {
        databaseReference = FirebaseDatabase.getInstance().getReference("DatabaseName")
        var lblTotalExpense = findViewById<TextView>(R.id.labelTotalExpense)
        var expenseDatabaseClass = DatabaseClass2(inpName, inpPrice.toString())
        var expenseDataKey = databaseReference.push().key

        try {
            if (expenseDataKey != null) {
                totalPrice += inpPrice.toInt()
                var totalText = totalPrice.toString() + " pesos"
                databaseReference.child(uid!!).child(whatYear).child(whatMonth).child(whatDay).child(expenseDataKey).setValue(expenseDatabaseClass)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Success - Add", Toast.LENGTH_SHORT).show()
                    }
                lblTotalExpense.setText(totalText)
                databaseReference.child(uid!!).child(whatYear).child(whatMonth).child(whatDay).child("TotalExpense").setValue(totalPrice)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Total expense updated", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: Exception) {
            Log.i("setBudget", e.message.toString())
        }
        return null
    }
    private fun fetchExistingExpenses() {
        databaseReference = FirebaseDatabase.getInstance().getReference("DatabaseName")

        if (uid == null) {
            Log.e("ExpenseTracker", "User ID is null. Cannot fetch expenses.")
            Toast.makeText(this, "User ID is null. Cannot fetch expenses.", Toast.LENGTH_SHORT).show()
            return
        }

        val expensesRef = databaseReference.child(uid!!).child(whatYear).child(whatMonth).child(whatDay)

        expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    expenseArrayList?.clear()

                    for (expenseSnapshot in dataSnapshot.children) {
                        // Exclude the "TotalExpense" node
                        if (expenseSnapshot.key != "TotalExpense") {
                            val expense = expenseSnapshot.getValue(DatabaseClass2::class.java)

                            if (expense != null) {
                                // Add existing expenses to the list
                                expenseArrayList?.add(ExpenseList(expense.nameOfExpense, expense.priceOfExpense.toInt()))
                            }
                        }
                    }

                    // Update the ListView
                    eItemAdapter = ExpenseItemAdapter(this@DayActivity, uid!!, whatYear, whatMonth, whatDay, expenseArrayList!!)
                    lstTracker?.adapter = eItemAdapter
                    eItemAdapter?.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("ExpenseTracker", "Error while processing data: ${e.message}", e)
                    Toast.makeText(this@DayActivity, "Error fetching expenses. Check logs for details.", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ExpenseTracker", "Failed to read value. Error: ${databaseError.message}", databaseError.toException())
                Toast.makeText(this@DayActivity, "Failed to read expenses. Check logs for details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun refreshData(toBeSubtracted: Int) {
        var lblTotalExpenseSubtract = findViewById<TextView>(R.id.labelTotalExpense)
        totalPrice -= toBeSubtracted
        var totalTextSub = totalPrice.toString() + " pesos"
        lblTotalExpenseSubtract.setText(totalTextSub)

    }
}