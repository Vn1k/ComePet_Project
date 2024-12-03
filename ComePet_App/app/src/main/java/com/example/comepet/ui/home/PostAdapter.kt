package com.example.comepet.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PostAdapter(private var postList: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.getPostUsernameTop().text = post.username
        holder.getPostUsernameBottom().text = post.username
        holder.getPostLocation().text = post.location
        holder.getPostCaption().text = post.caption
        holder.getPostDate().text = post.date
        holder.getPostLikeCount().text = post.likeCount.toString()
        holder.getCommentCount().text = post.commentCount.toString()

        holder.bind(post)
    }

    fun updatePosts(newPosts: List<Post>) {
        postList = newPosts.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = postList.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProfile = itemView.findViewById<ImageView>(R.id.imageViewProfile)
        private val postUsernameTop = itemView.findViewById<TextView>(R.id.postUsernameTop)
        private val postUsernameBottom = itemView.findViewById<TextView>(R.id.postUsernameBottom)
        private val postLocation = itemView.findViewById<TextView>(R.id.postLocation)
        private val postImageView = itemView.findViewById<ImageView>(R.id.postImageView)
        private val postCaption = itemView.findViewById<TextView>(R.id.postCaption)
        private val postDate = itemView.findViewById<TextView>(R.id.postDate)
        private val postLikeCount = itemView.findViewById<TextView>(R.id.postLikeCount)
        private val commentCount = itemView.findViewById<TextView>(R.id.commentCount)
        private val postLike: ImageView = itemView.findViewById(R.id.postLike)
        private val postComment: ImageView = itemView.findViewById(R.id.commentButton)

        fun getImageViewProfile() = imageViewProfile
        fun getPostImageView() = postImageView
        fun getPostUsernameTop() = postUsernameTop
        fun getPostUsernameBottom() = postUsernameBottom
        fun getPostLocation() = postLocation
        fun getPostCaption() = postCaption
        fun getPostDate() = postDate
        fun getPostLikeCount() = postLikeCount
        fun getCommentCount() = commentCount

        fun bind(post: Post) {
            postUsernameTop.text = post.username
            postUsernameBottom.text = post.username
            postLocation.text = post.location
            postCaption.text = post.caption
            postDate.text = post.date
            postLikeCount.text = post.likeCount.toString()
            commentCount.text = post.commentCount.toString()

//            postComment.setOnClickListener {
//                onCommentClick(post)
//            }

            db.collection("users").document(post.userId)
                .get()
                .addOnSuccessListener { document ->
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null) {
                        Glide.with(itemView.context)
                            .load(profileImageUrl)
                            .circleCrop()
                            .into(imageViewProfile)
                    } else {
                        Log.e("PostAdapter", "Profile image URL is null")
                    }
                }

            db.collection("users").document(post.userId).collection("feeds")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            val imageUrl = document.getString("imageUrl")

                            if (imageUrl != null) {
                                Glide.with(itemView.context)
                                    .load(imageUrl)
                                    .into(postImageView)
                            } else {
                                Log.e("PostAdapter", "Image URL is null for document ID: ${document.id}")
                            }
                        }
                    } else {
                        Log.e("PostAdapter", "No documents found in the 'feeds' collection for post ID: ${post.userId}")
                    }
                }


            setLikeColor(post.isLiked)
            updateLikeDrawable(post.isLiked)

            postLike.setOnClickListener {
                post.isLiked = !post.isLiked
                setLikeColor(post.isLiked)
                updateLikeDrawable(post.isLiked)

                post.likeCount += if (post.isLiked) 1 else -1
                postLikeCount.text = post.likeCount.toString()

                updateLikeCountInFirestore(post.userId, post.id, post.isLiked)
            }

            itemView.findViewById<View>(R.id.commentButton).setOnClickListener {
                val commentText = "User's comment here"
                val commenterId = "currentUserId"
                addCommentToFirestore(post.userId, post.id, commentText, commenterId)
            }
        }

        private fun setLikeColor(isLiked: Boolean) {
            val color = if (isLiked) {
                itemView.context.getColor(android.R.color.holo_red_dark)
            } else {
                itemView.context.getColor(android.R.color.black)
            }
            postLike.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
        }

        private fun updateLikeDrawable(isLiked: Boolean) {
            val drawableRes = if (isLiked) {
                R.drawable.heart
            } else {
                R.drawable.like
            }
            postLike.setImageResource(drawableRes)
        }

        private fun updateLikeCountInFirestore(userId: String, postId: String, isLiked: Boolean) {
            val postRef =
                db.collection("users").document(userId).collection("feeds").document(postId)

            postRef.update(
                "likeCount",
                if (isLiked) FieldValue.increment(1) else FieldValue.increment(-1)
            )
                .addOnSuccessListener {
                    Log.d("PostAdapter", "Like count updated successfully in Firestore")
                }
                .addOnFailureListener { error ->
                    Log.e(
                        "PostAdapter",
                        "Failed to update like count in Firestore: ${error.message}"
                    )
                }
        }

        private fun updateCommentCountInFirestore(userId: String, postId: String, isCommented: Boolean) {
            val postRef = db.collection("users").document(userId).collection("feeds").document(postId)

            postRef.update(
                "commentCount",
                if (isCommented) FieldValue.increment(1) else FieldValue.increment(-1)
            )
                .addOnSuccessListener {
                    Log.d("PostAdapter", "Comment count updated successfully in Firestore")
                }
                .addOnFailureListener { error ->
                    Log.e("PostAdapter", "Failed to update comment count in Firestore: ${error.message}")
                }
        }

        private fun addCommentToFirestore(userId: String, postId: String, commentText: String, commenterId: String) {
            val commentData = hashMapOf(
                "commentText" to commentText,
                "commenterId" to commenterId,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val commentRef = db.collection("users").document(userId)
                .collection("feeds").document(postId)
                .collection("comments").document()

            commentRef.set(commentData)
                .addOnSuccessListener {
                    Log.d("PostAdapter", "Comment added successfully to Firestore")
                    updateCommentCountInFirestore(userId, postId, true)
                }
                .addOnFailureListener { error ->
                    Log.e("PostAdapter", "Failed to add comment to Firestore: ${error.message}")
                }
        }

//        private fun onCommentClick(post: Post) {
//            val commentsFragment = CommentsFragment.newInstance(post.id)
//
//            val fragmentTransaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.fragment_container, commentsFragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
//        }


//        private fun removeCommentFromFirestore(userId: String, postId: String, commentId: String) {
//            val commentRef = db.collection("users").document(userId)
//                .collection("feeds").document(postId)
//                .collection("comments").document(commentId)
//
//            commentRef.delete()
//                .addOnSuccessListener {
//                    Log.d("PostAdapter", "Comment removed successfully from Firestore")
//                    updateCommentCountInFirestore(userId, postId, false)
//                }
//                .addOnFailureListener { error ->
//                    Log.e("PostAdapter", "Failed to remove comment from Firestore: ${error.message}")
//                }
//        }
    }
}