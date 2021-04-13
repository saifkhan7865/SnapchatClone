package com.metafisa.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.System.`in`
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapsActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var messageTextview: TextView?= null
    var snapImageView : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snaps)
        messageTextview = findViewById(R.id.MessageTextView)
        snapImageView = findViewById(R.id.snapimageview)
        messageTextview!!.text = intent.getStringExtra("message")
        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage =
                task.execute(intent.getStringExtra("imageURL")).get()!!
            snapImageView!!.setImageBitmap(myImage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
     inner class ImageDownloader : AsyncTask<String?, Void?, Bitmap?>() {
          override fun doInBackground(vararg p0: String?): Bitmap? {
             try {
                val url = URL(p0[0])
                val connection =
                    url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                 return BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return BitmapFactory.decodeStream(`in`)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()
        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("imageName")).delete()
    }
}
