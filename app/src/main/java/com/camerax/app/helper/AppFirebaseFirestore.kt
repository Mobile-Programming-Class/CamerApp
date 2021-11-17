package com.camerax.app.helper

import android.content.Context
import android.widget.Toast
import com.camerax.app.adapter.Image
import com.camerax.app.datatype.UpdateDataType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AppFirebaseFirestore(private val context: Context, collection: String) {

    private val galleryCollection: CollectionReference = FirebaseFirestore.getInstance().collection( collection )

    fun getGalleryCollection (): CollectionReference {
        return galleryCollection
    }

    fun add(docId: String, data: HashMap<String, Any>){
        galleryCollection.document(docId).set(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

    fun read(): ArrayList<Image> {
        var dataImage = ArrayList<Image>()
        var errorReading = ""
        galleryCollection.orderBy("uploadAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dataImage.add(Image(document.data.get("imageUrl") as String, "caption is empty te-he"))
                }
            }
            .addOnFailureListener { exc ->
                errorReading += exc.message.toString() + "\n"
            }
        if (errorReading.equals("")) Toast.makeText(context, "Success read all data", Toast.LENGTH_LONG).show()
        else Toast.makeText(context, errorReading, Toast.LENGTH_LONG).show()
        return dataImage
    }

    fun update(documentId: String, updateData: ArrayList<UpdateDataType>) {
        val document = galleryCollection.document(documentId)
        updateData.forEach{
            document.update(it.key, it.value)
        }
    }

    fun delete(docId: String) {
        galleryCollection.document(docId).delete()
            .addOnSuccessListener { Toast.makeText(context, "Success delete data", Toast.LENGTH_LONG).show() }
            .addOnFailureListener { Toast.makeText(context, "Failed delete data", Toast.LENGTH_LONG).show() }
    }
}