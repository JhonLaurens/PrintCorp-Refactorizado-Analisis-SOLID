# PrintCorp Refactorizado - Analisis SOLID

Proyecto Java refactorizado aplicando principios SOLID sobre el reto base.

## Estructura

- `src/impresoras/`: codigo fuente Java por paquetes
- `docs/`: documentacion del analisis y sustentacion
- `docs/diagramas/`: diagramas de arquitectura

## Compilar y ejecutar

```powershell
Set-Location "C:\Users\jhonjara\Downloads\files"
if (Test-Path out) { Remove-Item -Recurse -Force out }
javac -encoding UTF-8 -d out src\impresoras\*.java src\impresoras\interfaces\*.java src\impresoras\nucleo\*.java src\impresoras\modelos\*.java src\impresoras\infraestructura\*.java
java -cp out impresoras.Main
```

