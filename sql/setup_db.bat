@echo off
set PGPASSWORD=renaido
echo === ELIMINANDO BD atc79 ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "DROP DATABASE IF EXISTS atc79;"
echo.
echo === CREANDO BD + TABLAS + DATOS ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -f "D:\DAW\PRG\PRG\proyectosJava\Terminal-ATC-79\sql\init.sql"
echo.
echo === VERIFICACION ===
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "\dt"
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "SELECT airport_code, name FROM airports ORDER BY airport_code;"
pause
