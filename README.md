Introduction
============

This project is meant as starting point for Trifork projects that use Gradle 
as build-system.

Add this repository as a remote in git and pull from it. That way you can also
easily get any future additions.

Take a look in 'build.gradle', 'settings.gradle' and in the config-directory
to customize the build to fit your project.

There are two example sub-projects, one java and one scala. Only the Java one
is enabled by default (see 'settings.gradle').

Suggestions and patches (or pull requests) are very welcome. If you encounter
any bugs please make an issue on Github.

Git Hooks
=========

TBA

Requirements
============

This build setup is made for Gradle 9.2+.

If you want to build the Scala subproject you will need to have install a
Scala compiler installed.
