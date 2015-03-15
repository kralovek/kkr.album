@echo off


set DIR_SPRING=%MAVEN_REPOSITORY%\org\springframework

set DIR_ENV=d:\GIT\kkr.album\env
set JAVA=%JAVA_HOME%\bin\java.exe
set JAR_ALBUM=%MAVEN_REPOSITORY%\kkr\album\kkr.album\0.0.1\kkr.album-0.0.1.jar
set JARS_SPRING=%DIR_SPRING%\spring-core\3.2.0.RELEASE\spring-core-3.2.0.RELEASE.jar;%DIR_SPRING%\spring-beans\3.2.0.RELEASE\spring-beans-3.2.0.RELEASE.jar;%DIR_SPRING%\spring-context\3.2.0.RELEASE\spring-context-3.2.0.RELEASE.jar;%DIR_SPRING%\spring-expression\3.2.0.RELEASE\spring-expression-3.2.0.RELEASE.jar
set JAR_LOG4J=%MAVEN_REPOSITORY%\log4j\log4j\1.2.17\log4j-1.2.17.jar;%MAVEN_REPOSITORY%\commons-logging\commons-logging\1.1.3\commons-logging-1.1.3.jar;%MAVEN_REPOSITORY%\kkr\deeplog\kkr.deeplog\0.0.1\kkr.deeplog-0.0.1.jar
set JAR_DEPENDENCES=%MAVEN_REPOSITORY%\org\apache\sanselan\sanselan\0.97-incubator\sanselan-0.97-incubator.jar

set CLASSPATH=%DIR_ENV%;%JAR_ALBUM%;%JAR_LOG4J%;%JARS_SPRING%;%JAR_DEPENDENCES%

 
