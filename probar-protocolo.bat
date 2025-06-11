@echo off
echo ========================================
echo    PRUEBA DEL PROTOCOLO CLIENTE-SERVIDOR
echo ========================================
echo.

echo Este script te ayudara a probar el protocolo de comunicacion.
echo.

:menu
echo Seleccione una opcion:
echo 1. Compilar proyecto
echo 2. Iniciar servidor (deje abierto)
echo 3. Probar cliente con servidor
echo 4. Probar cliente sin servidor (modo local)
echo 5. Ver logs del protocolo
echo 6. Salir
echo.
set /p opcion="Ingrese su opcion (1-6): "

if "%opcion%"=="1" goto compilar
if "%opcion%"=="2" goto servidor
if "%opcion%"=="3" goto cliente_servidor
if "%opcion%"=="4" goto cliente_local
if "%opcion%"=="5" goto logs
if "%opcion%"=="6" goto salir
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
echo.
echo IMPORTANTE: Asegurese de que MongoDB este ejecutandose en localhost:27017
pause
goto menu

:servidor
echo.
echo ========================================
echo INICIANDO SERVIDOR
echo ========================================
echo.
echo El servidor escuchara en puerto 50005
echo IMPORTANTE: Deje esta ventana abierta
echo Abra otra terminal para ejecutar el cliente
echo.
echo Presione Ctrl+C para detener el servidor
echo.
mvn exec:java -Dexec.mainClass="com.clinica.servidor.Servidor"
pause
goto menu

:cliente_servidor
echo.
echo ========================================
echo PROBANDO CLIENTE CON SERVIDOR
echo ========================================
echo.
echo NOTA: Asegurese de que el servidor este ejecutandose
echo El cliente intentara conectarse al servidor en puerto 50005
echo.
echo Funcionalidades a probar:
echo - Login a traves del servidor
echo - Busqueda de productos via protocolo
echo - Actualizacion de unidades via protocolo
echo.
mvn clean javafx:run
pause
goto menu

:cliente_local
echo.
echo ========================================
echo PROBANDO CLIENTE EN MODO LOCAL
echo ========================================
echo.
echo NOTA: NO inicie el servidor para esta prueba
echo El cliente detectara que no hay servidor y usara MongoDB directamente
echo.
echo Funcionalidades a probar:
echo - Login directo a MongoDB
echo - Busqueda directa en MongoDB
echo - Actualizacion directa en MongoDB
echo.
mvn clean javafx:run
pause
goto menu

:logs
echo.
echo ========================================
echo INFORMACION DEL PROTOCOLO
echo ========================================
echo.
echo CODIGOS DE OPERACION:
echo 1 - LOGIN_REQUEST
echo 2 - BUSCAR_FARMACIA_REQUEST  
echo 3 - OBTENER_FARMACIA_REQUEST
echo 4 - ACTUALIZAR_UNIDADES_REQUEST
echo.
echo CODIGOS DE RESPUESTA:
echo 100 - LOGIN_RESPONSE
echo 101 - BUSCAR_FARMACIA_RESPONSE
echo 102 - OBTENER_FARMACIA_RESPONSE
echo 103 - ACTUALIZAR_UNIDADES_RESPONSE
echo.
echo CODIGOS DE ESTADO:
echo 200 - SUCCESS
echo 201 - LOGIN_SUCCESS
echo 401 - LOGIN_FAILED
echo 500 - SERVER_ERROR
echo 501 - DATABASE_ERROR
echo.
echo FORMATO DE MENSAJE:
echo CODIGO^|PARAMETRO1:PARAMETRO2:ID_MENSAJE
echo.
echo Ejemplo login: 1^|usuario:password:123
echo Ejemplo busqueda: 2^|aspirina:456
echo.
pause
goto menu

:salir
echo.
echo Saliendo...
exit /b 0 