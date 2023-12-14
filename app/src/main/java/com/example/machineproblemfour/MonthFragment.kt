package com.example.machineproblemfour

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import kotlin.properties.Delegates


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthFragment : Fragment() {
    lateinit var monthname : String
    lateinit var sendmonth : String
    lateinit var sendyear : String
    lateinit var sendday : String
    lateinit var budg : String
    lateinit var uid : String
    private lateinit var databaseReference : DatabaseReference
    private lateinit var  currentFirebaseUser : FirebaseUser

    private lateinit var  auth: FirebaseAuth
    var calendar : CalendarView? = view?.findViewById(R.id.calendarView)
    var dateText : TextView? = view?.findViewById(R.id.txtDate)
    var budgetText : TextView? = view?.findViewById(R.id.txtBudgetNum)
    var spentText : TextView? = view?.findViewById(R.id.txtSpentNum)
    var btnBudget : Button? = view?.findViewById(R.id.btnGotoDay)
    var btnDay : Button? = view?.findViewById(R.id.btnGoDay)
    var remainText : TextView? = view?.findViewById(R.id.txtRemainNum)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = Firebase.auth.currentUser
        if (user != null) {
            user?.let {
                uid = it.uid
               Log.i("UID","User ID: " + uid)
            }
        } else {
            Log.i("UIS","No User")
        }

    }
    private fun setBudget(){
        btnBudget?.setOnClickListener {
            activity?.let{
                val intent = Intent(it, MonthlyBudget::class.java)
                intent.putExtra("Month", sendmonth)
                intent.putExtra("Year", sendyear)
                it.startActivity(intent)
            }
        }
    }

    private fun gotoDay(){
        btnDay?.setOnClickListener {
            activity?.let{
                val intent2 = Intent(it, DayActivity::class.java)
                intent2.putExtra("Month", sendmonth)
                intent2.putExtra("Year", sendyear)
                intent2.putExtra("Day", sendday)
                it.startActivity(intent2)
            }
        }
    }
    private fun setupCalendar() {
        databaseReference = FirebaseDatabase.getInstance().getReference("DatabaseName")
        calendar?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.

            when (month + 1){
                1 -> monthname = "January"
                2 -> monthname = "February"
                3 -> monthname = "March"
                4 -> monthname = "April"
                5 -> monthname = "May"
                6 -> monthname = "June"
                7 -> monthname = "July"
                8 -> monthname = "August"
                9 -> monthname = "September"
                10 -> monthname = "October"
                11 -> monthname = "November"
                12 -> monthname = "December"
            }
            sendmonth = monthname
            sendyear = year.toString()
            sendday = dayOfMonth.toString()
            val date = monthname + " " + dayOfMonth.toString() + " " + year
            dateText?.text = date
            Log.i("calendar", " setOnDateChangeListener is working")
            try {
                databaseReference.child(uid).child(sendyear).child(sendmonth).get().addOnSuccessListener {
                        snapshot ->
                    val monthBudget = snapshot.child("monthBudget").getValue(Int::class.java)
                    if (monthBudget != null) {
                        budg = monthBudget.toString()
                        Log.i("MONTHBUDGET", "Got value $budg")
                        budgetText?.text = budg
                    } else {
                        Log.w("MONTHBUDGET", "monthBudget is null")
                        budgetText?.text = "Budget Number"
                    }
                }.addOnFailureListener{
                    Log.e("MONTHBUDGET", "Error getting data", it)
                }

            }
            catch (e: Exception){
                Log.e("MONTHBUDGET", e.message.toString())
            }
            try {
                databaseReference.child(uid).child(sendyear).child(sendmonth).get().addOnSuccessListener { snapshot ->
                    var totalExpenseForMonth = 0

                    // Iterate through each day and sum up TotalExpense values
                    for (daySnapshot in snapshot.children) {
                        val totalExpense = daySnapshot.child("TotalExpense").getValue(Int::class.java)
                        totalExpenseForMonth += totalExpense ?: 0
                    }

                    // Update the UI with the total expense for the month
                    spentText?.text = totalExpenseForMonth.toString()
                }.addOnFailureListener {
                    Log.e("SpentText", "Error getting data", it)
                }
            } catch (e: Exception) {
                Log.e("SpentText", e.message.toString())
            }
            try {
                val remainingBudget = budg.toInt() - spentText?.text.toString().toInt()
                remainText?.text = remainingBudget.toString()

            } catch (e: Exception) {
                Log.e("remainText", e.message.toString())
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        calendar = requireView().findViewById(R.id.calendarView)
        dateText = requireView().findViewById(R.id.txtDate)
        spentText = requireView().findViewById(R.id.txtSpentNum)
        budgetText = requireView().findViewById(R.id.txtBudgetNum)
        btnBudget = requireView().findViewById(R.id.btnGotoDay)
        btnDay = requireView().findViewById(R.id.btnGoDay)
        remainText = requireView().findViewById(R.id.txtRemainNum)

        setupCalendar()
        setBudget()
        gotoDay()
    }
}