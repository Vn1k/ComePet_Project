package com.example.comepet.ui.boarding

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment

class BoardingFragment : BaseAuthFragment() {

    companion object {
        fun newInstance() = BoardingFragment()
    }

    private val viewModel: BoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_boarding, container, false)
    }
}