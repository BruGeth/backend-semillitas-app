$ErrorActionPreference = "Stop"

$url = "http://localhost:4173/login"

$carpeta = Join-Path $PSScriptRoot "..\evidencias\lighthouse"

New-Item -ItemType Directory -Force -Path $carpeta |
    Out-Null

$salida = Join-Path $carpeta "login-semillitas"

Write-Host "====================================="
Write-Host " AUDITORIA LIGHTHOUSE - SEMILLITAS"
Write-Host "====================================="
Write-Host "URL evaluada: $url"
Write-Host "Generando reportes..."

& npx --yes lighthouse $url `
    "--only-categories=accessibility,performance,best-practices,seo" `
    --preset=desktop `
    --output=html `
    --output=json `
    "--output-path=$salida" `
    --chrome-flags="--headless"

if ($LASTEXITCODE -ne 0) {
    throw "La auditoria Lighthouse fallo."
}

Write-Host ""
Write-Host "Auditoria finalizada correctamente."
Write-Host "Reportes guardados en: $carpeta"
