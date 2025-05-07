package com.example.grocerywise.data

import com.example.grocerywise.models.GroceryItem
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {
    // Get an instance of FirebaseDatabase.
    private val database = FirebaseDatabase.getInstance()

    // Get a reference to the current user's inventory node.
    fun getUserInventoryRef(userId: String) = database.getReference("users").child(userId).child("inventory")

    private fun getUserIngredientsRef(userId: String) = database.getReference("users").child(userId).child("ingredients")

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

    fun removeInventoryItem(
        userId: String,
        itemId: String,
        onComplete: ((Boolean, Exception?) -> Unit)? = null,
    ) {
        getUserInventoryRef(userId)
            .child(itemId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    FirebaseStorageManager.deleteItemImage(
                        userId,
                        "inventory",
                        itemId)
                }
                onComplete?.invoke(task.isSuccessful, task.exception)
            }
    }

    // Listen to changes in the inventory data; attaches a ValueEventListener.
    fun listenToInventory(
        userId: String,
        listener: ValueEventListener,
    ) {
        getUserInventoryRef(userId).addValueEventListener(listener)
    }

    fun listenIngredients(
        userId: String,
        listener: ValueEventListener,
    ) {
        getUserIngredientsRef(userId).addValueEventListener(listener)
    }

    fun getUserGroceryListRef(userId: String) = database.getReference("users").child(userId).child("groceryList")

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

    fun removeGroceryListItem(
        userId: String,
        itemId: String,
        onComplete: ((Boolean, Exception?) -> Unit)? = null,
    ) {
        getUserGroceryListRef(userId)
            .child(itemId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseStorageManager.deleteItemImage(
                        userId,
                        "groceryList",
                        itemId
                    )
                }
                onComplete?.invoke(task.isSuccessful, task.exception)
            }
    }

    /**
     * Save the user’s avatar download URL under
     * users/{uid}/avatarUrl in Realtime Database.
     */
    fun setAvatarUrl(userId: String, url: String) {
        database
            .getReference("users")
            .child(userId)
            .child("avatarUrl")
            .setValue(url)
    }

    /**
     * Return a DatabaseReference pointing to
     * users/{uid}/avatarUrl so callers can listen to it.
     */
    fun getAvatarUrlRef(userId: String) =
        database
            .getReference("users")
            .child(userId)
            .child("avatarUrl")


    fun listenToGroceryList(
        userId: String,
        listener: ValueEventListener,
    ) {
        getUserGroceryListRef(userId).addValueEventListener(listener)
    }
}
