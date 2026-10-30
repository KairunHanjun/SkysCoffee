# Run from PowerShell: .\start-demo.ps1
param([int]$Port = 8081)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$portableRoot = Join-Path (Split-Path $PSScriptRoot -Parent) '.portfolio-tools'
$portableJdk = Get-ChildItem -LiteralPath $portableRoot -Directory -Filter 'jdk-21*' -ErrorAction SilentlyContinue | Select-Object -First 1
if ($portableJdk) { $env:JAVA_HOME = $portableJdk.FullName }
if (-not $env:JAVA_HOME) { throw 'Install JDK 21 and set JAVA_HOME, then retry.' }
if ($portableJdk) { $env:GRADLE_USER_HOME = Join-Path $portableRoot 'gradle-cache' }
& .\gradlew.bat bootRun "--args=--spring.profiles.active=demo --server.port=$Port" --console=plain
exit $LASTEXITCODE
