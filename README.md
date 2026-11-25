# Placebo - Unofficial Port

Placebo is a shared library used by the Apotheosis family of Minecraft mods. It supplies dynamic data registries, recipe helpers, networking, menus and other common utilities.

Original project: [Shadows_of_Fire / Brennan Ward](https://github.com/Shadows-of-Fire/Placebo). Based on upstream revision `8fcfa1e6e947e8f0f12044ad31ce4bef2145982a` (10.0.2).

## Installation
Minecraft 26.3, NeoForge 26.3.0.118, Java 25. Download the mod JAR from [Releases](https://github.com/JGorbachof/placebo-26-3/releases). Install it on client and server. This library does not include Apotheosis or its other dependent mods.

## Alpha testing
The isolated server GameTests passed for dynamic registries, legacy potion/container brewing, item caching and legacy recipe generation. Client rendering, multiplayer sync, live datapack reload, full registry datagen and dependent-mod compatibility remain unverified.

## Source and license
Build with `gradlew.bat build` on Windows or `./gradlew build` on Linux using Java 25. The project retains the upstream MIT license, with inherited LGPL-2.1 and Apache-2.0 code notices in LICENSES. See PORT_NOTES.md for changes and API migration notes.

[ModHub](https://jgorbachof.github.io/) | [Discord](https://discord.gg/3zPAp2vk6Q)
