package com.example.grocerywise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged


class InventoryViewModel : ViewModel() {
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val _prev = mutableListOf<InventoryItem>()
    private val _rcpResponse = mutableListOf<RecipeResponse>()
//    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.distinctUntilChanged().stateIn(scope = viewModelScope)
val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems // Apply distinctUntilChanged
    val Rcplist: List<RecipeResponse> = _rcpResponse
//    .stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000L),
//        initialValue = emptyList()
//    )
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
                        Log.e("InventoryViewModel", "Data fetch error: ${error.message}")
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
