package com.arclabs.fixture.feature.sample.data

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.arclabs.fixture.domain.GreetingRepository
import com.arclabs.fixture.domain.GreetingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt bindings of the sample feature: the Room database, its DAO and the greeting use case. */
@Module
@InstallIn(SingletonComponent::class)
object SampleDataModule {
    private const val DATABASE_NAME = "sample.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SampleDatabase =
        Room
            .databaseBuilder(context, SampleDatabase::class.java, DATABASE_NAME)
            .setDriver(AndroidSQLiteDriver())
            .build()

    @Provides
    fun provideGreetingDao(database: SampleDatabase): GreetingDao = database.greetingDao()

    @Provides
    fun provideGreetingRepository(repository: RoomGreetingRepository): GreetingRepository = repository

    @Provides
    fun provideGreetingUseCase(repository: GreetingRepository): GreetingUseCase = GreetingUseCase(repository)
}
