# BunnyLoader for Rider

Create C# BunnyLoader Mods from Rider's native **New Solution** wizard, with a live preview that follows the IDE theme.

## Features

- Create a Mod through **New Solution → BunnyLoader → BunnyLoader Mod**.
- Preview the Mod icon, display name, author and version as you edit.
- Configure the project name, Mod ID, UUID, version and destination folder.
- Generate and open a `.slnx` solution automatically.
- Add ModItems from the directory context menu.

## Requirements

- JetBrains Rider 2026.1 (build 261).
- .NET 10 SDK available on `PATH`.
- BunnyLoader's `blmod` and `blitem` .NET templates installed separately. This plugin does not bundle the templates or the game API.

## Installation

In Rider, open **Settings → Plugins → ⚙ → Install Plugin from Disk**, select the plugin ZIP and restart Rider.

## Build

Use PowerShell and JDK 21:

```powershell
./build-local.ps1 -RiderPath "C:/path/to/Rider" -JdkPath "C:/path/to/jdk-21"
```

The installable ZIP is written to `build/distributions/`.

## License

Copyright (c) 2026 末泠. Licensed under the [MIT License](LICENSE). External IDE libraries, templates and game APIs retain their respective licenses and are not covered by this license.
