$ErrorActionPreference = 'Stop'

$toolsPath = Split-Path $MyInvocation.MyCommand.Definition
$binPath = "$toolsPath\..\..\..\bin"

Remove-Item "$toolsPath\konversation.jar" -ea 0
Remove-Item "$binPath\konversation.bat" -ea 0