# 'A Distant Journey' Core  (ADJ Core)

This is the 'core' mod of [A Distant Journey](https://github.com/kohy-creates/A-Distant_Journey/), handling some mod incompatibilities, reworks that couldn't be done with pure datapacks/configs/KubeJS, and just handling stuff that would otherwise be impossible to do or VERY tedious outside of pure KJS. Oh, and since KubeJS was mentioned twice already, it *does* come with its own KubeJS plugin - with a few events and a few methods made specifically for use with the engine.

## Features
- Porting the mod Auditory (with some changes here and there).
- Making bows slightly inaccurate.
- Reducing max levels of vanilla enchantments.
- Adding a Campfire regeneration buff (there were mods for those but I wanted to add a custom effect for it).
- Changing how the damage handler works and removing invincibility frames from attacks (or capping them at certain values).
- Configuring potion effects, their amplifiers and duration.
- Adding and handling a few new attributes.
- Removing Resistance effect's hardcoded damage reduction in favor of the new attributes.
- Buffs the Conduit.
- Disables critical hits and sweep attacks (doesn't disable sweeping visuals and sounds).
- Makes it unable to equip 2 of the same Curios.
- Removing every Curio slot type except a few selected ones (configurable list).
- Adding Curio exclusion groups using datapacks.
- Reworking Botania's Terra Blade sword.
- Adding a way to register Terarria-like Wing items.
- Misc methods for KubeJS scripts to utilize and exposing some private mod methods and fields (e.g. FTBQuest's) 

## KubeJS Plugin

Most of the events and methods provided are ADJ-specific and **will not** work outside the modpack (or are unlikely to).

## ASSET NOTICE

The `tiara_wing_N.json` and `tiara_wing_N.png` asset files come from Botania mod. They are only used for testing purposes since the code for wings renderer was ~~ripped~~ based on Botania's. All credits for creating these goes to their authors, as I take none.
