package com.example.grocerywise.data

import com.example.grocerywise.InventoryItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {
    private val database = FirebaseDatabase.getInstance()

    // Get reference to the current user's inventory node.
    fun getUserInventoryRef(userId: String) =
        database.getReference("users").child(userId).child("inventory")

    // Add an inventory item; uses push() to generate a unique key.
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

    // Update an existing inventory item (e.g., update quantity changes).
    fun updateInventoryItem(userId: String, item: InventoryItem) {
        if (item.id != null) {
            getUserInventoryRef(userId).child(item.id!!).setValue(item)
        }
    }

    // Add a grocery list item (similar implementation).
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

    // Listen to changes in the inventory data; attaches a ValueEventListener.
    fun listenToInventory(userId: String, listener: ValueEventListener) {
        getUserInventoryRef(userId).addValueEventListener(listener)
    }
}
