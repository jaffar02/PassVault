package com.zero.passvault

import android.content.Context
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


public class RecyclerAdapterClass(option: FirestoreRecyclerOptions<User>? = null,val context:Context?=null)
    : FirestoreRecyclerAdapter<User, RecyclerAdapterClass.myViewHolder>(option!!) {

    class myViewHolder(item: View) : RecyclerView.ViewHolder(item) { //viewholder class which holds our views attributes
        val title: TextView = item.findViewById(R.id.cardTitle)
        var Creds: TextView = item.findViewById(R.id.credentials)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder { //setting the view for our recycler items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.data_card, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int, model: User) {  //binds our viewholder with our fetched data
        holder.title.text = model.title
        holder.Creds.text = model.credentials
    }

    public fun delete(position: Int){
        snapshots.getSnapshot(position).getReference().delete()  //deleting document by the getting the reference of our document
    }


}
