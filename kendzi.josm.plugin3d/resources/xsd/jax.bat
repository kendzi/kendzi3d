
set java=c:\Program Files\Java\jdk1.6.0_17\
set java=c:\Program Files\Java\jdk1.6.0_23\

"%java%\bin\xjc.exe"  -d ..\..\src_xsd -p kendzi.josm.kendzi3d.dto.xsd PointModels.xsd TextureLibrary.xsd

rem http://jaxb.java.net/tutorial/section_1_3-Hello-World.html#Hello%20World

pause;