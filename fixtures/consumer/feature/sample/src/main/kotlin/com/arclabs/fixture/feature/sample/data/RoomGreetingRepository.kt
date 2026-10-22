package com.arclabs.fixture.feature.sample.data

import com.arclabs.fixture.domain.GreetingRepository
import javax.inject.Inject

/** [GreetingRepository] backed by Room: the latest stored salutation, or "Hello" when none is stored. */
class RoomGreetingRepository @Inject constructor(private val dao: GreetingDao) : GreetingRepository {
    override suspend fun salutation(): String = dao.latest()?.salutation ?: DEFAULT_SALUTATION

    private companion object {
        const val DEFAULT_SALUTATION = "Hello"
    }
}
