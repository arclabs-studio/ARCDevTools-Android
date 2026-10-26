package com.arclabs.fixture.feature.sample

import com.arclabs.fixture.domain.GreetingRepository

/** Hand-written fake: always answers with the salutation it was created with. */
class FakeGreetingRepository(private val salutation: String) : GreetingRepository {
    override suspend fun salutation(): String = salutation
}
