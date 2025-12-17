@echo off

wsl.exe docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ===========================================================
    echo Docker no esta ejecutandose. Iniciando Docker Desktop...
    echo ===========================================================
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo Esperando a que Docker inicie...
    :wait_docker
    timeout /t 5 >nul
    wsl.exe docker info >nul 2>&1
    if %errorlevel% neq 0 (
        goto wait_docker
    )
    echo Docker esta ahora en ejecución.
)
set CLEAN=false
if "%1"=="clean" (
    set CLEAN=true
)

if "%CLEAN%"=="true" (
    echo ===========================================================
    echo Borrando contenedores, volumenes y redes...
    echo ===========================================================
    wsl.exe docker compose -f docker-compose-dev.yml --env-file '.env.dev' down -v
    wsl.exe docker system prune -f
    wsl.exe docker volume prune -f
    wsl.exe docker network prune -f
    echo ===========================================================
    echo Asegurando los formatos correctos de los scripts...
    echo ===========================================================
    wsl.exe sed -i 's/\r//' ./scripts/mysql/init-db.sh
)

wsl.exe docker volume ls -q --filter "name=kiwi-dev_mysql_data" | findstr /C:"kiwi-dev_mysql_data" >nul
if %errorlevel%==0 (
    echo ===========================================================
    echo La database existe.
    echo ===========================================================
    wsl.exe docker compose -f docker-compose-dev.yml --env-file '.env.dev' up --build -d
) else (
    echo ===========================================================
    echo La database no existe. Construyendo database...
    echo ===========================================================
    wsl.exe docker compose -f docker-compose-dev.yml --env-file '.env.dev' up --build db -d
    echo ===========================================================
    echo Construyendo el resto de servicios...
    echo ===========================================================
    wsl.exe docker compose -f docker-compose-dev.yml --env-file '.env.dev' up --build -d
)
