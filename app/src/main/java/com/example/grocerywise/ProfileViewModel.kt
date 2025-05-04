package com.example.grocerywise

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.example.grocerywise.data.FirebaseDatabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val db = FirebaseDatabase.getInstance()

    init {
        // Listen for changes to users/{uid}/avatarUrl
        userId?.let { uid ->
            FirebaseDatabaseManager
                .getAvatarUrlRef(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _avatarUrl.value = snapshot.getValue(String::class.java)
                    }
                    override fun onCancelled(error: DatabaseError) { /* handle error */ }
                })
        }
    }

    /** Upload a new avatar image to Cloud Storage and save its URL to Realtime DB. */
    fun uploadAvatar(uri: Uri) {
        val uid = userId ?: return
        val storageRef = FirebaseStorage
            .getInstance()
            .reference
            .child("avatars/$uid.jpg")

        // 1) Upload file to Storage
        storageRef.putFile(uri)
            .addOnSuccessListener {
                // 2) Get download URL
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        // 3) Save URL in Realtime Database
                        FirebaseDatabaseManager.setAvatarUrl(uid, downloadUri.toString())
                    }
            }
            .addOnFailureListener { /* handle error */ }
    }

    /** Sign out the current user. */
    fun signOut() {
        auth.signOut()
    }
}
