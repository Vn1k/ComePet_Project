package com.example.comepet.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Comment(
    val commentText: String? = null,
    val username: String? = null
)

class CommentFragment : BottomSheetDialogFragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val POST_ID = "post_id"

        fun newInstance(postId: String): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putString(POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    private var postId: String? = null
    private var listener: OnCommentAddedListener? = null

    interface OnCommentAddedListener {
        fun onCommentAdded(commentText: String)
    }

    fun setOnCommentAddedListener(listener: OnCommentAddedListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString(POST_ID)
        Log.d("CommentFragment", "Received postId: $postId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewComments)
        val commentInput = view.findViewById<EditText>(R.id.commentInput)
        val commentSendButton = view.findViewById<ImageView>(R.id.commentSendButton)

        val commentAdapter = CommentAdapter()
        commentRecyclerView.adapter = commentAdapter
        commentRecyclerView.layoutManager = LinearLayoutManager(context)

        val db = FirebaseFirestore.getInstance()

        // Ambil data komentar
        db.collection("users")
            .document(postId!!)
            .collection("feeds")
            .document(postId!!)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CommentFragment", "Error fetching comments: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val comments = snapshot.documents.mapNotNull { document ->
                        val commentText = document.getString("commentText")
                        val username = document.getString("username") // Ambil username dari komentar
                        Comment(commentText, username)  // Buat objek Comment
                    }
                    commentAdapter.submitList(comments)
                }
            }

        commentSendButton.setOnClickListener {
            val commentText = commentInput.text.toString()
            val currentUser = mAuth.currentUser?.uid

            if (commentText.isNotBlank()) {
                db.collection("users")
                    .document(currentUser!!)
                    .get()
                    .addOnSuccessListener { userDocument ->
                        val username = userDocument.getString("username") ?: "Unknown"

                        val commentData = mapOf(
                            "commentText" to commentText,
                            "username" to username,
                            "date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        )

                        db.collection("users")
                            .document(postId!!)
                            .collection("feeds")
                            .document(postId!!)
                            .collection("comments")
                            .add(commentData)
                            .addOnSuccessListener {
                                commentInput.text.clear()
                                Log.d("CommentFragment", "Comment added successfully")
                                listener?.onCommentAdded(commentText)
                            }
                            .addOnFailureListener { error ->
                                Log.e("CommentFragment", "Error adding comment: ${error.message}")
                            }
                    }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}