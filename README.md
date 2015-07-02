# rundeck-httppost-plugin
Rundeck plugin which sends a http post as a rundeck workflow step
-----------------------------------------------------------------

[![Build Status](https://api.travis-ci.org/rvs-fluid-it/rundeck-httppost-plugin.svg)](https://travis-ci.org/rvs-fluid-it/rundeck-httppost-plugin)

Usage:

Put the plugin jar in the `/var/lib/rundeck/libext` folder.
(Get the jar from the maven central repository or create it yourself entering `mvn install` on the commandline)

Add a httppost step to a Rundeck job and configure the httppost step by entering values for
the _Remote Url_, _Post Parameters_ and _Step Description_ plugin properties. Placeholders for entered Rundeck job option values (e.g. ${option._name_.value}) in the _Remote Url_ and the _Post Parameters_ property of the plugin will be expanded.

[Released version in Maven Central](http://search.maven.org/remotecontent?filepath=be/fluid-it/tools/rundeck/plugins/rundeck-httppost-plugin/0.1-1/rundeck-httppost-plugin-0.1-1.jar) : 
```
<dependency>
    <groupId>be.fluid-it.tools.rundeck.plugins</groupId>
    <artifactId>rundeck-httppost-plugin</artifactId>
    <version>0.1-1</version>
</dependency>
```

