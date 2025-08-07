# Automatic Jibri recording

Jicofo can start and stop recordings automatically when specific users join or
leave a conference. When enabled, Jicofo instructs Jibri to start recording as
soon as one of a hardcoded set of users joins a conference while no other users
from the set are present. Recording stops automatically when none of the users
from the set remain in the conference. Internally Jicofo keeps a count of
auto-record users in the room and triggers recording only when this count
transitions from 0 to 1 and stops when it returns to 0.

The set of users is defined in the source code as an immutable constant
`AUTO_RECORD_USERS` (containing `EntityBareJid` values). It is exposed via the
`JibriConfig.getAutoRecordUsers()` function, which returns an unmodifiable set
that can be inspected at runtime. A convenience
`JibriConfig.isAutoRecordUser(jid)` method is also available to test individual
JIDs. The returned set contains `user1@example.com` and `user2@example.com` by
default. The membership list can be adjusted by modifying the constant in the
source code. For example, retrieving the set in Kotlin looks like this:

```kotlin
val autoUsers: Set<EntityBareJid> = JibriConfig.getAutoRecordUsers()
```

The same call from Java returns an unmodifiable `Set`:

```java
Set<EntityBareJid> autoUsers = JibriConfig.getAutoRecordUsers();
```

This behaviour is controlled with the following configuration option in
`jicofo.conf`:

```
jicofo {
  jibri {
    # Enable or disable automatic recording (enabled by default)
    auto-record = true
  }
}
```

Automatic recording is enabled by default (the default is specified in
`reference.conf`). To disable automatic recording set `auto-record = false`
under the `jibri` section.
