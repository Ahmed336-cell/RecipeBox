package com.elm.recipebox.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionUiState(
    val collections: List<RecipeCollection> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val editCollectionUseCase: EditCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val addToCollectionUseCase: AddToCollectionUseCase,
    private val removeFromCollectionUseCase: RemoveFromCollectionUseCase
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _collections = MutableStateFlow<List<RecipeCollection>>(emptyList())
    val collections: StateFlow<List<RecipeCollection>> = _collections.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val uiState: StateFlow<CollectionUiState> = combine(
        _collections,
        _isLoading
    ) { collections, isLoading ->
        CollectionUiState(
            collections = collections,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CollectionUiState()
    )
    
    val filteredCollections: StateFlow<List<RecipeCollection>> = combine(
        collections,
        searchQuery
    ) { collections, query ->
        if (query.isBlank()) {
            collections
        } else {
            collections.filter { collection ->
                collection.name.contains(query, ignoreCase = true) ||
                collection.description.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        loadCollections()
    }
    
    fun loadCollections() {
        viewModelScope.launch {
            _isLoading.value = true
            getCollectionsUseCase()
                .collect { collections ->
                    _collections.value = collections
                    _isLoading.value = false
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun createCollection(name: String, description: String = "") {
        viewModelScope.launch {
            createCollectionUseCase(name, description)
                .onSuccess { collectionId ->
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun updateCollection(collection: RecipeCollection) {
        viewModelScope.launch {
            editCollectionUseCase(collection)
                .onSuccess {
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun deleteCollection(collection: RecipeCollection) {
        viewModelScope.launch {
            deleteCollectionUseCase(collection)
                .onSuccess {
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun addRecipeToCollection(recipeId: Long, collectionId: Long) {
        viewModelScope.launch {
            addToCollectionUseCase(recipeId, collectionId)
                .onSuccess {
                }
                .onFailure { exception ->
                }
        }
    }
    
    fun removeRecipeFromCollection(recipeId: Long, collectionId: Long) {
        viewModelScope.launch {
            removeFromCollectionUseCase(recipeId, collectionId)
                .onSuccess {
                }
                .onFailure { exception ->
                }
        }
    }
}