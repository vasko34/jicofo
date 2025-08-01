# Automatic Jibri recording

Jicofo can start and stop recordings automatically when a conference reaches
certain participant counts. When enabled, Jicofo instructs Jibri to start
recording as soon as there are at least `auto-start-participants` user
participants in the conference. Recording stops automatically once the number
of participants falls to `auto-stop-participants` or below. This behaviour is
controlled with the following configuration options in `jicofo.conf`:

```
jicofo {
  jibri {
    # Number of participants required in the conference before recording starts
    auto-start-participants = 2

    # Number of participants below which any active recording stops
    auto-stop-participants = 1

    # Enable or disable automatic recording
    auto-record = true
  }
}
```

If not explicitly set, the defaults of `2` and `1` are used for the participant
thresholds, and automatic recording is enabled. To disable automatic recording
set `auto-record = false` under the `jibri` section.
