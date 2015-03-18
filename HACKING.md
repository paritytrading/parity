Developer Guide
===============

Welcome to Parity Developer Guide. The purpose of this document is to help you
get started with developing Parity on your own workstation.

Developing Parity requires Java Development Kit (JDK) 8 or newer and Maven.


Test
----

Run the tests with Maven:

    mvn test


Build
-----

Build the artifacts with Maven:

    mvn package

Maven puts the artifacts into a `target` directory under each module.


Run
---

After building the artifacts, the executables can be found in the `target`
directories. Their filenames have the following format:

    <module>-<version>.jar

Run an executable with Java:

    java -jar <executable>

If an executable requires a configuration file, an example of one can be found
as `devel.conf` in the `etc` directory under the module.
