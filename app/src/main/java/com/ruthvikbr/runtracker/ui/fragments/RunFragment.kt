package com.ruthvikbr.runtracker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run){
    private val viewModel:MainViewModel by viewModels()
}