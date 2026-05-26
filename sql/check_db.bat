@echo off
set PGPASSWORD=renaido
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d atc79 -c "\dt"
pause
