package com.arclabs.fixture.domain

/** Hand-written fake: always answers with the salutation it was created with. */
class FakeGreetingRepository(private val salutation: String) : GreetingRepository {
    override suspend fun salutation(): String = salutation
}
