@echo off
set PGPASSWORD=renaido
echo === BASES DE DATOS ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -l | findstr atc79
echo.
echo === TABLAS EN atc79 ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "\dt"
echo.
echo === ESTRUCTURA players ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "\d players"
echo.
echo === ESTRUCTURA game_sessions ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "\d game_sessions"
pause
