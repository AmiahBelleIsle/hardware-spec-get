## Overview

A JavaFX application that collects the user's hardware information
and then displays it in an interactive GUI.

Makes use of the following:
* [JavaFX](https://openjfx.io/) - For the GUI
* [OSHI](https://github.com/oshi/oshi) - For collecting hardware information
* [Jackson](https://github.com/FasterXML/jackson) - For storing and loading data to and from JSON files.

## Functions

* Choose an image of your choice to display alongside your hardware information.
* Organize how the information is displayed by moving entries up and down.
* Ability to disable entries you don't want displayed.

Planned but not yet implemented functions include: setting the icon that appears next to each entry, changing the colors of the application, and ability to create custom entries.

## Preview Image

![v.0.1 app](https://raw.githubusercontent.com/AmiahBelleIsle/hardware-spec-get/main/images/spec-get-v0.1.png)

## Releases

Available for Linux and Windows.

[v0.1](https://github.com/AmiahBelleIsle/hardware-spec-get/releases/tag/v0.1) (Current Release)

## Building

If you want to build this yourself, you will have to add module declarations to some dependencies before you can JLink.
A helpful tool for this is [Module Info Inject](https://github.com/DraqueT/Module-Info-Inject), by DraqueT. You will
likely get an error about multi-release for the slf4j-api dependency, in which case you will have to download the
source of the above tool to add in the `--multi-release=21` arg.
