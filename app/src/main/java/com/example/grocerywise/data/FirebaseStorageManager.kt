package com.example.grocerywise.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageManager {
    // Root reference to Cloud Storage
    private val storageRef = FirebaseStorage.getInstance().reference

    /**
     * Upload an image file for a grocery or inventory item, then invoke onComplete with its download URL.
     *
     * @param userId    current user’s UID
     * @param itemType  either "inventory" or "grocery"
     * @param itemId    the Realtime-DB key of the item
     * @param imageUri  Uri pointing to the local image file
     * @param onComplete callback(downloadUrl: String?) – null on failure
     */
    fun uploadItemImage(
        userId: String,
        itemType: String,
        itemId: String,
        imageUri: Uri,
        onComplete: (String?) -> Unit
    ) {
        // e.g. users/{uid}/{itemType}/{itemId}.jpg
        val imageRef = storageRef.child("users/$userId/$itemType/$itemId.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { uri -> onComplete(uri.toString()) }
                    .addOnFailureListener { onComplete(null) }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Delete an item’s image from Cloud Storage.
     *
     * @param userId    current user’s UID
     * @param itemType  either "inventory" or "groceryList"
     * @param itemId    the Realtime-DB key of the item
     * @param onComplete callback(success: Boolean)
     */
    fun deleteItemImage(
        userId: String,
        itemType: String,
        itemId: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val imageRef = storageRef.child("users/$userId/$itemType/$itemId.jpg")
        imageRef.delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}





