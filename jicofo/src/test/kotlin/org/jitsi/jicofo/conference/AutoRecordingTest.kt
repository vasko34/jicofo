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

class AutoRecordingTest : ShouldSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    should("start and stop recording based on auto-record users") {
        withNewConfig("""
            jicofo.jibri.auto-record = true
        """) {
            // Create conference with minimal dependencies.
            val conference = JitsiMeetConferenceImpl(
                JidCreate.entityBareFrom("room@example.com"),
                mockk(relaxed = true),
                HashMap(),
                java.util.logging.Level.INFO,
                null,
                false,
                mockk(relaxed = true)
            )

            // Mock JibriRecorder and its sessions list.
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

            // Create a participant whose bare JID is in the auto-record set.
            val bareJid = JidCreate.bareFrom("user1@example.com")
            val occupantJid = JidCreate.entityFullFrom("room@example.com/user1")
            val chatMember = mockk<org.jitsi.jicofo.xmpp.muc.ChatRoomMember> {
                every { jid } returns bareJid
                every { occupantJid } returns occupantJid
            }
            val participant = mockk<Participant> {
                every { chatMember } returns chatMember
                every { isUserParticipant } returns true
            }

            // Reflectively invoke userParticipantAdded/Removed.
            val addMethod = JitsiMeetConferenceImpl::class.java.getDeclaredMethod("userParticipantAdded", Participant::class.java)
            addMethod.isAccessible = true
            addMethod.invoke(conference, participant)
            verify(exactly = 1) { jibriRecorder.handleStartRequest(any()) }

            val removeMethod = JitsiMeetConferenceImpl::class.java.getDeclaredMethod("userParticipantRemoved", Participant::class.java)
            removeMethod.isAccessible = true
            removeMethod.invoke(conference, participant)
            verify(exactly = 1) { sessionMock.stop(null) }
        }
    }
})
