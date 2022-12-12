package com.mek.sharingphotos.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mek.sharingphotos.R
import kotlinx.android.synthetic.main.activity_sharing_photo.*
import java.util.*

class SharingPhotoActivity : AppCompatActivity() {
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing_photo)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),) { result ->
        if (result.resultCode == Activity.RESULT_OK ) {
            val intent = result.data
            if (intent!=null){
                secilenGorsel = intent.data
                if (secilenGorsel != null){
                    if (Build.VERSION.SDK_INT >=28){
                        val source = ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(secilenBitmap)
                    }else{
                        secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                        imageView.setImageBitmap(secilenBitmap)
                    }
            }

                }
        }
    }
    fun paylas(view: View){
        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"
        val reference = storage.reference
        val gorselReference = reference.child("images").child(gorselIsmi)
        if (secilenGorsel!=null){
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener {
                val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    val guncelKullaniciEmail = auth.currentUser!!.email.toString()
                    val kullaniciYorumu = txtYorum.text.toString()
                    val tarih = com.google.firebase.Timestamp.now()
                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("email",guncelKullaniciEmail)
                    postHashMap.put("yorum",kullaniciYorumu)
                    postHashMap.put("downloadurl",downloadUrl)
                    postHashMap.put("tarih",tarih)
                    database.collection("Post").add(postHashMap).addOnCompleteListener {
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }

        }


    }
    fun gorselSec(view: View){



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(galeriIntent)
        }
    }
    fun openGaleriForResult(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(galeriIntent)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(galeriIntent)
            }
        }
    }


}
