package com.ruthvikbr.runtracker.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ruthvikbr.runtracker.R

class CancelTrackingDialog : DialogFragment() {

    private var positiveButtonListener: (() -> Unit)? = null

    fun setPositiveButtonListener(listener: ()-> Unit){
        positiveButtonListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel Run")
            .setMessage("Are you sure?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_ ->
                positiveButtonListener?.let {yes->
                    yes()
                }
            }
            .setNegativeButton("No") { dialogInterface , _ ->
                dialogInterface.cancel()
            }
            .create()

    }
}