package com.example.grocerywise

import com.example.grocerywise.InventoryItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {
    // Retrieve the FirebaseDatabase instance. The firebase_database_url is automatically read from values.xml.
    private val database = FirebaseDatabase.getInstance()

    // Get a reference to the current user's inventory node using the user UID provided by FirebaseAuth.
    fun getUserInventoryRef(userId: String) =
        database.getReference("users").child(userId).child("inventory")

    // Add an inventory item using push() to generate a unique key.
    fun addInventoryItem(
        userId: String,
        item: InventoryItem,
        onComplete: ((Boolean, Exception?) -> Unit)? = null
    ) {
        val ref = getUserInventoryRef(userId).push()
        item.id = ref.key
        ref.setValue(item).addOnCompleteListener { task ->
            onComplete?.invoke(task.isSuccessful, task.exception)
        }
    }

    // Add a grocery list item (similar approach).
    fun addGroceryListItem(
        userId: String,
        item: com.example.grocerywise.GroceryListItem,
        onComplete: ((Boolean, Exception?) -> Unit)? = null
    ) {
        val ref = database.getReference("users").child(userId).child("groceryList").push()
        item.id = ref.key
        ref.setValue(item).addOnCompleteListener { task ->
            onComplete?.invoke(task.isSuccessful, task.exception)
        }
    }

    // Listen for changes in the inventory data by adding a ValueEventListener.
    fun listenToInventory(userId: String, listener: ValueEventListener) {
        getUserInventoryRef(userId).addValueEventListener(listener)
    }
}
