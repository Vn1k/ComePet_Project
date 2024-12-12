package com.example.comepet.ui.home

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
    val username: String? = null,
    val profilePicture: String? = null
)

class CommentFragment : BottomSheetDialogFragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var isSubmitting = false

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
        db.collection("feeds")
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
                        val username = document.getString("username")
                        val profilePicture = document.getString("profilePicture") ?: ""
                        Comment(commentText, username, profilePicture)
                    }
                    commentAdapter.submitList(comments)
                }
            }
        db.collection("users")
            .document(mAuth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { userDocument ->
                val profilePicture = userDocument.getString("profilePicture")
                Log.d("CommentFragment", "PP: $profilePicture")
            }
            .addOnFailureListener { error ->
                Log.e("CommentFragment", "Error fetching user data: ${error.message}")
            }

        commentSendButton.setOnClickListener {
                if (isSubmitting) return@setOnClickListener

                val commentText = commentInput.text.toString().trim()
                val currentUser = mAuth.currentUser?.uid

                if (commentText.isNotBlank() && postId != null) {
                    isSubmitting = true

                    commentSendButton.isEnabled = false

                    db.collection("users")
                        .document(currentUser!!)
                        .get()
                        .addOnSuccessListener { userDocument ->
                            val username = userDocument.getString("username") ?: "Unknown"
                            val profilePicture = userDocument.getString("profilePicture") ?: ""

                            val commentData = mapOf(
                                "profilePicture" to profilePicture,
                                "commentText" to commentText,
                                "username" to username,
                                "date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            )

                            db.collection("feeds")
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
                                    Toast.makeText(context, "Failed to send comment", Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener {
                                    isSubmitting = false
                                    commentSendButton.isEnabled = true
                                }
                        }
                        .addOnFailureListener { error ->
                            Log.e("CommentFragment", "Error fetching user data: ${error.message}")
                            isSubmitting = false
                            commentSendButton.isEnabled = true
                            Toast.makeText(context, "Failed to send comment", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("CommentFragment", "Comment text is empty or postId is null")
                    Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show()
                }
        }

        commentInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                commentSendButton.performClick()
                true
            } else {
                false
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}