package com.ruthvikbr.runtracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.ruthvikbr.runtracker.db.RunDatabase
import com.ruthvikbr.runtracker.utilities.Constants.KEY_FIRST_TIME_TOGGLE
import com.ruthvikbr.runtracker.utilities.Constants.KEY_NAME
import com.ruthvikbr.runtracker.utilities.Constants.KEY_WEIGHT
import com.ruthvikbr.runtracker.utilities.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunDatabase::class.java,
        "Run_Database"
        ).build()

    @Singleton
    @Provides
    fun getDao(db : RunDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun providesWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun providesFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)
}