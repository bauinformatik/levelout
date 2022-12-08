Run BIMserver (JAR)
===================


Via Starter
----------------
This assumes you have a packaged `bimserverjar-<version>.jar`, either from the [Github releases](https://github.com/opensourceBIM/BIMserver/releases) or from a custom build.  

For startup from the GUI, just double-click the JAR and fill in the parameters as described in the [BIMserver wiki](https://github.com/opensourceBIM/BIMserver/wiki/JAR-Starter) .

For startup from the commandline, run `java -jar bimserverjar-<version>.jar`.


Unpacked BIMserver jar and libraries 
-----------------------
Once the start has been run, the contents are unpacked. Alternatively, you can unpack with the following command:

`mkdir <folder> && cd <folder> && jar xf ..\bimserverjar-1.5.183-SNAPSHOT.jar`

From the unpacked folder, the JarBimServer can be run as follows with the current and lib folder contents on the classpath:

~~~
java -cp .;lib/* org.bimserver.JarBimServer address=localhost port=8082 homedir=<homedir>
~~~

LocalDevBimServerStarter uses a fixed adresse and port (localhost:8080) and configures BIMserver with a standard admin user on startup. It also allows to load plugins during startup, see below.

~~~
java -cp .;lib/* org.bimserver.LocalDevBimServerStarterJar -home <homedir>
~~~


From IDE
-------------------------
When developing BIMserver core code checked out from <https://github.com/opensourcebim/bimserver>, then `org.bimserver.LocalDevBimServerStarter` can be run from within an IDE. As its JAR-companion (see above), allows to load specified plugins on startup, see below.     


Further Options
-------

These options are interesting for every way to run BIMserver.

* Memory: `-Xmx8490m -Xss1024k` 
* Logging: `-Dorg.apache.cxf.Logger=org.apache.csf.common.logging.Slf4jLogger`
* Debugging: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`



Plugin bundle installation
===================================

Via BIMvie.ws

* _Admin / Available Plugin Bundles / Install from File ..._

Via JSON interface

* `AdminInterface.installPluginBundle`, `..FromFile`, `..FromUrl`
* TODO full code snippet

For local development during startup use LocalDevBimServerStarterJar:

~~~
java -cp .;lib/* org.bimserver.LocalDevBimServerStarterJar -plugins <path/to/project/dir> -home <path/to/home/dir>
~~~

Full call with logging and debugging options:

~~~
java -Dorg.apache.cxf.Logger=org.apache.csf.common.logging.Slf4jLogger  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -cp .;lib/* org.bimserver.LocalDevBimServerStarterJar -plugins <path/to/plugin/dir> -home <path/to/home/dir`
~~~


Plugin usage
================

Checking service via BIMvie.ws

* Add to project: _Project / Tab Tree / Drop-down top-right / Add Internal Service / Add_
* List and delete configured services: _Project / Tab Services_
* Automatically triggered on upload of new revision
* Re-run: _Project / Tab Revisions / Actions Drop-down / Run "..."_
* Results: _Project / Tab Extended Data_, click on line e.g. title, not on schema link

Serializer via BIMvie.ws:

* Download: _Project / Tree tab / Drop-down top-right / Download / Serializer_


Debug and test plugin code
===================

Debug plugins with BIMserver (started externally with debugging option) in Eclipse:

* _Run / Debug Configurations / Remote Java Application_
* _New Configuration_ and change port to 5005 (or other port setup during BIMserver startup)

Debug plugins with BIMserver started internally from within IDE:

 * TODO



Build plugins
=================

Install dependencies

~~~
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=gml-v_3_2_1-2.6.2-SNAPSHOT.jar -Dmaven.repo.local=</path/to/local/repo>
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=indoorgml-v_1_0-2.6.2-SNAPSHOT.jar -Dmaven.repo.local=</path/to/local/repo>
~~~

Maven build


