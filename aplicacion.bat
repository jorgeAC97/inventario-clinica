@echo off
title Aplicacion Clinica

echo ==========================================
echo   APLICACION INVENTARIO CLINICA
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
echo [2/2] Iniciando aplicacion JavaFX...
echo ==========================================
echo   APLICACION INICIADA
echo   Cerrando ventana para terminar
echo ==========================================
echo.

call mvn javafx:run

echo.
echo Aplicacion cerrada.
pause 