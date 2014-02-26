# Installation from command line
* git clone https://github.com/kendzi/kendzi3d.git
* cd kendzi3d/kendzi3d-parent
* mvn install

# Installation inside eclipse

* Download and install java 1.6 JDK

* Download and install Eclipse 4.2 EE

* download kendzi3d project using egit
```
Window > Perspective > Git Repository Exploring
```
```
Git Repositories > Clone Git repository
```
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install1.png)
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install2.png)

* import git workspace as maven project and select all sub modules
```
File>Import>Maven>Existing Maven Project
```
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install3.png)

* compile and install artifacts using maven
```
Click on file kendzi3d-parent >  pom.xml > Run as > Maven install
```
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install4.png)

Project should compile with status success!
* update maven dependency
```
Right click on project kendzi3d-parent > Maven > Update project... > Select All > Ok
```
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install5.png)

* create run configuration
```
Menu > Run > Run Configurations... > Java Application > New
```
 
```
Project: kendzi3d-plugin
Main class: org.openstreetmap.josm.gui.MainApplication
Classpath: Maven Dependencies
```
![](https://raw.github.com/kendzi/kendzi3d/master/doc/install6.png)

* run JOSM
```
Menu > Run > Run history > kendzi3d
```

* Add kendzi3d pluging to inside JOSM
```
F12 > Plugins > Kendzi3d
```

* restart JOSM

* optional JOSM sources and kendzi-math sources can be download but they are not required to run application


