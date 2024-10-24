package com.example.comepet.ui.post.addlocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.comepet.R


class AddlocationFragment : Fragment() {

    private lateinit var backButtonToUpload : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addlocation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButtonToUpload = view.findViewById(R.id.backButtonToUpload)

        backButtonToUpload.setOnClickListener {
            findNavController().navigate(R.id.navigation_addlocation_to_navigation_upload)
        }
    }
}