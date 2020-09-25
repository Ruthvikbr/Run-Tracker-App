package com.ruthvikbr.runtracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.utilities.Constants.KEY_NAME
import com.ruthvikbr.runtracker.utilities.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_setup.etName
import kotlinx.android.synthetic.main.fragment_setup.etWeight
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings){

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()

        btnApplyChanges.setOnClickListener{
            val success = applyChangesTOSharedPref()
            if(success){
                Snackbar.make(view,"Changes Saved",Snackbar.LENGTH_SHORT).show()
            } else{
                Snackbar.make(view,"Please fill the details",Snackbar.LENGTH_SHORT).show()

            }
        }
    }

    private fun loadFieldsFromSharedPref(){
        val name = sharedPreferences.getString(KEY_NAME,"")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT,80f)
        etName.setText(name)
        etWeight.setText(weight.toString())

    }

    private fun applyChangesTOSharedPref() : Boolean {
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT,weightText.toFloat())
            .apply()

        val toolbarText = "Let's go $nameText!"
        requireActivity().tvToolbarTitle.text = toolbarText

        return true


    }
}