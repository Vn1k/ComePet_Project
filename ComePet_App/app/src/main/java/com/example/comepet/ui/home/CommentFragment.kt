//package com.example.comepet.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.comepet.databinding.FragmentCommentsBinding
//
//data class Comment(
//    val username: String,
//    val text: String
//)
//
//class CommentFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var commentAdapter: CommentAdapter
//    private lateinit var commentList: MutableList<Comment>
//    private lateinit var homeViewModel: HomeViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentCommentsBinding.inflate(inflater, container, false)
//
//        recyclerView = binding.recyclerViewComments
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
//
//        val postId = arguments?.getString("postId") ?: ""
//
//        homeViewModel.getCommentsForPost(postId).observe(viewLifecycleOwner) { comments ->
//            commentAdapter.updateComments(comments)
//        }
//
//        commentAdapter = CommentAdapter(emptyList())
//        recyclerView.adapter = commentAdapter
//
//        return binding.root
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(postId: String) = CommentFragment().apply {
//            arguments = Bundle().apply {
//                putString("postId", postId)
//            }
//        }
//    }
//}