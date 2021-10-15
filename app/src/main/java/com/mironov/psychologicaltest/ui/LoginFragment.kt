package com.mironov.psychologicaltest.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.widget.Button
import android.content.Intent
import android.widget.EditText

import android.text.InputFilter
import com.mironov.psychologicaltest.MainActivity
import com.mironov.psychologicaltest.R
import com.mironov.psychologicaltest.ResultsActivity
import com.mironov.psychologicaltest.constants.KeysContainer
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_FRAGMENT
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_SENDER
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_ID
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.security.LoginProvider


class LoginFragment : DialogFragment() {

    lateinit var sendBtn:Button

    lateinit var inputNameText:EditText

    var testId=0

    private val blockCharacterSet = "~#^|$%&*!.,\\/"

    private var userName: String? = null
    private var testName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.user_input_data_fragment, container,  false)

        inputNameText = rootView.findViewById<View>(R.id.textInputLayout) as EditText

        inputNameText.filters = arrayOf(filter)

        val bundle= arguments
        userName= bundle?.getString(KEY_USER_NAME)
        testName= bundle?.getString(KEY_TEST_NAME)

        sendBtn  = rootView.findViewById<View>(R.id.sendBtn) as Button
        sendBtn.setOnClickListener { sendData(inputNameText.text.toString()) }

        return rootView
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        sendData("")
    }



        private val filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    private fun sendData(text:String) {
        //INTENT OBJ

        val intent = Intent(requireActivity().baseContext, ResultsActivity::class.java)
        intent.putExtra(KEY_SENDER, KeysContainer.KEY_NAME_MAIN_ACTIVITY)
        intent.putExtra(KEY_USER_NAME, userName)
        intent.putExtra(KEY_TEST_NAME, testName)
        //PACK DATA


        //START ACTIVITY
        val loginProvider= LoginProvider()
        val str=inputNameText.text.toString()
        if(str.length>0) {
            if (loginProvider.checkPassword(str)) {
                this.startActivity(intent)
                inputNameText.setText("")
                dismiss()
            } else {
                inputNameText.setText("не верно")
            }
        }


    }
}