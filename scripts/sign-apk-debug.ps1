# Sign an unsigned release APK with the local debug keystore for ADB device smoke tests.
# Usage: pwsh scripts/sign-apk-debug.ps1 -InputApk path/to/app-release-unsigned.apk [-OutputApk path]
param(
    [Parameter(Mandatory = $true)]
    [string]$InputApk,
    [string]$OutputApk = ""
)

$ErrorActionPreference = "Stop"
if (-not (Test-Path $InputApk)) { throw "Input APK not found: $InputApk" }

$ks = Join-Path $env:USERPROFILE ".android\debug.keystore"
if (-not (Test-Path $ks)) { throw "Debug keystore not found: $ks" }

$buildTools = Get-ChildItem (Join-Path $env:LOCALAPPDATA "Android\Sdk\build-tools") -Directory |
    Sort-Object Name -Descending | Select-Object -First 1
$apksigner = Join-Path $buildTools.FullName "apksigner.bat"
if (-not (Test-Path $apksigner)) { throw "apksigner not found under build-tools" }

if ([string]::IsNullOrWhiteSpace($OutputApk)) {
    $dir = Split-Path -Parent $InputApk
    $OutputApk = Join-Path $dir "app-release-adb-smoke.apk"
}

& $apksigner sign --ks $ks --ks-pass pass:android --key-pass pass:android --out $OutputApk $InputApk
Write-Host "Signed APK: $OutputApk"
