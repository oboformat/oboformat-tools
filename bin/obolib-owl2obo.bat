@echo off

SET OBORUNNERMAIN=org.obolibrary.cli.OBORunners

CALL bin\obolib-basic.bat %* --owl2obo

(SET OBORUNNERMAIN=)