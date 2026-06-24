# Sign an unsigned release APK for sideload install (phones reject unsigned APKs).
# Prefers release keystore env; falls back to debug keystore for local bench.
# Usage: pwsh scripts/sign-apk-sideload.ps1 [-InputApk path] [-OutputApk path]
param(
    [string]$InputApk = "",
    [string]$OutputApk = ""
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

if ([string]::IsNullOrWhiteSpace($InputApk)) {
    $InputApk = Join-Path $Root "examples\android\app\build\outputs\apk\release\app-release-unsigned.apk"
}
if ([string]::IsNullOrWhiteSpace($OutputApk)) {
    $OutputApk = Join-Path (Split-Path -Parent $InputApk) "app-release-signed.apk"
}

$envFile = Join-Path $env:USERPROFILE ".obdforge\signing.env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)=(.*)$') {
            Set-Item -Path "env:$($Matches[1])" -Value $Matches[2].Trim('"')
        }
    }
}

$signRelease = Join-Path $Root "scripts\sign-release-apk.ps1"
if ($env:OBDFORGE_KEYSTORE_PATH -and $env:OBDFORGE_KEYSTORE_PASSWORD -and (Test-Path $env:OBDFORGE_KEYSTORE_PATH)) {
    & $signRelease -InputApk $InputApk -OutputApk $OutputApk
    exit 0
}

$debugSign = Join-Path $Root "scripts\sign-apk-debug.ps1"
if (Test-Path $debugSign) {
    & $debugSign -InputApk $InputApk -OutputApk $OutputApk
    Write-Host "NOTE: signed with local debug keystore — configure release keystore for stable sideload updates"
    exit 0
}

throw "No signing key available. Set OBDFORGE_KEYSTORE_* or ensure ~/.android/debug.keystore exists."
