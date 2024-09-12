@echo off
title 1.21.1 Spigot Server
cls
"C:\Program Files\Zulu\zulu-21\bin\java.exe" -Xmx2048M -Xms2048M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar spigot-1.21.1.jar nogui
PAUSE
