package com.mek.sharingphotos.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mek.sharingphotos.R
import com.mek.sharingphotos.adapter.NewsRecyclerAdapter
import com.mek.sharingphotos.model.Post
import kotlinx.android.synthetic.main.activity_news2.*
import java.util.ArrayList

class NewsActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    var postList = ArrayList<Post>()
    private lateinit var recyclerViewAdapter : NewsRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news2)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    verileriAl()
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = NewsRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menusu,menu)

        return super.onCreateOptionsMenu(menu)
    }
    fun verileriAl(){
        database.collection("Post").orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    if (!snapshot.isEmpty){
                        val documents = snapshot.documents
                        postList.clear()
                        for (document in documents){
                            val kullaniciEmail = document.get("email") as String
                            val kullaniciYorum = document.get("yorum") as String
                            val gorselUrl = document.get("downloadurl") as String
                            var indirilenPost = Post(kullaniciEmail,kullaniciYorum,gorselUrl)
                            postList.add(indirilenPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fotografPaylas){
            val intent = Intent(this, SharingPhotoActivity::class.java)
            startActivity(intent)

        }else if (item.itemId == R.id.cikisYap){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}