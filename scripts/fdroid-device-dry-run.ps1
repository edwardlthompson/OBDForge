# F-Droid device dry-run (Windows). Usage: pwsh scripts/fdroid-device-dry-run.ps1
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$adb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) { $adb = "adb" }
$Launcher = "dev.foss.obdforge/dev.foss.obdforge.MainActivity"

$devices = & $adb devices | Select-String "\tdevice"
if (-not $devices) {
    Write-Host "No authorized device. Enable USB debugging and accept the RSA prompt."
    & $adb devices -l
    exit 1
}

$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot"
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$env:SOURCE_DATE_EPOCH = "1700000000"

Push-Location $Root
try {
    if (Get-Command bash -ErrorAction SilentlyContinue) {
        & "C:\Program Files\Git\bin\bash.exe" -lc "cd /c/Users/edwar/Desktop/OBDForge && bash scripts/verify-fdroid-metadata.sh"
    } else {
        Write-Host "SKIP verify-fdroid-metadata.sh (bash unavailable; CI gate covers metadata)"
    }

    $unsigned = Join-Path $Root "examples\android\app\build\outputs\apk\release\app-release-unsigned.apk"
    if (-not (Test-Path $unsigned)) {
        Push-Location (Join-Path $Root "examples\android")
        .\gradlew.bat clean assembleRelease --no-daemon
        Pop-Location
    }

    $signed = Join-Path $Root "examples\android\app\build\outputs\apk\release\app-release-adb-smoke.apk"
    & (Join-Path $Root "scripts\sign-apk-debug.ps1") -InputApk $unsigned -OutputApk $signed

    Write-Host "APK: $signed"
    & $adb logcat -c 2>$null
    & $adb install -r $signed
    & $adb shell am start -W -n $Launcher | Out-Null
    Start-Sleep -Seconds 5

    $focus = & $adb shell "dumpsys window | grep mCurrentFocus"
    if ($focus -notmatch "dev.foss.obdforge") {
        throw "App not in foreground after launch: $focus"
    }

    $log = Join-Path $env:TEMP "fdroid-dry-run-logcat.txt"
    & $adb logcat -d | Out-File -Encoding utf8 $log
    if (Select-String -Path $log -Pattern "FATAL EXCEPTION" -Quiet) {
        throw "Crash detected in logcat: $log"
    }

    Write-Host "OK   F-Droid device dry-run passed (release APK, cold start, no crash)"
    Write-Host "Logcat: $log"
} finally {
    Pop-Location
}
