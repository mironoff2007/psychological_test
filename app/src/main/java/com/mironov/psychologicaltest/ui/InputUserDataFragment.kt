package com.mironov.psychologicaltest.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.mironov.psychologicaltest.MainActivity
import com.mironov.psychologicaltest.R
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_NAME_FRAGMENT
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_SENDER
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_TEST_ID


class InputUserDataFragment : DialogFragment() {

    lateinit var sendBtn: Button

    lateinit var inputNameText: EditText

    var testId = 0

    private val blockCharacterSet = "\"';:~#^|$%&*!.,\\/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.input_dialog_fragment, container, false)

        inputNameText = rootView.findViewById<View>(R.id.textInputLayout) as EditText

        inputNameText.filters = arrayOf(filter)

        val bundle = arguments
        testId = bundle?.getInt(KEY_TEST_ID, 0)!!

        sendBtn = rootView.findViewById<View>(R.id.sendBtn) as Button
        sendBtn.setOnClickListener { sendData(inputNameText.text.toString()) }

        return rootView
    }


    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        sendBtn.setOnClickListener(null)
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        inputNameText.isCursorVisible = false
    }

    private val filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    private fun sendData(text: String) {
        //INTENT OBJ
        val i = Intent(requireActivity().baseContext, MainActivity::class.java)

        //PACK DATA
        i.putExtra(KEY_SENDER, KEY_NAME_FRAGMENT)
        i.putExtra(KEY_NAME_FRAGMENT, text)
        i.putExtra(KEY_TEST_ID, testId)

        //RESET WIDGETS
        inputNameText.setText("")

        //START ACTIVITY

        requireActivity().startActivity(i)

        dismiss()
    }
}