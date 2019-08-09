$ErrorActionPreference = 'Stop' # Recommended to use, so the script will stop if there is a problem
$toolsDir = Split-Path $MyInvocation.MyCommand.Definition

"@java -jar $toolsDir\konversation.jar %*" | out-file -encoding ASCII "$toolsDir\konversation.bat"
Install-BinFile -Name "konversation" -Path "$toolsDir\konversation.bat"