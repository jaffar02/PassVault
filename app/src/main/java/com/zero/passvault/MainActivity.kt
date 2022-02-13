package com.zero.passvault

import android.R
import android.appwidget.AppWidgetHostView
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.zero.passvault.databinding.ActivityMainBinding
import kotlin.properties.Delegates
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var RC_SIGN_IN = 100
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var tempid: String?= null
    private lateinit var sessionManager: SessionManagerPrefrences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManagerPrefrences(this)

        binding.helpbtn.setOnClickListener{
                binding.bacDimLayout.isVisible = true
        }
        binding.bacDimLayout.setOnClickListener{
            binding.bacDimLayout.isVisible = false
        }
        val googleClientID = "240643187966-b0rff2hn0hus1891vnaa5vj7d4fpudsd.apps.googleusercontent.com"
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestIdToken(googleClientID)
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth
        val account = GoogleSignIn.getLastSignedInAccount(this)
        binding.signInButton.setOnClickListener{
            signInGoogle()
        }


    }

    private fun signInGoogle(){
        binding.progressCircle.isVisible = true
        val signInIntent = mGoogleSignInClient.signInIntent  //starts our SignIn process with passing properties with intent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null) { //this methods verify whether user is already signed or logged out
            intent = Intent(this, appWorking::class.java)
            binding.progressCircle.isVisible = false
            startActivity(intent)
        }
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personId = acct.id
                tempid = acct.id
                sessionManager.setKeyUserName(tempid)
            }
            if (account!=null){
                updateUI(account)
            }

           } catch (e: ApiException) {
            Toast.makeText(this, "Something happened!", Toast.LENGTH_SHORT).show()
            Log.w("Signin Status", "signInResult:failed code=" + e.statusCode)
            binding.progressCircle.isVisible = false
        }
    }

    private fun updateUI(account: GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credentials).addOnCompleteListener{
            task->
            if (task.isSuccessful){
                intent = Intent(this, appWorking::class.java)
                intent.putExtra("uid", tempid)
                binding.progressCircle.isVisible = false
                startActivity(intent)
                finish()
            }
        }
    }
}