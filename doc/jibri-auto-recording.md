# Automatic Jibri recording

Jicofo can start and stop recordings automatically when specific users join or
leave a conference. When enabled, Jicofo instructs Jibri to start recording as
soon as one of a hardcoded set of users joins a conference while no other users
from the set are present. Recording stops automatically when none of the users
from the set remain in the conference. The set of users is defined in the
source code and returned by the `JibriConfig.getAutoRecordUsers()` function.
By default this set contains `user1@example.com` and `user2@example.com`, and
can be adjusted by modifying the function's return value.

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

Automatic recording is enabled by default. To disable automatic recording set
`auto-record = false` under the `jibri` section.
