# Driftbox Bird

A Flappy Bird minigame that lives inside your JetBrains IDE. Take a break between deploys.

## Play

**Tools → Driftbox Bird**, or click the bird icon in the right tool window bar.

- **Space / Click** — Flap
- **Arrow Up** — Also flap
- Dodge the purple pipes. Beat your high score.

## Install

Download the latest `.zip` from [Releases](https://github.com/elijamesku/driftbox-bird/releases), then:

**Settings → Plugins → ⚙️ → Install Plugin from Disk** → select the zip.

Or build from source:

```bash
./gradlew buildPlugin
```

The plugin zip lands in `build/distributions/`.

## Compatibility

- IntelliJ IDEA, WebStorm, PyCharm, GoLand, Rider, CLion, PhpStorm, RubyMine, DataGrip — any JetBrains IDE 2024.1+
- Requires JCEF (ships with all JetBrains IDEs by default)

## How it works

The game is a self-contained HTML5 Canvas page loaded via JCEF (JetBrains Chromium Embedded Framework) in a tool window panel. No external dependencies, no network calls. High scores persist in the browser's localStorage.

## License

MIT
