@echo off
echo Iniciando servidor de la clinica...
echo.

REM Compilar el proyecto
echo Compilando proyecto...
mvn compile

if %ERRORLEVEL% neq 0 (
    echo Error al compilar el proyecto
    pause
    exit /b 1
)

echo.
echo Iniciando servidor en puerto 50005...
echo Presiona Ctrl+C para detener el servidor
echo.

REM Ejecutar el servidor
mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"

pause 