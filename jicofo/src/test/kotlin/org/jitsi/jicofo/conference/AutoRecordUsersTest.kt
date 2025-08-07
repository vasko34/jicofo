package org.jitsi.jicofo.jibri

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly

class AutoRecordUsersTest : ShouldSpec({
    should("return the hardcoded set of auto-record users") {
        JibriConfig.getAutoRecordUsers() shouldContainExactly setOf(
            "user1@example.com",
            "user2@example.com"
        )
    }
})
