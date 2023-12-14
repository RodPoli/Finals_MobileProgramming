package com.example.machineproblemfour

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ExpenseItemAdapter(
    var context: Context,
    var uid: String,
    var whatYear: String,
    var whatMonth: String,
    var whatDay: String,
    var expenseArrayList: ArrayList<ExpenseList>
) : BaseAdapter() {
    private var mContext = context
    private var mList = expenseArrayList
    lateinit var dbReference: DatabaseReference

    fun removeFromDatabase(inpName: String, inpPrice: String, position: Int) {
        dbReference = FirebaseDatabase.getInstance().getReference("DatabaseName")
        val list = ArrayList<String>()
        dbReference.child(uid).child(whatYear).child(whatMonth).child(whatDay).get().addOnSuccessListener { task ->
            for (document in task.children) {
                list.add(document.key.toString())
            }
            dbReference.child(uid).child(whatYear).child(whatMonth).child(whatDay).child(list[position]).removeValue().addOnSuccessListener {
                Log.d("ExpenseTracker", "removeWorks")
            }
        }

        var expenseToBeSubtracted = inpPrice.toInt()
        var currentTotal = 0
        dbReference.child(uid).child(whatYear).child(whatMonth).child(whatDay).child("TotalExpense").get().addOnSuccessListener { task ->
            currentTotal = task.getValue().toString().toInt()
            var totalPriceSub = currentTotal - expenseToBeSubtracted

            dbReference.child(uid).child(whatYear).child(whatMonth).child(whatDay).child("TotalExpense").setValue(totalPriceSub).addOnSuccessListener {
                Log.d("ExpenseTracker", "Total expense subtracted")
            }
            (context as DayActivity).refreshData(expenseToBeSubtracted)
        }
    }

    override fun getCount(): Int {
        return expenseArrayList.size
    }

    override fun getItem(position: Int): Any {
        return expenseArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.list_item, null)

        var getExpName: TextView = view.findViewById(R.id.expName)
        var getExpPrice: TextView = view.findViewById(R.id.expPrice)
        var deleteExpense: ImageButton = view.findViewById(R.id.btnExpCancel)

        var expenseList: ExpenseList = expenseArrayList[position]
        getExpName.text = expenseList.exName
        getExpPrice.text = expenseList.exPrice.toString()

        deleteExpense.setOnClickListener {
            removeFromDatabase(getExpName.text.toString(), getExpPrice.text.toString(), position)
            expenseArrayList.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }
}
