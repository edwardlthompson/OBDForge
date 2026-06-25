# Install signed GitHub Release APK on a connected device (ADB bench).
# Handles signature mismatch by uninstalling the old package once, then retrying.
# Usage: pwsh scripts/install-github-release.ps1 [-Version 1.2.4]
param(
    [string]$Version = ""
)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Pkg = "dev.foss.obdforge"
if (-not $Version) {
    $Version = (Get-Content (Join-Path $Root ".template-version") -Raw).Trim()
}
$ApkName = "OBDForge-$Version.apk"
$Url = "https://github.com/edwardlthompson/OBDForge/releases/download/v$Version/$ApkName"
$Tmp = Join-Path $env:TEMP $ApkName

$adb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) { $adb = "adb" }

$devices = & $adb devices | Select-String "\tdevice"
if (-not $devices) {
    Write-Host "ERROR: no authorized ADB device"
    & $adb devices -l
    exit 1
}

Write-Host "Downloading $Url ..."
Invoke-WebRequest -Uri $Url -OutFile $Tmp -UseBasicParsing

function Try-Install {
    param([switch]$Replace)
    $args = @("install")
    if ($Replace) { $args += "-r" }
    $args += $Tmp
    & $adb @args 2>&1 | Out-String
}

Write-Host "Installing $ApkName ..."
$out = Try-Install -Replace
Write-Host $out
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK   Installed $Pkg v$Version"
    & $adb shell dumpsys package $Pkg | Select-String "versionName|versionCode" | Select-Object -First 2
    exit 0
}

if ($out -match "signatures do not match|UPDATE_INCOMPATIBLE") {
    Write-Host "WARN signature mismatch — uninstalling $Pkg (debug or old signing key) and retrying..."
    & $adb uninstall $Pkg 2>$null
    $out2 = Try-Install
    Write-Host $out2
    if ($LASTEXITCODE -ne 0) { throw "Install failed after uninstall: $out2" }
    Write-Host "OK   Installed $Pkg v$Version after uninstall"
    & $adb shell dumpsys package $Pkg | Select-String "versionName|versionCode" | Select-Object -First 2
    exit 0
}

if ($out -match "NO_CERTIFICATES|not signed") {
    throw "APK is unsigned — download OBDForge-X.Y.Z.apk from GitHub Releases only"
}

throw "Install failed: $out"
