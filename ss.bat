
@echo off
echo GOTO %1

if %1.==eclipse. goto eclipse
if %1.==docs. goto docs

rem normal build
echo BUILD
mvn -Dmaven.test.skip=true clean package install
goto jend

:eclipse
mvn eclipse:eclipse
goto jend

:docs
mvn javadoc:javadoc
goto jend


:jend
echo Done.
