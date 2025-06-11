@echo off
title Servidor Clinica

echo ==========================================
echo    SERVIDOR INVENTARIO CLINICA
echo ==========================================
echo.

echo [1/2] Compilando proyecto...
call mvn clean compile
if errorlevel 1 (
    echo.
    echo ERROR: No se pudo compilar el proyecto
    echo Revisa los errores mostrados arriba
    echo.
    pause
    exit /b 1
)

echo.
echo [2/2] Iniciando servidor...
echo ==========================================
echo   SERVIDOR ACTIVO EN PUERTO 50005
echo   Presiona Ctrl+C para detener
echo ==========================================
echo.

call mvn exec:java -Dexec.mainClass=com.clinica.servidor.Servidor

echo.
echo Servidor detenido.
pause 