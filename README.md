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

Build Customization
===================

There are very few things that actually need to be changed in the build,
configuration – at least to start out with.
But there are though a couple of variables you need to change in
'build.gradle'.

First off, you will have to change the name of the project `projectName`.
The `version` variable should also generally be changed. Remember not to
remove the SNAPSHOT suffix from the version string, or at least only do so
when you have a version ready for release. In future versioning of builds will
be handled a bit smarter and you will not have to worry too much about it.

It is a good idea to keep references to dependencies you use in several
subprojects in the 'config/dependencies.groovy' file. That way you can keep
versions consistent. There is of course nothing that is stopping your from
declaring dependencies on an ad-hoc basis. But this can quickly get out of
hand in larger projects.

If you do not want to use Jira or use some other CVS than Git you will also
have to change some settings in the 'config/deployment.groovy' file.

Coding standards and formatting is checked using checkstyle. You can edit them
to fit your project in 'config/checkstyle/checkstyle.xml'.

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
