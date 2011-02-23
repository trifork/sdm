Introduction
============

This project is meant as starting point for Trifork projects that use Gradle 
as build-system.

Add this repository as a remote in git and pull from it. That way you can also
easily get any future additions.

Take a look in 'build.gradle', 'settings.gradle' and in the config-directory
to customize the build to fit your project.

There are two example sub-projects, one Java and one Scala.

Suggestions and patches (or pull requests) are very welcome. If you encounter
any bugs please make an issue on Github.

Git Tips
========

It is a good idea to check your code before you push anything to other central
repositories. While you don't necessarily want to run tests and check code for
every commit you do locally.

You can setup a git command alias to check your code before you push anything.

    git config alias.publish '! gradle check && git push "$@"'

This will allow you to e.g. write:

    git publish origin master

as an alternative to `git push`. Please note that since you will be running
tests and other code checks this will usually take considerably longer than
a normal push.

Requirements
============

This build setup is made for Gradle 9.2+.

If you want to run the Scala subproject you will need to have install a
version of Scala.
