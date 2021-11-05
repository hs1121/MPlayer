package com.example.mplayer.Utility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mplayer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddPlaylistDialog(
    val onAddClick:(String,String)->Unit
) :DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.add_playlist_dialog,container,false)
        val nameText=view.findViewById<TextInputEditText>(R.id.name_text_field)
        val byText=view.findViewById<TextInputEditText>(R.id.by_text_field)
        val cancelButton=view.findViewById<MaterialButton>(R.id.cancel_button)
        val addButton=view.findViewById<MaterialButton>(R.id.add_button)

        addButton.setOnClickListener {
            if (nameText.text.toString().isNotEmpty()&& byText.text.toString().isNotEmpty()){
                onAddClick(nameText.text.toString(),byText.text.toString())
                dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }

        return view;
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.setBackgroundDrawableResource(R.color.transparent)
    }
}