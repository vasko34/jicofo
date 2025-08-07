# Automatic Jibri recording

Jicofo can start and stop recordings automatically when specific users join or
leave a conference. When enabled, Jicofo instructs Jibri to start recording as
soon as one of a hardcoded set of users joins a conference while no other users
from the set are present. Recording stops automatically when none of the users
from the set remain in the conference. The set of users is defined in the
source code as an immutable constant and exposed via the
`JibriConfig.getAutoRecordUsers()` function. The returned set is unmodifiable
and contains `user1@example.com` and `user2@example.com` by default. The
membership list can be adjusted by modifying the constant in the source code
and inspected at runtime through `JibriConfig.getAutoRecordUsers()`.

This behaviour is controlled with the following configuration option in
`jicofo.conf`:

```
jicofo {
  jibri {
    # Enable or disable automatic recording
    auto-record = true
  }
}
```

Automatic recording is enabled by default (the default is specified in
`reference.conf`). To disable automatic recording set `auto-record = false`
under the `jibri` section.
