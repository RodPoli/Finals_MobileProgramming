package com.example.machineproblemfour


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment


//class AddExpenseDialog (context: Context) : Dialog(context) {
//
//    init {
//        setCancelable(false)
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.add_expense)
//
//
//    }
//}

//class AddExpenseDialog (context: Context) : Dialog(context) {
//    private var expName: EditText? = null
//    private var expPrice: EditText? = null
//    private var lstTracker: ListView? = null
//    private var listener: DialogListener? = null
//    init {
//        setCancelable(false)
//    }
//    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.add_expense)
//        val builder = AlertDialog.Builder(context)
//        val view: View = layoutInflater.inflate(R.layout.add_expense, null)
//        builder.setView(view)
//            .setTitle("AddExpense")
//            .setNegativeButton("Cancel",
//                DialogInterface.OnClickListener { dialogInterface, i -> })
//            .setPositiveButton("Add Expense",
//                DialogInterface.OnClickListener { dialogInterface, i ->
//                    val eName = expName!!.text.toString()
//                    val ePrice = expPrice!!.text.toString()
//                    listener!!.applyTexts(eName, ePrice)
//                })
//        expName = view.findViewById(R.id.expenseName)
//        expPrice = view.findViewById(R.id.expensePrice)
//        return builder.create()
//
//    }
//
////    override fun onAttach(context: Context) {
////        super.onAttach(context)
////        listener = try {
////            context as DialogListener
////        } catch (e: ClassCastException) {
////            throw ClassCastException(
////                context.toString() +
////                        "must implement DialogListener"
////            )
////        }
////    }
//
//    interface DialogListener {
//        fun applyTexts(eName: String?, ePrice: String?)
//    }
//
//
//}

class AddExpenseDialog : DialogFragment() {
    interface DialogListener {
        fun sendInput(inputName: String, inputPrice: String)
    }

    var dialogListener: DialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView : View = inflater.inflate(R.layout.add_expense, container, false)

        var expName: EditText = rootView.findViewById(R.id.expenseName)
        var expPrice: EditText = rootView.findViewById(R.id.expensePrice)

        var buttonExpenseCancel : Button = rootView.findViewById(R.id.btnExpCancel)
        var buttonExpenseAdd : Button = rootView.findViewById(R.id.btnExpAdd)

        var bundle = Bundle()


        buttonExpenseAdd.setOnClickListener {
//            bundle.putString("bundleEName", expName.text.toString())
//            bundle.putString("bundleEPrice", expPrice.text.toString())

            val inputName: String = expName.text.toString()
            val inputPrice: String = expPrice.text.toString()

            if (inputName != "" && inputPrice != ""){
                dialogListener?.sendInput(inputName, inputPrice)
                dialog!!.dismiss()
            }else{
                dialog!!.dismiss()
            }


        }

        buttonExpenseCancel.setOnClickListener {
            dismiss()
        }

        return rootView

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dialogListener = activity as DialogListener?
        } catch (e: ClassCastException) {
            Log.e(
                "Dialog Fragment", "onAttach: ClassCastException: "
                        + e.message
            )
        }
    }


}
