$ErrorActionPreference = 'Stop'

$toolsPath = Split-Path $MyInvocation.MyCommand.Definition
$binPath = "$toolsPath\..\..\..\bin"

Remove-Item "$toolsPath\konversation.jar" -ea 0
Remove-Item "$binPath\konversation.bat" -ea 0

Invoke-WebRequest -Uri https://github.com/rewe-digital-incubator/Konversation/releases/download/1.0.1-beta1/konversation-cli.jar -OutFile "$toolsPath\konversation.jar"
"@java -jar $toolsPath\konversation.jar %1 %2 %3 %4 %5 %6 %7 %8 %9" | out-file -encoding ASCII "$binPath\konversation.bat"