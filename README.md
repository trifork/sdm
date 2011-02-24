Introduction
============

This project is meant as starting point for Trifork projects that use Gradle
as build-system. It is a good idea to become familiar with Gradle, and the
[Gradle Userguide](http://gradle.org/documentation.html) is a very good
reference.

To get started make a git repository and add this repository as a remote:

    git remote add template git://github.com/trifork/gradle-template.git

That way you can also easily get any future additions:

    git pull template master

The template contains two example sub-projects, one Java and one Scala.

Suggestions and patches (or pull requests) are very welcome. If you encounter
any bugs please make an issue on Github.

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

If you do not want to use Jira or use some other SCM than Git you will also
have to change some settings in the `config/deployment.groovy` file.

Coding standards and formatting is checked using Checkstyle. You can edit them
to fit your project in `config/checkstyle/checkstyle.xml`.

Dependency Management
---------------------

It is a good idea to keep a list of dependencies you use in several
sub-projects in the `config/dependencies.groovy` file. That way you can keep
versions consistent. There is of course nothing that is stopping your from
declaring dependencies on an ad-hoc basis in your subprojects. But this can
quickly get out of hand for larger projects.

Check out [Gradle Java Dependencies](http://bit.ly/fwBqFF) to see how to
manage dependencies effectively.

Releases & Snapshots
--------------------

It is important to have a well-defined and uniform way of sharing jar files
and other artifacts. Maven repos are the de-facto standard for artifact
sharing. To leverage this and keep a high degree of interoperability.

The property `nextReleaseVersion` plays an important roll in this respect.
You should always update it when you have made a release to the repository.

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

When making a release artifact it is important to make sure that you don't
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

This build setup is made for Gradle 0.9.2+.

If you want to run the Scala subproject you will need to have install a
version of Scala.
