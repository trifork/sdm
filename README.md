Introduction
============

This project uses Gradle as build-system. It is a good idea to become familiar
with Gradle, and the [Gradle Userguide](http://gradle.org/documentation.html)
is a very good reference.

Build Customization
-------------------

Take a look in 'gradle.properties', 'build.gradle', 'settings.gradle' and in
the config-directory to customize the build to fit your project.

There are very few things that actually need to be changed in the build
configuration – at least to start out with.
But there are though a couple of properties you need to change in
'gradle.properties'.

First off, you will have to change the name of the `projectName` and
`nextReleaseVersion` properties. These properties are defined in
`gradle.properties` file. The properties are described in greater detail in
the file itself.

Coding standards and formatting is checked using Checkstyle. You can edit them
to fit your project in `config/checkstyle/checkstyle.xml`.

Dependency Management
---------------------

It is a good idea to keep a list of dependencies you use in several
sub-projects in the `config/libraries.groovy` file. That way you can keep
versions consistent. There is of course nothing that is stopping your from
declaring dependencies on an ad-hoc basis in your sub-projects. But this can
quickly get out of hand for larger projects.

Releases & Snapshots
--------------------

When you apply the deployment-plugin you get a few extra tasks to help you
share your artifacts.

It is important to have a well-defined and consistent way of sharing jar files
and other artifacts. Maven repos are the de-facto standard for artifact
sharing and by using it you get a high degree of interoperability between
build systems.

When using the deployment-plugin it is important to manage your build version.
The property `version` should always update it when you have made a release to
the repository. It is defined in the 'gradle.properties' file.

You will only be able to release an artifact of a given version once. If you
try to redeploy a release artifact, the deployment will fail (as it should).
If you have made a mistake in a release, the only thing you can do is to
confess you messed up and make a new release with a version bump.

Deploy a release artifact:

    gradle deployRelease

While releases are fine when you are actually finished with an iteration or
some other milestone, it is not always convenient to use release artifacts
during active development. Therefore when you want to share your diamonds in
the rough, you can use _Snapshot_ versions. Snapshots of a given version can
be deployed with any number of times. For people familiar with Ivy, snapshots
can be used as a 'latest integration' dependencies.

Deploy a snapshot artifact:

    gradle deploySnapshot

When making a release it is important to make sure that you don't
depend on any snapshot artifacts. Since snapshots change over time, future
snapshot versions will potentially break your release (which is bad).

So make sure you have no '-SNAPSHOT' dependencies when you call
`deployRelease` or at the very least, as few of them as possible.

Git Tips
--------

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
------------

This build setup is made for Gradle 1.0-milestone-1+.
