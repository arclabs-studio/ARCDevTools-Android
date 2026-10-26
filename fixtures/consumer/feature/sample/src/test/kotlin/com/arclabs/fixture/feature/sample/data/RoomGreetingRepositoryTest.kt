package com.arclabs.fixture.feature.sample.data

import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** Runs the repository against an in-memory Room 3 database through the real DAO (Robolectric). */
@RunWith(RobolectricTestRunner::class)
class RoomGreetingRepositoryTest {
    private lateinit var database: SampleDatabase

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), SampleDatabase::class.java)
                .setDriver(AndroidSQLiteDriver())
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `falls back to Hello when nothing is stored`() =
        runTest {
            // Given
            val sut = makeSut()

            // When
            val salutation = sut.salutation()

            // Then
            assertEquals("Hello", salutation)
        }

    @Test
    fun `returns the most recently stored salutation`() =
        runTest {
            // Given
            val dao = database.greetingDao()
            dao.insert(GreetingEntity(salutation = "Hola"))
            dao.insert(GreetingEntity(salutation = "Ciao"))
            val sut = makeSut()

            // When
            val salutation = sut.salutation()

            // Then
            assertEquals("Ciao", salutation)
        }

    private fun makeSut() = RoomGreetingRepository(database.greetingDao())
}
