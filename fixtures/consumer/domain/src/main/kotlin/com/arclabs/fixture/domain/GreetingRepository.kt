package com.arclabs.fixture.domain

/** Source of the salutation that opens a greeting, e.g. "Hello". Implemented by the data layer. */
interface GreetingRepository {
    /** The salutation to greet with. */
    suspend fun salutation(): String
}
