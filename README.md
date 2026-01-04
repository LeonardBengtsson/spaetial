Utility Minecraft mod for faster building and world editing.

# Installation

Place the mod's .jar file into your minecraft installation's `mods/` directory. See releases at [releases/](https://github.com/LeonardBengtsson/spaetial/releases).

Dependencies (make sure Minecraft version is matching):
- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api/)
- [Mod Menu](https://modrinth.com/mod/modmenu/) (optional)

# Usage

While in game, toggle the mod ON/OFF with the `\` key.

The mod's functionality is split between so called 'editing states', which are controlled using keyboard/mouse input. The default state when the mod is toggled ON is `Normal`.

The following hotkeys are common to multiple editing states:

| Key          | Function                           |
|--------------|------------------------------------|
| LMB          | Confirm                            |
| RMB          | Cancel                             |
| Z            | Undo                               |
| Alt Z        | Redo                               |
| Ctrl Scroll  | Move selection                     |
| Alt RMB      | Move selection to cursor           |
| Ctrl Alt RMB | Move selection to cursor (surface) |
| G            | 2D move selection                  |

## `Normal` state

| Key          | Function                         |
|--------------|----------------------------------|
| Alt LMB      | Start cuboid selection           |
| Ctrl Alt LMB | Start cuboid selection (surface) |
| Y            | Quick replace                    |
| Ctrl Y       | Quick replace (surface)          |
| Alt Y        | Quick set                        |
| Ctrl Alt Y   | Quick set (surface)              |
| V            | Paste                            |
| Ctrl V       | Paste (surface)                  |
| Alt V        | Paste (including air)            |
| Ctrl Alt V   | Paste (surface, including air)   |

## `Cuboid selection` state

Selection manipulation:

| Key             | Function                           |
|-----------------|------------------------------------|
| LMB             | Extend selection                   |
| Alt LMB         | Extend selection (surface)         |
| Alt Scroll      | Resize selection                   |
| Ctrl Alt Scroll | Resize selection (from back)       |
| Alt G           | 2D resize selection                |

Operations:

| Key   | Function             |
|-------|----------------------|
| X     | Cut                  |
| Alt X | Cut (including air)  |
| C     | Copy                 |
| Alt C | Copy (including air) |

## `Copy/Cut` state

| Key   | Function                                   |
|-------|--------------------------------------------|
| Tab   | Cycle mode (Clone/Line stack/Volume stack) |
| 1     | Switch to Clone mode                       |
| 2     | Switch to Line stack mode                  |
| 3     | Switch to volume stack mode                |
| V     | Confirm and continue                       |
| Alt V | Confirm (including air) and continue       |

Line stack mode:

| Key             | Function             |
|-----------------|----------------------|
| Alt Scroll      | Change stacking size |
| Ctrl Alt Scroll | Change spacing       |
| Ctrl Alt G      | 2D Change spacing    |

Volume stack mode:

| Key             | Function                |
|-----------------|-------------------------|
| Alt Scroll      | Change stacking size    |
| Alt G           | 2D Change stacking size |
| Ctrl Alt Scroll | Change spacing          |
| Ctrl Alt G      | 2D Change spacing       |
