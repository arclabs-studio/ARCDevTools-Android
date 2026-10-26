package com.arclabs.fixture.feature.sample.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** A stored salutation; the most recent row wins. */
@Entity(tableName = "greeting")
data class GreetingEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val salutation: String)
