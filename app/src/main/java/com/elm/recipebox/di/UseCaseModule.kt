package com.elm.recipebox.di

import com.elm.recipebox.domain.repository.RecipeRepository
import com.elm.recipebox.domain.repository.CollectionRepository
import com.elm.recipebox.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideAddRecipeUseCase(repository: RecipeRepository): AddRecipeUseCase {
        return AddRecipeUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetRecipeByIdUseCase(repository: RecipeRepository): GetRecipeByIdUseCase {
        return GetRecipeByIdUseCase(repository)
    }
    
}