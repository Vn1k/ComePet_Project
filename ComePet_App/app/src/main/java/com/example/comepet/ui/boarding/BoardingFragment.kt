package com.example.comepet.ui.boarding

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment

class BoardingFragment : BaseAuthFragment() {

    private lateinit var personlaButton: Button
    private lateinit var businessButton: Button

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personlaButton = view.findViewById(R.id.personalPurposeButton)
        businessButton = view.findViewById(R.id.sellerPurposeButton)

        personlaButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_boarding_to_navigation_welcome)
        }

        businessButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_boarding_to_navigation_welcome)
        }
    }
}