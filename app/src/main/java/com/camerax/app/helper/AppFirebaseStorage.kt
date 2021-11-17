package com.camerax.app.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.camerax.app.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap

class AppFirebaseStorage(private val context: Context) {

    private val storageReference = FirebaseStorage.getInstance().reference
    private val appFireStore = AppFirebaseFirestore(context, "posts")
    fun getStorageReference (): StorageReference {
        return storageReference
    }

    // upload captured pic from camera
    fun uploadCaptured(iv: ImageView, docId: String): HashMap<String, Any> {
        val ref = storageReference.child("uploads/" + docId)

        val bitmap = (iv.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = ref.putBytes(data)
        return runningUploadTask(ref, uploadTask, docId)
    }

    /* upload selected image from gallery
    *  @param filePath: Uri,
    * */
    fun uploadImage(filePath: Uri? = null, docId: String): HashMap<String, Any> {
        if(filePath == null){
            Toast.makeText(context, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }

        val ref = storageReference.child("uploads/" + docId)
        val uploadTask = ref.putFile(filePath!!)

        return runningUploadTask(ref, uploadTask, docId)
    }

    private fun runningUploadTask(ref: StorageReference?, uploadTask: UploadTask?, docId: String) : HashMap<String, Any> {

        val dataRecorded = HashMap<String, Any>()
        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref?.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val dataRecorded = HashMap<String, Any>()
                val downloadUri = task.result
//                addUploadRecordToDb(downloadUri.toString())
                dataRecorded["docId"] = docId
                dataRecorded["imageUrl"] = downloadUri.toString()
                dataRecorded["uploadAt"] = Timestamp.now()

                appFireStore.add(docId, dataRecorded)

            } else {
                Toast.makeText(context, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
        }?.addOnFailureListener{
            Toast.makeText(context, "Error saving to DB", Toast.LENGTH_LONG).show()
        }
        return dataRecorded
    }
}