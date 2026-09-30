Write-Host "Comprobando configuracion..."

if (-not (Test-Path ".env")) {
    Write-Error "No existe el archivo .env. Copia .env.example como .env."
    exit 1
}

Write-Host "Cargando configuracion..."

Get-Content ".env" | ForEach-Object {

    $line = $_.Trim()

    if (
        $line -ne "" -and
        -not $line.StartsWith("#") -and
        $line.Contains("=")
    ) {

        $parts = $line.Split("=", 2)

        $name = $parts[0].Trim()
        $value = $parts[1].Trim()

        Set-Item `
            -Path "Env:$name" `
            -Value $value
    }
}

$env:DB_URL =
    "jdbc:mariadb://localhost:3306/$env:DB_NAME"

Write-Host "Iniciando MariaDB..."

docker compose up -d --wait --wait-timeout 60

if ($LASTEXITCODE -ne 0) {

    Write-Error "MariaDB no se ha podido iniciar correctamente."

    Write-Host ""
    Write-Host "Logs de MariaDB:"
    docker compose logs --tail 30 mariadb

    exit 1
}

Write-Host "MariaDB disponible."

$jarPath =
    "target\tablesMariaDB-1.0-SNAPSHOT.jar"

if (-not (Test-Path $jarPath)) {

    Write-Host "El JAR no existe. Compilando proyecto..."

    mvn clean package

    if ($LASTEXITCODE -ne 0) {

        Write-Error "La compilacion Maven ha fallado."
        exit 1
    }
}

Write-Host "Iniciando aplicacion..."

java -jar $jarPath

if ($LASTEXITCODE -ne 0) {

    Write-Error "La aplicacion ha terminado con un error."
    exit 1
}