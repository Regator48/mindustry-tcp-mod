# TCP over UDP - Mindustry Mod

Force TCP connections instead of UDP to reduce error snapshots.

## Features

- Toggle TCP mode from the main menu
- Forces all network sends through TCP
- Works on vanilla servers (hidden from compatibility check)
- Setting persists across restarts

## Installation

### Option 1: Download Release

1. Go to [Releases](https://github.com/Regator48/mindustry-tcp-mod/releases)
2. Download `tcp-mod.jar`
3. Copy to your Mindustry mods folder:
   - **Linux:** `~/.local/share/Mindustry/mods/`
   - **Windows:** `%appdata%/Mindustry/mods/`
   - **macOS:** `~/Library/Application Support/Mindustry/mods/`
4. Restart Mindustry

### Option 2: Build from Source

```bash
git clone https://github.com/Regator48/mindustry-tcp-mod.git
cd mindustry-tcp-mod
./gradlew jar
cp build/libs/tcp-mod.jar ~/.local/share/Mindustry/mods/
```

## Usage

1. Launch Mindustry
2. Click "TCP Mode" on the main menu
3. Toggle ON/OFF
4. The setting persists across restarts

## How It Works

The mod uses a dynamic proxy to intercept Mindustry's `sendClient` method. When enabled, all network packets are sent through TCP instead of UDP.

- TCP = reliable, ordered delivery
- UDP = fast but can lose packets

By forcing TCP, you avoid UDP error snapshots that can cause disconnects.

## Multiplayer

- Works on vanilla servers (mod is hidden from compatibility check)
- Client-side only - server doesn't need the mod
- If you experience issues, toggle OFF before connecting

## Requirements

- Mindustry v159.6 or later
- Java 17+

## License

MIT
