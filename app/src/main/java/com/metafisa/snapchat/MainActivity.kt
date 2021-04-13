package com.metafisa.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.net.PasswordAuthentication

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    var EmailEditText:EditText? = null
    var PasswordEditText:EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EmailEditText = findViewById(R.id.EmailEditText)
        PasswordEditText = findViewById(R.id.PasswordEditText)
        if(mAuth.currentUser!=null){
            Login()
        }
    }
    fun Go(view:View){
        mAuth.signInWithEmailAndPassword(EmailEditText?.text.toString(), PasswordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Login()
                } else {
                    // If sign in fails, display a message to the user.
                    mAuth.createUserWithEmailAndPassword(EmailEditText?.text.toString(), PasswordEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user?.uid.toString()).child("email").setValue(EmailEditText?.text.toString())
                                Login()
                                // Sign in success, update UI with the signed-in user's information
                                val user = mAuth.currentUser

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()

                            }

                            // ...
                        }
                    // Sign Up the user
                }

                // ...
            }
        //Check if we can login the user

        //Sign Up the user
    }
    fun Login(){
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
        //Move to next activity
    }
}
