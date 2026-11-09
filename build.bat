@echo off
rem Baut WeDoText.jar und das Abgabe-ZIP (Doppelklick genuegt).
rem Vorher die Dokumentation als PDF speichern: docs\Dokumentation.html im Browser oeffnen -> Drucken -> "Als PDF speichern".
cd /d "%~dp0"

if exist build\classes rmdir /s /q build\classes
mkdir build\classes
javac -encoding UTF-8 -d build\classes src\editor\Main.java src\editor\model\*.java src\editor\view\*.java src\editor\controller\*.java || goto :error

if not exist dist mkdir dist
echo Main-Class: editor.Main> build\manifest.txt
jar cfm dist\WeDoText.jar build\manifest.txt -C build\classes . || goto :error

if exist build\abgabe rmdir /s /q build\abgabe
mkdir build\abgabe
copy dist\WeDoText.jar build\abgabe\ >nul
copy docs\Dokumentation.pdf build\abgabe\ >nul
xcopy src build\abgabe\src\ /e /i /q >nul
powershell -NoProfile -Command "Compress-Archive -Path build\abgabe\* -DestinationPath dist\WeDoText_Abgabe.zip -Force" || goto :error

echo.
echo Fertig: dist\WeDoText.jar und dist\WeDoText_Abgabe.zip
pause
exit /b 0

:error
echo.
echo FEHLER beim Bauen.
pause
exit /b 1
