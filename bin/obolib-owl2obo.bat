@echo off

SET OBORUNNERMAIN=org.obolibrary.cli.OBORunners

CALL obolib-basic.bat %* --owl2obo

(SET OBORUNNERMAIN=)