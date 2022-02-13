package com.zero.passvault.DaoLayer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import com.zero.passvault.SessionManagerPrefrences
import com.zero.passvault.appWorking
import com.zero.passvault.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao(context:Context) {
    val con = context
    public var check = false
    private val db = FirebaseFirestore.getInstance()
    fun addUser(user: User?){
        Toast.makeText(con, "Saved Successfully!", Toast.LENGTH_SHORT).show()
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                //We use GlobalScope.launch to be safe from nested calls conditions
                db.collection(user.uid).document("Data").collection("init").add(it)
            }
        }
    }

}