eXtensible Binary Universal Protocol - Data Libraries
=====================================================

This repository contains experimental Java implementation of libraries for representation of various data structures.

XBUP is binary data protocol and file format for communication, data storage and application interfaces. 

Homepage: http://xbup.exbin.org  

Structure
---------

As the project is currently in alpha stage, repository contains complete resources for distribution package with following folders:

  * modules - Libraries and other
  * src - Sources related to building distribution packages
  * doc - Documentation + related presentations
  * deps - Folder for downloading libraries for dependency resolution
  * gradle - Gradle wrapper
  * resources - Related resource files, like sample files, images, etc.

Compiling
---------

Build commands: "gradle build" and "gradle distZip"

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle 7.1 build system is used: https://gradle.org

You can either download and install gradle or use gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

On the first build there will be an attempt to download all required dependecy modules and currently it's necessary to execute build twice.

Alternative is to have all dependecy modules stored in local maven repository - Manually download all dependencies from GitHub (clone repositories from github.com/exbin - see. deps directory for names) and run "gradle publish" on each of them.

License
-------

Project uses various libraries with specific licenses and some tools are licensed with multiple licenses with exceptions for specific modules to cover license requirements for used libraries.

Primary license: Apache License, Version 2.0 - see LICENSE-2.0.txt
