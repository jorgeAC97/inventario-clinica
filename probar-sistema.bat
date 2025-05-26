@echo off
echo ========================================
echo    SISTEMA DE INVENTARIO CLINICA
echo ========================================
echo.

:menu
echo Seleccione una opcion:
echo 1. Compilar proyecto
echo 2. Iniciar servidor
echo 3. Iniciar cliente (aplicacion)
echo 4. Iniciar cliente sin servidor
echo 5. Salir
echo.
set /p opcion="Ingrese su opcion (1-5): "

if "%opcion%"=="1" goto compilar
if "%opcion%"=="2" goto servidor
if "%opcion%"=="3" goto cliente
if "%opcion%"=="4" goto cliente_local
if "%opcion%"=="5" goto salir
echo Opcion invalida. Intente de nuevo.
goto menu

:compilar
echo.
echo Compilando proyecto...
mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo Error al compilar el proyecto
    pause
    goto menu
)
echo Compilacion exitosa!
pause
goto menu

:servidor
echo.
echo Iniciando servidor en puerto 50005...
echo NOTA: Deje esta ventana abierta y abra otra terminal para el cliente
echo Presione Ctrl+C para detener el servidor
echo.
mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"
pause
goto menu

:cliente
echo.
echo Iniciando aplicacion cliente...
echo NOTA: Asegurese de que el servidor este ejecutandose
echo.
mvn clean javafx:run
pause
goto menu

:cliente_local
echo.
echo Iniciando aplicacion en modo local (sin servidor)...
echo.
mvn clean javafx:run
pause
goto menu

:salir
echo.
echo Saliendo...
exit /b 0 