# Description

This is application for generating 3d model from OSM data. It can be used as JOSM plug-in for editing and preview. It can be used as standalone application for generate image from 3d data or as tomcat application building open-layer tiles.

# How to build

This project is currently migrating to maven
Most of sub-project you can build be using command 
maven install

JOSM plug-in "kendzi.josm.plugin3d" is build by ant script.

# How to import to eclipse
Currently conversion to maven is in progress. Build will change in future. 
Only one of subproject is build by ant all others are maven project. 

* Install java JDK 8 64bit from java.oracle.com
* Install Eclipse EE form eclipse.org
* Install Eclipse plugins from marketplace: "Help > Eclipse Marketplace"
 * Subversive
 * Checkstyle
 * FindBugs
 * If it is not Eclipse EE don't forget about m2e plugin
* Download sources for JOSM as new java project with the same name. Svn url: 
http://josm.openstreetmap.de/svn/trunk
* Check if JOSM is building correctly by running ant:
{JOSM}/build.xml dist
* Download sources of kendzi3d-josm-jogl project. It is container for JOSM native libraries for JOSM. Git link:
https://github.com/kendzi/kendzi3d-josm-jogl.git
* Setup path for JOSM builded jar in file (line 42):
```{kendzi3d-josm-jogl}/build.xml```
* Compile kendzi3d-josm-jogl by calling ant script
```{kendzi3d-josm-jogl}/build.xml clean dist```
* Download source of kendzi3d. Git link: 
https://github.com/kendzi/kendzi3d.git
 * import from local git repo as java ant project: kendzi.josm.plugin3d
 * import all other sub projects from local git working directory as java maven projects. If need you can import it as general project and later convert to maven by clicking on project: "Convigure>Convert to Maven"
* Compile all maven project by using command:
``` {kendzi3d-plugin-build}/pom.xml clean install ```
This step is important. It will generate temporary directors used by and script! All subproject should pass!

* Setup formatter
Chose Menu > Preferences > Java > Code Style > Formatter > Import...
Select file ```doc/kendzi3d_formatter.xml```

* Setup save actions:
Chose Menu > Preferences > Java > Editor > Save Actions
Select settings like on image ```doc/save_actions.png```


# License

This software is provided "AS IS" without a warranty of any kind.  You use it on your own risk and responsibility!!!

This program is shared on license BSDv3 more information in file BSD3.
Some parts of program as source, images, models may be shared on different licenses. In case of doubt ask.
