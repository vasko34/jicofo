# Automatic Jibri recording

Jicofo can start and stop recordings automatically when a conference reaches
certain participant counts. This behaviour is controlled with the following
configuration options in `jicofo.conf`:

```
jicofo {
  jibri {
    # Number of participants required in the conference before recording starts
    auto-start-participants = 2

    # Number of participants below which any active recording stops
    auto-stop-participants = 1
  }
}
```

If not explicitly set, the defaults of `2` and `1` are used.
