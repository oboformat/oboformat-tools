@echo off

SETLOCAL ENABLEEXTENSIONS

SETLOCAL ENABLEDELAYEDEXPANSION

IF NOT DEFINED OBORUNNERMAIN (
  echo OBORUNNERMAIN variable not set, do not run this script by itself.
  echo Use obolib-obo2owl.bat, obolib-owl2obo.bat, or obolib-gui.bat
  exit /B -1
)


FOR %%F IN (../lib/*.jar) DO (

  IF DEFINED cp (

    SET cp=!cp!;../lib/%%F%

  ) ELSE (

    SET cp=../lib/%%F%

  )
)



echo java -Xmx2048M -cp "%cp%" %OBORUNNERMAIN% %*

java -Xmx2048M -cp "%cp%" %OBORUNNERMAIN% %*