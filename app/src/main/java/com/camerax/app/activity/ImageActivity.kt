package com.camerax.app.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camerax.app.R
import com.camerax.app.adapter.GalleryImageClickListener
import com.camerax.app.adapter.Image
import com.camerax.app.adapter.ImageManagerAdapter
import com.camerax.app.helper.AppFirebaseFirestore
import com.camerax.app.helper.AppFirebaseStorage
import com.google.firebase.firestore.Query
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ImageActivity : AppCompatActivity(), GalleryImageClickListener {

    private var myFireStore = AppFirebaseFirestore(this, "posts")
    private var myFirebaseStorage = AppFirebaseStorage(this)
    private var filePath: Uri? = null

    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: ImageManagerAdapter
    private var pointerImage : Image? = null

    private val PICK_IMAGE_REQUEST = 71
    private val CAMERA_REQUEST = 1888

    private lateinit var etInputName : EditText
    private lateinit var ivToUpload : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        galleryAdapter = ImageManagerAdapter(imageList)
        galleryAdapter.listener = this

        // init recyclerview
        val recyclerView = findViewById(R.id.rvImage) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = galleryAdapter

        initItem()

        refresh()
    }

    fun initItem () {

        etInputName = findViewById<EditText>(R.id.etInputName)
        ivToUpload = findViewById<ImageView>(R.id.ivToUpload)

        val btnAddFromGallery = findViewById<ImageButton>(R.id.btnAddFromGallery)
        val btnAddFromCamera = findViewById<ImageButton>(R.id.btnAddFromCamera)
        val btnDelete = findViewById<ImageButton>(R.id.btnDelete)
        val btnRefresh = findViewById<ImageButton>(R.id.btnRefresh)

        btnAddFromGallery.setOnClickListener() { launchGallery() }
        btnAddFromCamera.setOnClickListener() { launchCamera() }
        btnDelete.setOnClickListener() { deleteSelected() }
        btnRefresh.setOnClickListener() { refresh() }
    }

    // handling after opening intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // handle intent camera
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap

            ivToUpload.visibility = View.VISIBLE
            ivToUpload.setImageBitmap(photo)

            // TODO: NAME FOR FILE
            var docId = etInputName.text.toString()
            Toast.makeText(applicationContext, docId, Toast.LENGTH_LONG).show()
            if (docId.equals("")) docId =  UUID.randomUUID().toString()
            val addRecord = myFirebaseStorage?.uploadCaptured(ivToUpload, docId)
            refresh()
        }

        // handle intent gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val ivToUpload = findViewById(R.id.ivToUpload) as ImageView

                ivToUpload.visibility = View.VISIBLE
                ivToUpload.setImageBitmap(bitmap)

                // TODO: NAME FOR FILE
                var docId = etInputName.text.toString()
                Toast.makeText(applicationContext, docId, Toast.LENGTH_LONG).show()
                if (docId.equals("")) docId =  UUID.randomUUID().toString()
                val addRecord = myFirebaseStorage!!.uploadImage(filePath, docId)
                refresh()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun refresh() {
        imageList.clear()
        val galCollection = myFireStore.getGalleryCollection()
        galCollection.orderBy("uploadAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    imageList.add(
                        Image(
                            document.data.get("imageUrl") as String,
                            "caption is empty te-he",
                            document.data.get("docId") as String))
                }
                ivToUpload.visibility = View.INVISIBLE
                ivToUpload.getLayoutParams().height = 0;
                galleryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exc ->
                Toast
                    .makeText(applicationContext, "fail to load firestore", Toast.LENGTH_LONG)
                    .show()
            }
    }

    // add listener for each pic, so it can be view in fullscreen
    override fun onClick(position: Int) {
        val image = imageList.get(position)
        pointerImage = image
        Toast
            .makeText(this,
                "choose position " + pointerImage?.docId.toString(),
                Toast.LENGTH_LONG)
            .show()
    }

    private fun deleteSelected () {
        if ( pointerImage != null ) {
            Toast
                .makeText(this,
                    "attempt to delete " + pointerImage?.docId.toString(),
                    Toast.LENGTH_LONG)
                .show()
            myFirebaseStorage.getStorageReference()
                .child( "uploads/" + pointerImage?.docId.toString())
                .delete()
                .addOnSuccessListener {
                    myFireStore.delete(pointerImage?.docId.toString())
                    pointerImage = null
                }.addOnFailureListener {
                    Toast
                        .makeText(this,
                            "Fail to delete in storage" + it.message,
                            Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            Toast.makeText(this,
                "Please select the item",
                Toast.LENGTH_LONG)
                .show()
        }
    }
}