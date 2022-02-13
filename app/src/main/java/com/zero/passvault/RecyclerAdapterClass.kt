package com.zero.passvault

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.zero.passvault.models.User


public class RecyclerAdapterClass(option: FirestoreRecyclerOptions<User>, val context: Context)
    : FirestoreRecyclerAdapter<User, RecyclerAdapterClass.myViewHolder>(option!!) {
    private lateinit var intent: Intent
    private lateinit var viewT: View

    class myViewHolder(item: View) :
        RecyclerView.ViewHolder(item) { //viewholder class which holds our views attributes
        val title: TextView = item.findViewById(R.id.cardTitle)
        var Creds: TextView = item.findViewById(R.id.credentials)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder { //setting the view for our recycler items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.data_card, parent, false)
        viewT = view
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int, model: User) {  //binds our viewholder with our fetched data
        holder.title.text = model.title
        holder.Creds.text = model.credentials
        /*intent = Intent(context, appWorking::class.java)
        viewT.setOnClickListener {
            intent.putExtra("title", model.title)
            intent.putExtra("passwords", model.credentials)
            intent.putExtra("checkFlag", "true")
            context.startActivity(intent)
        }*/
    }
        public fun delete(position: Int) {
            snapshots.getSnapshot(position).getReference()
                .delete()  //deleting document by the getting the reference of our document
        }
}
