package com.ruthvikbr.runtracker.di

import android.content.Context
import androidx.room.Room
import com.ruthvikbr.runtracker.db.RunDatabase
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

}