@echo off
setlocal enabledelayedexpansion
echo ============================================
echo   AI Cognitive Health System
echo   One-Click Startup
echo ============================================
echo.

set MYSQL_USER=root
set MYSQL_PASS=123456
set DB_NAME=cognitive_health
set "PROJECT_DIR=%~dp0"

echo [1/4] Checking MySQL...
where mysql >nul 2>&1
if !errorlevel! neq 0 (
    echo [ERROR] mysql not found in PATH!
    pause
    exit /b 1
)

mysql -u %MYSQL_USER% -p%MYSQL_PASS% -e "SELECT 1" 2>nul
if !errorlevel! neq 0 (
    echo [ERROR] Cannot connect to MySQL!
    pause
    exit /b 1
)
echo [OK] MySQL connected

echo.
echo [2/4] Checking database...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% --default-character-set=utf8mb4 -e "USE %DB_NAME%" 2>nul
if !errorlevel! neq 0 (
    echo [INFO] Database not found, creating...
    mysql -u %MYSQL_USER% -p%MYSQL_PASS% --default-character-set=utf8mb4 < "%PROJECT_DIR%src\main\resources\init-complete.sql" 2>nul
    
    echo [INFO] Waiting for database to be ready...
    set RETRY=0
    :RETRY_DB
    timeout /t 2 /nobreak >nul
    set /a RETRY+=1
    mysql -u %MYSQL_USER% -p%MYSQL_PASS% --default-character-set=utf8mb4 -e "USE %DB_NAME%" 2>nul
    if !errorlevel! neq 0 (
        if !RETRY! lss 5 (
            echo [INFO] Retry !RETRY!/5...
            goto RETRY_DB
        )
        echo [ERROR] Init failed after 5 retries!
        pause
        exit /b 1
    )
    echo [OK] Database created
) else (
    echo [OK] Database exists
)

echo.
echo [3/4] Starting backend on port 8080...
cd /d "%PROJECT_DIR%"
start "Backend" /min cmd /c "mvnw.cmd spring-boot:run"

echo Waiting for backend...
timeout /t 10 /nobreak >nul

echo.
echo [4/4] Starting frontend on port 3000...
cd /d "%PROJECT_DIR%frontend"
if exist node_modules (
    start "Frontend" /min cmd /c "npm run dev"
) else (
    echo Installing dependencies...
    start "Frontend" /min cmd /c "npm install && npm run dev"
)

echo.
echo ============================================
echo   Startup complete!
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:3000
echo   Login:    admin / admin123
echo ============================================

timeout /t 5 /nobreak >nul
start http://localhost:3000/login

pause
