cmd /c "mvn clean package -P %1 -Dquarkus.package.type=fast-jar -DskipTests=true"
if not %ERRORLEVEL% == 0 (
    echo "[ERROR] ----------------------[ maven build error ]-----------------------"
    exit 1
)

:dockerbuild
cmd /c "cd nautible-app-ms-stock-build & docker build -t %IMAGE% -f ./src/main/docker/Dockerfile.jvm ."
