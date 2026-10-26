package com.arclabs.fixture.feature.sample.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

/** Room 3 database of the sample feature. */
@Database(entities = [GreetingEntity::class], version = 1)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun greetingDao(): GreetingDao
}
