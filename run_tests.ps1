$projectRoot = $PSScriptRoot
$backendDir = Join-Path $projectRoot "backend"
$mobileDir = Join-Path $projectRoot "mobile"

Write-Host "`n========== [1/4] BACKEND: pruebas Maven (mvn.cmd test) ==========" -ForegroundColor Cyan
Set-Location $backendDir
mvn.cmd test
if ($LASTEXITCODE -ne 0) {
    Set-Location $projectRoot
    exit $LASTEXITCODE
}

Write-Host "`n========== [2/4] ANDROID: pruebas unitarias (gradlew testDebugUnitTest) ==========" -ForegroundColor Cyan
Set-Location $mobileDir
.\gradlew testDebugUnitTest

if ($LASTEXITCODE -ne 0) {
    Set-Location $projectRoot
    exit $LASTEXITCODE
}

Write-Host "`n========== [3/4] ANDROID: pruebas instrumentadas ==========" -ForegroundColor Cyan
$sdkRoot = $env:ANDROID_SDK_ROOT
if ([string]::IsNullOrWhiteSpace($sdkRoot)) {
    $sdkRoot = $env:ANDROID_HOME
}
if ([string]::IsNullOrWhiteSpace($sdkRoot) -and $env:LOCALAPPDATA) {
    $sdkRoot = Join-Path $env:LOCALAPPDATA "Android\Sdk"
}
if ([string]::IsNullOrWhiteSpace($sdkRoot)) {
    throw "No se encontró Android SDK. Define ANDROID_SDK_ROOT o ANDROID_HOME."
}

$adb = Join-Path $sdkRoot "platform-tools\adb.exe"
$emulator = Join-Path $sdkRoot "emulator\emulator.exe"
if (-not (Test-Path $adb) -or -not (Test-Path $emulator)) {
    throw "No se encontraron adb y emulator dentro de Android SDK: $sdkRoot"
}

$availableAvds = @(& $emulator -list-avds)
$avdName = $env:ANDROID_AVD_NAME
if ([string]::IsNullOrWhiteSpace($avdName)) {
    if ($availableAvds -contains "Pixel_7") {
        $avdName = "Pixel_7"
    } elseif ($availableAvds.Count -gt 0) {
        $avdName = $availableAvds[0].Trim()
    } else {
        throw "No hay AVDs instalados. Crea uno desde Android Studio o define ANDROID_AVD_NAME."
    }
}
if ($availableAvds -notcontains $avdName) {
    throw "No existe el AVD '$avdName'. AVDs disponibles: $($availableAvds -join ', ')"
}

$androidTestExitCode = -1
$emulatorProcess = $null
$emulatorSerial = $null
try {
    Write-Host "Iniciando el emulador Android Studio '$avdName'..." -ForegroundColor Yellow
    $emulatorProcess = Start-Process -FilePath $emulator -ArgumentList @("-avd", $avdName) -PassThru
    $bootDeadline = (Get-Date).AddMinutes(5)
    $bootCompleted = $false

    while ((Get-Date) -lt $bootDeadline) {
        if ($emulatorProcess.HasExited) {
            throw "El proceso del emulador '$avdName' terminó antes de arrancar."
        }

        $device = & $adb devices | Select-String '^\s*(emulator-\d+)\s+device\b' | Select-Object -First 1
        if ($device) {
            $emulatorSerial = $device.Matches[0].Groups[1].Value
            $bootState = (& $adb -s $emulatorSerial shell getprop sys.boot_completed 2>$null | Out-String).Trim()
            if ($bootState -eq "1") {
                $bootCompleted = $true
                break
            }
        }
        Start-Sleep -Seconds 2
    }

    if (-not $bootCompleted) {
        throw "El emulador '$avdName' no terminó de iniciar en 5 minutos."
    }

    Write-Host "Emulador listo. Ejecutando pruebas instrumentadas..." -ForegroundColor DarkCyan
    .\gradlew connectedDebugAndroidTest
    $androidTestExitCode = $LASTEXITCODE
} finally {
    if ($emulatorProcess) {
        Write-Host "Cerrando el emulador iniciado por este script..." -ForegroundColor DarkCyan
        if ($emulatorSerial) {
            & $adb -s $emulatorSerial emu kill | Out-Null
        }
        Wait-Process -Id $emulatorProcess.Id -Timeout 15 -ErrorAction SilentlyContinue
        if (-not $emulatorProcess.HasExited) {
            Stop-Process -Id $emulatorProcess.Id -Force
        }
    }
}

if ($androidTestExitCode -ne 0) {
    Set-Location $projectRoot
    exit $androidTestExitCode
}

Write-Host "`n========== [4/4] MOBILE: analisis Detekt (gradlew detekt --info) ==========" -ForegroundColor Cyan
.\gradlew detekt --info

if ($LASTEXITCODE -ne 0) {
    Set-Location $projectRoot
    exit $LASTEXITCODE
}

Set-Location $projectRoot