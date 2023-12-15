package com.example.reddit.di

import android.content.Context
import com.example.reddit.data.repository.RedditRepositoryImpl
import com.example.reddit.data.repository.SharedPreferencesRepositoryImpl
import com.example.reddit.domain.repository.RedditRepository
import com.example.reddit.domain.repository.SharedPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(@ApplicationContext context: Context): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(context = context)
    }

    @Provides
    @Singleton
    fun provideRedditRepository(): RedditRepository {
        return RedditRepositoryImpl()
    }
}