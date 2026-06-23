# Sign a release APK with the OBDForge release keystore (post-build).
# Usage: pwsh scripts/sign-release-apk.ps1 [-InputApk path] [-OutputApk path]
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
        if ($_ -match '^\s*export\s+(\w+)=(.*)$') {
            Set-Item -Path "env:$($Matches[1])" -Value ($Matches[2] -replace '^"|"$', '')
        } elseif ($_ -match '^\s*(\w+)=(.*)$') {
            Set-Item -Path "env:$($Matches[1])" -Value ($Matches[2] -replace '^"|"$', '')
        }
    }
}

$ks = $env:OBDFORGE_KEYSTORE_PATH
$ksPass = $env:OBDFORGE_KEYSTORE_PASSWORD
$alias = if ($env:OBDFORGE_KEY_ALIAS) { $env:OBDFORGE_KEY_ALIAS } else { "obdforge" }
$keyPass = if ($env:OBDFORGE_KEY_PASSWORD) { $env:OBDFORGE_KEY_PASSWORD } else { $ksPass }

if (-not $ks -or -not $ksPass) {
    throw "Set OBDFORGE_KEYSTORE_PATH and OBDFORGE_KEYSTORE_PASSWORD (or ~/.obdforge/signing.env)"
}
if (-not (Test-Path $InputApk)) { throw "Input APK not found: $InputApk" }
if (-not (Test-Path $ks)) { throw "Keystore not found: $ks" }

$buildTools = Get-ChildItem (Join-Path $env:LOCALAPPDATA "Android\Sdk\build-tools") -Directory |
    Sort-Object Name -Descending | Select-Object -First 1
$apksigner = Join-Path $buildTools.FullName "apksigner.bat"
if (-not (Test-Path $apksigner)) { throw "apksigner not found under build-tools" }

& $apksigner sign `
    --ks $ks `
    --ks-key-alias $alias `
    --ks-pass "pass:$ksPass" `
    --key-pass "pass:$keyPass" `
    --out $OutputApk `
    $InputApk

& $apksigner verify --verbose $OutputApk
Write-Host "Signed APK: $OutputApk"
