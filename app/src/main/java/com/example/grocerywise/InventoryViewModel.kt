package com.example.grocerywise

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InventoryViewModel : ViewModel() {
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())

    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems

    init {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseDatabaseManager.listenToInventory(
                userId,
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val items = snapshot.children.mapNotNull { it.getValue(InventoryItem::class.java) }
                        _inventoryItems.value = items
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("InventoryViewModel", "Data fetch error: ${error.message}")
                    }
                },
            )
        }
    }
}
