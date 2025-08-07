package org.jitsi.jicofo.jibri

import io.kotest.core.spec.style.StringSpec
import kotlin.test.assertEquals

class JibriConfigAutoRecordUsersTest : StringSpec({
    "auto-record users set is exposed" {
        val expected = setOf(
            "user1@example.com",
            "user2@example.com"
        )
        assertEquals(expected, JibriConfig.getAutoRecordUsers())
    }
})
