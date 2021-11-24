package com.camerax.app.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camerax.app.R
import com.camerax.app.adapter.GalleryImageClickListener
import com.camerax.app.adapter.Image
import com.camerax.app.adapter.ImageManagerAdapter
import com.camerax.app.response.General
import com.camerax.app.response.Post
import com.camerax.app.service.DataServices
import retrofit2.Call
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import retrofit2.Callback
import retrofit2.Response

class InstappActivity : AppCompatActivity(), GalleryImageClickListener {

//    private var myFireStore = AppFirebaseFirestore(this, "posts")
//    private var myFirebaseStorage = AppFirebaseStorage(this)
    private var filePath: Uri? = null

    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: ImageManagerAdapter
    private var pointerImage : Image? = null
    private var pointerPosition : Int? = null

    private val PICK_IMAGE_REQUEST = 71
    private val CAMERA_REQUEST = 1888

    private lateinit var etInputName : EditText
    private lateinit var ivToUpload : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instapp)

        galleryAdapter = ImageManagerAdapter(imageList)
        galleryAdapter.listener = this

        // init recyclerview
        val recyclerView = findViewById(R.id.rvImage) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = galleryAdapter

        initItem()

        getAllApi()
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
        btnRefresh.setOnClickListener() { getAllApi() }
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
//            val addRecord = myFirebaseStorage?.uploadCaptured(ivToUpload, docId)

            // TODO: upload baos to instapp
            val bitmap = (ivToUpload.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val encodedImage: String = encodeToString(data, DEFAULT)

            handlePost(docId, encodedImage)

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
//                val addRecord = myFirebaseStorage!!.uploadImage(filePath, docId)

                // TODO: upload baos to instapp
                val bitmap1 = (ivToUpload.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val encodedImage: String = encodeToString(data, DEFAULT)

                handlePost(docId, encodedImage)

                refresh()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getAllApi() {
        val networkServices = DataServices.create()
        val call = networkServices.getAll()

        call.enqueue(object: Callback<Post>{
            override fun onFailure(call: Call<Post>, t: Throwable) {
                println("On Failure")
                println(t.message)
                Toast.makeText(getApplicationContext(), "Failed Getting Response" + t.message,
                    Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<Post>,response: Response<Post>) {
                Toast.makeText(getApplicationContext(), "Success Getting Response",
                    Toast.LENGTH_LONG).show()
                if (response.body() != null) {
                    val data: Post = response.body()!!
                    Toast.makeText(getApplicationContext(), "Response body not null",
                        Toast.LENGTH_LONG).show()
                    if (data.data!!.isNotEmpty()) {
                        imageList.clear()
                        for (post in data.data) {
                            imageList.add(Image(data.message!! + post!!.foto!!, post.caption!!, post.id.toString()))
                        }
                        if (imageList.count() != 0) {
                            println("Dataset isnt null")
                            galleryAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Response Body Null on get fun",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun handlePost (caption: String, imageEncoded: String) {
        var imgEncodedToPass = "";
        val networkServices = DataServices.create()
        if (imageEncoded.subSequence(0, 4).equals("/9j/")) imgEncodedToPass = "data:image/jpeg;base64," + imageEncoded
        if (imageEncoded.subSequence(0, 4).equals("iVBO")) imgEncodedToPass = "data:image/png;base64," + imageEncoded
        val call = networkServices.uploadFoto(caption, imgEncodedToPass)
        // Create JSON using JSONObject
//        val data = SendPhoto(caption, imageEncoded)
        call.enqueue(object: Callback<General>{
            override fun onFailure(call: Call<General>, t: Throwable) {
                println("On Failure")
                println(t.message)
                Toast.makeText(getApplicationContext(), "Failed Post" + t.message,
                    Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<General>, response: Response<General>) {
                Toast.makeText(getApplicationContext(), "Success Getting Response " + imageEncoded.subSequence(0, 50),
                    Toast.LENGTH_LONG).show()
                if (response.body() != null) {
                    val data: General = response.body()!!
                    Toast.makeText(getApplicationContext(), "Response body not null: " + data.message,
                        Toast.LENGTH_LONG).show()
                    if (data.data!!.isNotEmpty()) {
                        imageList.add(Image(data.message!! + data.data, null, "foto baru"))

                        if (imageList.count() != 0) {
                            println("Dataset isn't null")
                            galleryAdapter.notifyItemInserted(imageList.count())
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Response Body Null on Post fun",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun deleteSelected () {
        if ( pointerImage != null ) {
            // TODO: delete to instapp
            val networkServices = DataServices.create()
            val call = networkServices.deleteById(pointerImage?.docId!!.toInt())

            call.enqueue(object: Callback<General>{
                override fun onFailure(call: Call<General>, t: Throwable) {
                    println("On Failure")
                    println(t.message)
                    Toast.makeText(getApplicationContext(), "Failed Getting Response" + t.message,
                        Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<General>,response: Response<General>) {
                    Toast.makeText(getApplicationContext(), "Success Getting Response",
                        Toast.LENGTH_LONG).show()
                    if (response.body() != null) {
                        val data: General = response.body()!!
                        Toast.makeText(getApplicationContext(), "Response body not null",
                            Toast.LENGTH_LONG).show()
                        if (data.data!!.isNotEmpty()) {
                            if (pointerPosition != null) {
                                imageList.removeAt(pointerPosition!!)
                                galleryAdapter.notifyItemRemoved(pointerPosition!!)
                            }
                            pointerImage = null
                            pointerPosition = null
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Response Body Null on Delete fun",
                            Toast.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            Toast.makeText(this,
                "Please select the item",
                Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun refresh() {
        imageList.clear()
        // TODO: call getAll
    }

    // add listener for each pic, so it can be view in fullscreen
    override fun onClick(position: Int) {
        val image = imageList.get(position)
        pointerImage = image
        pointerPosition = position
        Toast
            .makeText(this,
                "choose position " + pointerImage?.docId.toString(),
                Toast.LENGTH_LONG)
            .show()
    }
}