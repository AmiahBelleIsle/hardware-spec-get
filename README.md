Currently a work in progress

A JavaFX application that collects the user's hardware information
and then displays it in an interactive GUI.

Makes use of the following:
* [JavaFX](https://openjfx.io/) - For the GUI
* [OSHI](https://github.com/oshi/oshi) - For collecting hardware information
* [Jackson](https://github.com/FasterXML/jackson) - For storing and loading data to and from JSON files.

If you want to build this yourself, OSHI does not have a module declaration, so you will have
to add it yourself. A helpful tool for this is [Module Info Inject](https://github.com/DraqueT/Module-Info-Inject),
by DraqueT. However, if you are getting an error about multi-release, you will also have to download the source
to add in that argument yourself.