package org.jitsi.jicofo.conference

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jitsi.config.withNewConfig
import org.jitsi.jicofo.jibri.JibriRecorder
import org.jitsi.jicofo.jibri.JibriSession
import org.jitsi.xmpp.extensions.jibri.JibriIq
import org.jxmpp.jid.impl.JidCreate

private fun createConference(): Triple<JitsiMeetConferenceImpl, JibriRecorder, JibriSession> {
    val conference = JitsiMeetConferenceImpl(
        JidCreate.entityBareFrom("room@example.com"),
        mockk(relaxed = true),
        HashMap(),
        java.util.logging.Level.INFO,
        null,
        false,
        mockk(relaxed = true)
    )

    val sessions = mutableListOf<JibriSession>()
    val sessionMock: JibriSession = mockk(relaxed = true)
    val jibriRecorder: JibriRecorder = mockk(relaxed = true) {
        every { jibriSessions } answers { sessions }
        every { handleStartRequest(any()) } answers {
            sessions.add(sessionMock)
            JibriIq()
        }
    }
    val recorderField = JitsiMeetConferenceImpl::class.java.getDeclaredField("jibriRecorder")
    recorderField.isAccessible = true
    recorderField.set(conference, jibriRecorder)

    return Triple(conference, jibriRecorder, sessionMock)
}

private fun createParticipant(name: String): Participant {
    val bareJid = JidCreate.bareFrom("$name@example.com")
    val occupantJid = JidCreate.entityFullFrom("room@example.com/$name")
    val chatMember = mockk<org.jitsi.jicofo.xmpp.muc.ChatRoomMember> {
        every { jid } returns bareJid
        every { occupantJid } returns occupantJid
    }
    return mockk {
        every { this.chatMember } returns chatMember
        every { isUserParticipant } returns true
    }
}

private val addMethod = JitsiMeetConferenceImpl::class.java.getDeclaredMethod("userParticipantAdded", Participant::class.java).apply {
    isAccessible = true
}

private val removeMethod = JitsiMeetConferenceImpl::class.java.getDeclaredMethod("userParticipantRemoved", Participant::class.java).apply {
    isAccessible = true
}

class AutoRecordingTest : ShouldSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    should("start and stop recording based on auto-record users") {
        withNewConfig("""
            jicofo.jibri.auto-record = true
        """) {
            val (conference, jibriRecorder, sessionMock) = createConference()
            val participant = createParticipant("user1")

            addMethod.invoke(conference, participant)
            verify(exactly = 1) { jibriRecorder.handleStartRequest(any()) }

            removeMethod.invoke(conference, participant)
            verify(exactly = 1) { sessionMock.stop(any()) }
        }
    }

    should("not start recording when auto-record is disabled") {
        withNewConfig("""
            jicofo.jibri.auto-record = false
        """) {
            val (conference, jibriRecorder, _) = createConference()
            val participant = createParticipant("user1")

            addMethod.invoke(conference, participant)
            verify(exactly = 0) { jibriRecorder.handleStartRequest(any()) }
        }
    }

    should("start once for multiple users and stop when last leaves") {
        withNewConfig("""
            jicofo.jibri.auto-record = true
        """) {
            val (conference, jibriRecorder, sessionMock) = createConference()
            val participant1 = createParticipant("user1")
            val participant2 = createParticipant("user2")

            addMethod.invoke(conference, participant1)
            verify(exactly = 1) { jibriRecorder.handleStartRequest(any()) }

            addMethod.invoke(conference, participant2)
            verify(exactly = 1) { jibriRecorder.handleStartRequest(any()) }

            removeMethod.invoke(conference, participant1)
            verify(exactly = 0) { sessionMock.stop(any()) }

            removeMethod.invoke(conference, participant2)
            verify(exactly = 1) { sessionMock.stop(any()) }
        }
    }
})
