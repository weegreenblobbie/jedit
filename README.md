How to build jEdit 5.3.0 on Windows 10 64-bit
---------------------------------------------

1. Install the git client for windows, for basic POXIX commands like `unzip`, `tar`, and `patch`
1. Install Python for Windodws to be able to run `build.py`.
1. Install the Java JDK 64-bit from Oracle, Java SE Development Kit 8u162 worked for me.
1. Download the [jEdit source code](https://sourceforge.net/projects/jedit/files/jedit/5.3.0/jedit5.3.0source.tar.bz2/download) to the local dir
1. Download the [Apache Ant](http://mirror.olnevhost.net/pub/apache//ant/binaries/apache-ant-1.10.1-bin.zip) to the local dir
1. Inspect `config.json` to ensure paths and names are correct.

Then just run the build script:

    python build.py
    # ...
    BUILD SUCCESSFUL
    Total time: 1 minute 35 seconds

You may need to exit an existing jEdit session before running the one you built:

    cd jEdit\build && jedit.jar

Click on Help -> About jEdit to check the version.
