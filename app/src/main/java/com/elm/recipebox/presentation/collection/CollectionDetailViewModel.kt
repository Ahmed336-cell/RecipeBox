package com.elm.recipebox.presentation.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val getCollectionByIdUseCase: GetCollectionByIdUseCase,
    private val editCollectionUseCase: EditCollectionUseCase,
    private val addToCollectionUseCase: AddToCollectionUseCase,
    private val removeFromCollectionUseCase: RemoveFromCollectionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _collection = MutableStateFlow<RecipeCollection?>(null)
    val collection: StateFlow<RecipeCollection?> = _collection.asStateFlow()
    
    fun loadCollection(collectionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            getCollectionByIdUseCase(collectionId)
                .onSuccess { collectionData ->
                    _collection.value = collectionData
                }
                .onFailure { exception ->
                }
            _isLoading.value = false
        }
    }
    
    fun updateCollection(collection: RecipeCollection) {
        viewModelScope.launch {
            editCollectionUseCase(collection)
                .onSuccess {
                    loadCollection(collection.id)
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun addRecipeToCollection(recipeId: Long, collectionId: Long) {
        viewModelScope.launch {
            addToCollectionUseCase(recipeId, collectionId)
                .onSuccess {
                    loadCollection(collectionId)
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun removeRecipeFromCollection(recipeId: Long, collectionId: Long) {
        viewModelScope.launch {
            removeFromCollectionUseCase(recipeId, collectionId)
                .onSuccess {
                    loadCollection(collectionId)
                }
                .onFailure { exception ->
                }
        }
    }
}
