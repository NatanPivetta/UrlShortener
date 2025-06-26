@echo off
REM Script para build dos serviços Quarkus em paralelo (sem testes)

REM Caminho base do projeto (modifique conforme necessário)
cd /d D:\Projetos\UrlShortener

echo =============================
echo  Iniciando builds em paralelo
echo =============================

REM Build do urlshortener
start "urlshortener" cmd /c "cd urlshortener && gradlew clean build -x test"

REM Build do kafkaconsumers
start "kafkaconsumers" cmd /c "cd kafkaconsumers && gradlew clean build -x test"

REM Build do auth-service
start "auth-service" cmd /c "cd auth-service && gradlew clean build -x test"

echo =============================
echo  Builds em execução nas janelas abertas...
echo =============================

pause
