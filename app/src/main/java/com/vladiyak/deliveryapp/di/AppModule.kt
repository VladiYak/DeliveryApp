package com.vladiyak.deliveryapp.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vladiyak.deliveryapp.utils.Constants.INTRODUCTION_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun providesIntroductionSharedPreferences(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore
}