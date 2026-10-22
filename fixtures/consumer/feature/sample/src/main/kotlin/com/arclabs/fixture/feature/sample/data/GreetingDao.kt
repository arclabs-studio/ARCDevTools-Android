package com.arclabs.fixture.feature.sample.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

/** Room 3 DAO for [GreetingEntity] (Room 3 DAO functions are suspending or reactive). */
@Dao
interface GreetingDao {
    @Insert
    suspend fun insert(greeting: GreetingEntity)

    @Query("SELECT * FROM greeting ORDER BY id DESC LIMIT 1")
    suspend fun latest(): GreetingEntity?
}
