package com.example.mplayer.Utility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mplayer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddPlaylistDialog(
    private val name:String?,
    private val by:String?,
    val onAddClick:(String,String)->Unit
) :DialogFragment() {



    @Inject
    lateinit var nameValidator: NameValidator

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
            when (val results = nameValidator.isPlaylistNameValid(nameText.text?.toString())) {
                is DataResults.Error -> {
                    nameText.error = results.message
                }
                else -> {
                    onAddClick(results.data.toString(), byText.text.toString())
                    dismiss()
                }
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