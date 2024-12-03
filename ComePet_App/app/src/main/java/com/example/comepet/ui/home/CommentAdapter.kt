//package com.example.comepet.ui.home
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.comepet.R
//
//class CommentAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
//
//    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val commentUsername: TextView = itemView.findViewById(R.id.commentUsername)
//        private val commentText: TextView = itemView.findViewById(R.id.commentText)
//
//        fun bind(comment: Comment) {
//            commentUsername.text = comment.username
//            commentText.text = comment.text
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
//        return CommentViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
//        val comment = comments[position]
//        holder.bind(comment)
//    }
//
//    override fun getItemCount(): Int = comments.size
//
//    fun updateComments(newComments: List<Comment>) {
//        comments = newComments
//        notifyDataSetChanged()
//    }
//}