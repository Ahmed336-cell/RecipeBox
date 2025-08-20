package com.elm.recipebox.di

import com.elm.recipebox.data.repository.RecipeRepositoryImpl
import com.elm.recipebox.data.repository.CollectionRepositoryImpl
import com.elm.recipebox.domain.repository.RecipeRepository
import com.elm.recipebox.domain.repository.CollectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository
    
    @Binds
    @Singleton
    abstract fun bindCollectionRepository(
        collectionRepositoryImpl: CollectionRepositoryImpl
    ): CollectionRepository
}