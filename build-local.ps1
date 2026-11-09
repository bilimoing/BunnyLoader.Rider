param(
    [string]$RiderPath = 'D:/Rider/JetBrains Rider 2026.1.5',
    [string]$JdkPath = 'C:/Program Files/Java/jdk-21.0.12.1'
)
$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$build = Join-Path $root ('build/run-' + [guid]::NewGuid().ToString('N'))
$classes = Join-Path $build 'classes'
$lib = Join-Path $build 'stage/bunnyloader-rider/lib'
$dist = Join-Path $root 'build/distributions'
New-Item -ItemType Directory -Force $classes, $lib, $dist | Out-Null
$sources = @(Get-ChildItem (Join-Path $root 'src/main/java/dev/bunnyloader/rider') -Filter '*.java' | ForEach-Object FullName)
& "$JdkPath/bin/javac.exe" --release 21 -encoding UTF-8 -cp "$RiderPath/lib/*" -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed' }
& "$JdkPath/bin/jar.exe" --create --file "$lib/bunnyloader-rider-0.2.3.jar" -C $classes . -C "$root/src/main/resources" .
if ($LASTEXITCODE -ne 0) { throw 'Plugin JAR failed' }
$zip = Join-Path $dist 'bunnyloader-rider-0.2.3.zip'
& "$JdkPath/bin/jar.exe" --create --no-manifest --file $zip -C "$build/stage" bunnyloader-rider
if ($LASTEXITCODE -ne 0) { throw 'Plugin ZIP failed' }
Get-Item $zip | Select-Object FullName, Length
