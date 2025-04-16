package com.example.grocerywise.data

import com.example.grocerywise.models.GroceryItem
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {
    // Get an instance of FirebaseDatabase.
    private val database = FirebaseDatabase.getInstance()

    // Get a reference to the current user's inventory node.
    private fun getUserInventoryRef(userId: String) = database.getReference("users").child(userId).child("inventory")

    // Add an inventory item; uses push() to generate a unique key.
    fun addInventoryItem(
        userId: String,
        item: InventoryItem,
        onComplete: ((Boolean, Exception?) -> Unit)? = null,
    ) {
        val ref = getUserInventoryRef(userId).push()
        item.id = ref.key
        ref.setValue(item).addOnCompleteListener { task ->
            onComplete?.invoke(task.isSuccessful, task.exception)
        }
    }

    // Update an existing inventory item (for example, updating quantity changes).
    fun updateInventoryItem(
        userId: String,
        item: InventoryItem,
    ) {
        item.id?.let {
            getUserInventoryRef(userId).child(it).setValue(item)
        }
    }

    // Listen to changes in the inventory data; attaches a ValueEventListener.
    fun listenToInventory(
        userId: String,
        listener: ValueEventListener,
    ) {
        getUserInventoryRef(userId).addValueEventListener(listener)
    }

    private fun getUserGroceryListRef(userId: String) = database.getReference("users").child(userId).child("groceryList")
    // Add a grocery list item; uses push() to generate a unique key.
    fun addGroceryListItem(
        userId: String,
        item: GroceryItem,
        onComplete: ((Boolean, Exception?) -> Unit)? = null,
    ) {
        val ref = getUserGroceryListRef(userId).push()
        item.id = ref.key
        ref.setValue(item).addOnCompleteListener { task ->
            onComplete?.invoke(task.isSuccessful, task.exception)
        }
    }

    fun updateGroceryListItem(
        userId: String,
        item: GroceryItem,
    ) {
        item.id?.let {
            getUserGroceryListRef(userId).child(it).setValue(item)
        }
    }

    fun listenToGroceryList(
        userId: String,
        listener: ValueEventListener,
    ) {
        getUserGroceryListRef(userId).addValueEventListener(listener)
    }
}
