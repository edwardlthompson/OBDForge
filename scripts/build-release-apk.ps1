$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root
& bash scripts/build-release-apk.sh @args
exit $LASTEXITCODE
