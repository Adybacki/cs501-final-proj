package com.example.grocerywise

import androidx.lifecycle.ViewModel
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel to prevent store user recipes to avoid recalling api if inventory not changed
class InventoryViewModel : ViewModel() {
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val _prev = mutableListOf<InventoryItem>()
    private val _rcpResponse = mutableListOf<RecipeResponse>()
val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems // Apply distinctUntilChanged
    val Rcplist: List<RecipeResponse> = _rcpResponse
    val pre: List<InventoryItem> = _prev

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
                    }
                },
            )
        }
    }
    fun Memo(currentSnapshot: List<InventoryItem>){
        _prev.clear()
        _prev.addAll(currentSnapshot)

    }
    fun MemoRecipeLlist(currentSnapshot: List<RecipeResponse>){
        _rcpResponse.clear()
        _rcpResponse.addAll(currentSnapshot)
    }


}
