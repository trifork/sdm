Introduction
============

This project uses Gradle as build-system. It is a good idea to become familiar
with Gradle, and the [Gradle Userguide](http://gradle.org/documentation.html)
is a very good reference.

There are two ways of using Gradle to build the project. You can either
install it on your computer, ot you can use the wrapper script 'gradlew' that
can be found in the root of the source directory.

For the rest of this document we assume you are using the wrapper.

User Guide
----------

After cloning the repository you will have to take a few additional steps to
get everything up and running.

*   Create a MySQL database with the needed tables:

        % mysqladmin create sdm_warehouse
        % mysql -u _USERNAME_ sdm_warehouse < db/schema.sql

    If you want to call your database something else you can configure it in the
    config.properties files found in the subprojects. Just make sure not to push
    personal setting to the shared repository.

*   Check that everything is working:

        % ./gradlew test
    
    This will run all the tests in all the sub-projects. The tests include
    some integration tests and can take some time to complete.
    
    You can also run each tests in each sub-project individually, e.g.
    
        % ./gradlew importer:test
    
    will run all the tests in the importer project. 

*   Generate an eclipse project:
    
        % ./gradlew eclipse
    
    This will generate 1 eclipse project for each of the sub-projects (found in 
    the subprojects directory) and an additional project for the root.

    You will have to manually import the projects into eclipse using the _Import_
    dialog. You will only need to do this step once.
    
    NB. If you use Idea's IDE you can generate a project for that too, but this
    is currently not configured. Please contribute if you set it up. ;)

*   Running the application:

    You can run the application directly from command line:
    
        % ./gradlew importer:jettyRun
    
    The downside to this is that it will only run one sub-project at a time.

    You can also use the eclipse to deploy the application to J2EE application
    server. This is recommended because you can control the server directly
    from the IDE and deploy several WAR (one for each sub-project). 

    If you use eclipse, I recommend not using the 'Preview' server type since
    you will not get any console output.

    There are two projects you can run. The importer, and the replication
    service.

*   Configure OIOSAML:
    
    Currently you can skip this step.
    
    FIXME. This is currently disabled because of problems with certificates
    and the RID2CPR service at TDC.

*   Access the administration GUI:

    The administration GUI is a browser based GUI for administering user
    rights and access restrictions. You can find it on:

        http://localhost:8080/replication/admin/users

    and the importer status page on:

        http://localhost:8080/importer/importer

    Of course you will have to substitute the host and port in the URL to fit
    your setup.

DB Schema
---------

If you alter the database schema there are three things you must do:

1.  Alter schema.sql to reflect the newest version of the schema.
2.  Alter the diagram.mwb file using MySQL Workbench to reflect the changes,
    and produce a new version of the diagram.pdf using the tool.
3.  Create a migration from the previous version of the schema to the new one
    and place the migration file in the 'db/migrations' directory. 

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

To prevent git from telling you about config files that you do not want to
commit, issue the following command:

    git update-index --assume-unchanged subprojects/importer/src/main/resources/config.properties subprojects/replication/src/main/resources/config.properties

