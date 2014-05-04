@echo off

set DIR_SPRING=c:\MAVEN_REPOSITORY\org\springframework

set JAVA=%JAVA_HOME%\bin\java.exe
set JAR_ALBUM=%MAVEN_REPOSITORY%\kkr\album\kkr.album\0.0.1\kkr.album-0.0.1.jar
set JARS_SPRING=%DIR_SPRING%\spring-core\3.2.0.RELEASE\spring-core-3.2.0.RELEASE.jar;%DIR_SPRING%\spring-beans\3.2.0.RELEASE\spring-beans-3.2.0.RELEASE.jar;%DIR_SPRING%\spring-context\3.2.0.RELEASE\spring-context-3.2.0.RELEASE.jar
set JAR_LOG4J=c:\MAVEN_REPOSITORY\log4j\log4j\1.2.17\log4j-1.2.17.jar
set CLASSPATH=%JAR_ALBUM%;%JARS_SPRING%;%JAR_LOG4J%

 
