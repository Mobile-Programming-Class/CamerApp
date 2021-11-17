package com.camerax.app.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.camerax.app.R
import com.camerax.app.adapter.GalleryImageAdapter
import com.camerax.app.adapter.GalleryImageClickListener
import com.camerax.app.adapter.Image
import com.camerax.app.fragment.GalleryFullscreenFragment
import com.camerax.app.helper.AppFirebaseFirestore
import com.camerax.app.helper.AppFirebaseStorage
import com.google.firebase.firestore.Query

import java.io.IOException
import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), GalleryImageClickListener {
    // gallery column count
    private val SPAN_COUNT = 3
    private val CAMERA_REQUEST = 1888
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private var myFireStore = AppFirebaseFirestore(this, "posts")
    private var myFirebaseStorage = AppFirebaseStorage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this

        // init recyclerview
        val recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        // load images
        loadImages()

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)

        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setSubtitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // action bar pojok atas
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // tambah foto lewat camera
            R.id.action_add -> {
                Toast.makeText(applicationContext, "click on add image", Toast.LENGTH_LONG).show()

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)

                return true
            }

            // tambah foto lewat gallery
            R.id.action_add_from_gallery -> {
                launchGallery()

                return true
            }

            // manager
            R.id.action_photo_manager -> {
                val intent = Intent(this, ImageActivity::class.java)
                startActivity(intent)

                return true
            }

            // exit
            R.id.action_exit ->{
                Toast.makeText(applicationContext, "click on exit", Toast.LENGTH_LONG).show()
                moveTaskToBack(true)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadImages() {
        imageList.clear()
        val galCollection = myFireStore.getGalleryCollection()
        galCollection.orderBy("uploadAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    imageList.add(Image(document.data.get("imageUrl") as String, "caption is empty te-he"))
                }
                galleryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exc ->
                Toast.makeText(applicationContext, "fail to load firestore", Toast.LENGTH_LONG).show()
            }
    }

    // handling after opening intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // handle intent camera
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            val ivTest = findViewById(R.id.ivTest) as ImageView

            ivTest.visibility = View.VISIBLE
            ivTest.setImageBitmap(photo)

            // TODO: NAME FOR FILE
            val docId = UUID.randomUUID().toString()
            val addRecord = myFirebaseStorage?.uploadCaptured(ivTest, docId)
            loadImages()
        }

        // handle intent gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

//                setImageBitmap(bitmap)
                val docId = UUID.randomUUID().toString()
                val addRecord = myFirebaseStorage!!.uploadImage(filePath, docId)
//                myFireStore.add(docId, addRecord)
                loadImages()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // add listener for each pic, so it can be view in fullscreen
    override fun onClick(position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.setArguments(bundle)
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    // open intent gallery
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

}