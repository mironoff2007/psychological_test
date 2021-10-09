package com.mironov.psychologicaltest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import android.text.Spanned

import android.text.InputFilter






class InputUserDataFragment : DialogFragment() {

    lateinit var sendBtn:Button

    lateinit var inputNameText:EditText

    private val blockCharacterSet = "~#^|$%&*!.,\\/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.user_input_data_fragment, container,  false)

        inputNameText = rootView.findViewById<View>(R.id.textInputLayout) as EditText

        inputNameText.filters = arrayOf(filter);


        sendBtn = rootView.findViewById<View>(R.id.sendBtn) as Button
        sendBtn.setOnClickListener { sendData() }

        return rootView
    }

    private val filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    private fun sendData() {
        //INTENT OBJ
        val i = Intent(requireActivity().baseContext, MainActivity::class.java)

        //PACK DATA
        i.putExtra(KeysContainer.KEY_SENDER, KeysContainer.KEY_NAME_FRAGMENT)
        i.putExtra(KeysContainer.KEY_NAME_FRAGMENT, inputNameText.getText().toString())

        //RESET WIDGETS
        inputNameText.setText("")

        //START ACTIVITY
        requireActivity().startActivity(i)
        //dismiss()
    }
}