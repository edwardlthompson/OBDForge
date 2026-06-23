# Create a release signing keystore outside the repo (one-time human setup).
# Usage: pwsh scripts/generate-release-keystore.ps1 [-OutputPath path]
param(
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $env:USERPROFILE ".obdforge\obdforge-release.keystore"
}
$alias = if ($env:OBDFORGE_KEY_ALIAS) { $env:OBDFORGE_KEY_ALIAS } else { "obdforge" }

if (-not $env:OBDFORGE_KEYSTORE_PASSWORD) {
    throw "Set OBDFORGE_KEYSTORE_PASSWORD before running"
}
if (-not $env:OBDFORGE_KEY_PASSWORD) {
    $env:OBDFORGE_KEY_PASSWORD = $env:OBDFORGE_KEYSTORE_PASSWORD
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputPath) | Out-Null
if (Test-Path $OutputPath) { throw "Keystore already exists at $OutputPath" }

$dname = "CN=OBDForge Release, OU=Mobile, O=OBDForge, L=Local, ST=NA, C=US"
& keytool -genkeypair `
    -keystore $OutputPath `
    -alias $alias `
    -keyalg RSA `
    -keysize 4096 `
    -validity 9125 `
    -storepass $env:OBDFORGE_KEYSTORE_PASSWORD `
    -keypass $env:OBDFORGE_KEY_PASSWORD `
    -dname $dname

Write-Host "OK   Keystore created: $OutputPath"
Write-Host "Set OBDFORGE_KEYSTORE_PATH=$OutputPath"
