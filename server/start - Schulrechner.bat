@echo off
title 1.21.1 Spigot Server
cls
"C:\Program Files\Java\jdk-21\bin\java.exe" -Xmx4096M -Xms4096M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar spigot-1.21.1.jar nogui
PAUSE
