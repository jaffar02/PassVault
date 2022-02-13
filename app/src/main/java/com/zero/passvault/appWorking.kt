package com.zero.passvault

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.zero.passvault.DaoLayer.UserDao
import com.zero.passvault.databinding.ActivityAppWorkingBinding
import com.zero.passvault.models.User

class appWorking : AppCompatActivity(){
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityAppWorkingBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var id: String?= null
    private lateinit var sessionManager: SessionManagerPrefrences
    private var uid: String?= null
    private  var datalist: ArrayList<User> = ArrayList()
    private lateinit var adapterClass: RecyclerAdapterClass

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAppWorkingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_app_working)
        setContentView(binding.root)

        sessionManager = SessionManagerPrefrences(this)  //initializing our SharedPreferences singleton
        recyclerSetup()

        val googleClientID = "240643187966-b0rff2hn0hus1891vnaa5vj7d4fpudsd.apps.googleusercontent.com"  //is our google console client id for our project
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  //initializing our GoogleSignInOptions for GoogleSignInClient
            .requestEmail().requestIdToken(googleClientID)  //setting id token
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)  //initializing our GoogleSignInClient
        auth = Firebase.auth  //initializing our Firebase auth object

        //////MENU//////
        val menuListItems = arrayOf("Logout")  //Populating our list view
        val listAdapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, menuListItems)  //Setting up list adapter
        binding.menuList.adapter = listAdapter

        binding.menuIcon.setOnClickListener{
            binding.menuList.isVisible = true
        }

       binding.backgroundB.setOnClickListener{
           binding.menuList.isVisible = false
       }

       binding.addbtn.setOnClickListener{
           binding.bacDimLayout.isVisible = true
           binding.closeCard.isVisible = true
           binding.myRecycler.isVisible = false
           binding.welcomeText.isVisible = false
           binding.whiteBorder.isVisible = false
       }
       binding.closeCard.setOnClickListener{
           binding.bacDimLayout.isVisible = false
           binding.closeCard.isVisible = false
           binding.myRecycler.isVisible = true
           binding.welcomeText.isVisible = true
           binding.whiteBorder.isVisible = true
       }

        /*binding.RecloseCard.setOnClickListener{
            binding.bacDimLayout.isVisible = false
            binding.RecloseCard.isVisible = false
            binding.myRecycler.isVisible = true
            binding.welcomeText.isVisible = true
            binding.whiteBorder.isVisible = true
        }*/

       binding.menuList.setOnItemClickListener { adapterView, view, i, l ->
           if(menuListItems[i].equals("Logout")){
               mGoogleSignInClient.signOut().addOnCompleteListener {
                   val intent = Intent(this, MainActivity::class.java)
                   startActivity(intent)
                   finish()
               }
           }
       }
    }

    fun savebtn(view: View) {
        val title: String = binding.Title.text.toString()
        val credentials:String = binding.credentials.text.toString()
        id = sessionManager.getKeyUserName()  //getting the id which we saved in our shared preferences
        Log.i("prefid", id!!)
        if(title.isNotBlank()&&credentials.isNotBlank()) {
        binding.closeCard.isVisible = false
        binding.Title.setText("")
        binding.credentials.setText("")
        binding.bacDimLayout.isVisible = false
        binding.welcomeText.isVisible = true
        binding.whiteBorder.isVisible = true
        binding.myRecycler.isVisible = true

        val user = User(id!!,title, credentials)
        val context: Context = this.applicationContext
        val userDao = UserDao(context)
            userDao.addUser(user)
        }
        else{
            Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recyclerSetup(){
        val context = applicationContext
        val db = FirebaseFirestore.getInstance()
        val col = db.collection(sessionManager.getKeyUserName()!!).document("Data").collection("init")
        val query = col.orderBy("title", Query.Direction.DESCENDING)
        val recOption = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()  //setting query for our FireStoreRecyclerOptions
        adapterClass = RecyclerAdapterClass(recOption, this)   //Initializing our adapter
        binding.myRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.myRecycler.adapter = adapterClass

        //This code is from implementing swipe and drag actions for our recycler views
        val simpleCallBack = object:  ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(        //this is for dragging and changing positions of views
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { //this code is for swipe to delete views
                adapterClass.delete(viewHolder.adapterPosition)
                Toast.makeText(applicationContext, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack) //passing SimpleCallBack object to our ItemTouch constructor
        itemTouchHelper.attachToRecyclerView(binding.myRecycler)  //attaching itemTouch with our recycler

        /*val g = intent.getStringExtra("checkFLag")
        Toast.makeText(applicationContext, g, Toast.LENGTH_SHORT).show()
            if(g.equals("true")){
            binding.RecloseCard.isVisible =true
            binding.bacDimLayout.isVisible = true
            binding.myRecycler.isVisible = false
            binding.welcomeText.isVisible = false
            binding.whiteBorder.isVisible = false

            binding.ReTitle.setText(intent.getStringExtra("title"))
            binding.Recredentials.setText(intent.getStringExtra("passwords"))
        }*/

        }

    override fun onStart() {
        super.onStart()
        adapterClass.startListening() //check for the recycler if view is updated
    }

    override fun onStop() {
        super.onStop()
        adapterClass.stopListening()
    }

}