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
import android.widget.TextView
import android.widget.Toast
import com.mironov.psychologicaltest.R
import com.mironov.psychologicaltest.ResultsActivity
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_FRAGMENT_LOGIN
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_SENDER
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.security.LoginProvider


class LoginFragment : DialogFragment() {

    lateinit var sendBtn: Button

    lateinit var inputNameText: EditText
    lateinit var textView: TextView

    private val blockCharacterSet = "~#^|$%&*!.,\\/"

    private var userName: String? = null
    private var testName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.input_dialog_fragment, container, false)

        inputNameText = rootView.findViewById<View>(R.id.textInputLayout) as EditText
        textView = rootView.findViewById<View>(R.id.fragmentText) as TextView

        textView.text = getString(R.string.input_password)

        inputNameText.filters = arrayOf(filter)

        val bundle = arguments
        userName = bundle?.getString(KEY_USER_NAME)
        testName = bundle?.getString(KEY_TEST_NAME)

        sendBtn = rootView.findViewById<View>(R.id.sendBtn) as Button
        sendBtn.text = getString(R.string.enter)
        setButtonListener()

        return rootView
    }

    private fun setButtonListener() {
        sendBtn.setOnClickListener { login()  }
    }

    private fun login() {
        val loginProvider = LoginProvider()
        val str = inputNameText.text.toString()
        if (str.isNotEmpty()) {
            //Login if password is correct
            if (loginProvider.checkPassword(str)) {
                this.startActivity(prepareIntent())
                inputNameText.setText("")
                dismiss()
            } else {
                inputNameText.setText("")
                textView.text = getString(R.string.input_password)
                Toast.makeText(
                    context,
                    getString(R.string.wrong_password),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputNameText.isCursorVisible = false
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        sendBtn.setOnClickListener(null)
        dismiss()
    }

    private val filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    private fun prepareIntent():Intent{
        val intent = Intent(requireActivity().baseContext, ResultsActivity::class.java)
        intent.putExtra(KEY_SENDER, KEY_FRAGMENT_LOGIN)
        intent.putExtra(KEY_USER_NAME, userName)
        intent.putExtra(KEY_TEST_NAME, testName)
        return  intent
    }
}