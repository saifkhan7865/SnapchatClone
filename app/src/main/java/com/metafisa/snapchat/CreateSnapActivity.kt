package com.metafisa.snapchat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*

class CreateSnapActivity : AppCompatActivity() {
    var photoStringLink:String?=null
    val MyVersion = Build.VERSION.SDK_INT
    private val RESULT_LOAD_IMAGE = 1
    var createSnapImageView:ImageView?= null
    var messageEditText:EditText?= null
    val ImageName = UUID.randomUUID().toString() + ".jpg"
    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)


    }
    fun ChooseImageClicked(view: View){
        if(MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1 ){
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i,RESULT_LOAD_IMAGE)
            }
        }
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage: Uri = data.data
            val filePathColumn =
                arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor =
                contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()
            val ImageView = findViewById<ImageView>(R.id.createSnapImageView)
            ImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }
    fun nextClicked(view:View){

         //Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = FirebaseStorage.getInstance().reference.child("images").child(ImageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "upload failed", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>{ Snapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            val result: Task<Uri> =
                Snapshot.getMetadata()!!.getReference()!!.getDownloadUrl()
            result.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                photoStringLink = uri.toString()
                Log.i("URL",photoStringLink)
                val intent = Intent(this,ChooseUserActivity::class.java)
                intent.putExtra("imageURL",photoStringLink)
                intent.putExtra("imageName", ImageName)
                intent.putExtra("message",messageEditText?.text.toString())
                startActivity(intent)
            })
            //val downloadUrl = Snapshot.metadata!!.reference!!.downloadUrl

            //Toast.makeText(this,"hello there!" ,Toast.LENGTH_LONG).show()

        })

    }
}
