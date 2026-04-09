# PrintCorp Refactorizado – Análisis SOLID

## Estructura del proyecto

```
src/impresoras/
├── interfaces/
│   ├── Imprimible.java        ← ISP: contrato mínimo de impresión
│   ├── Escaneable.java        ← ISP: contrato mínimo de escaneo
│   ├── FaxCapaz.java          ← ISP: contrato mínimo de fax
│   └── Mantenible.java        ← ISP: operaciones de mantenimiento físico
├── nucleo/
│   ├── Documento.java         ← SRP: sólo datos del documento
│   ├── ColaImpresion.java     ← SRP: FIFO pura, sin simulaciones
│   └── GestorImpresoras.java  ← SRP+OCP: orquesta sin acoplarse a concretas
├── modelos/
│   ├── ImpresoraBasica.java         ← ISP+LSP: Imprimible + Mantenible
│   ├── ImpresoraMultifuncional.java ← ISP+LSP: 4 interfaces, implementación real
│   └── EscanerEnLaNube.java         ← OCP: NUEVA, sin tocar nada existente
├── infraestructura/
│   └── LogSistema.java        ← SRP: sólo registra eventos
└── Main.java
```

---

## Violaciones encontradas en el código legacy

### 1. ISP – Interface Segregation Principle ❌

**Problema:** `ImpresoraService` tenía 8 métodos en una sola interfaz:
```
imprimir · escanear · enviarFax · limpiarCabezal
autodiagnostico · calibrar · actualizarFirmware · sincronizarRed
```
`ImpresoraBasica` estaba **forzada a implementar** `escanear()` y `enviarFax()` aunque no puede.

**Solución:** Se separó en 4 interfaces cohesivas:
- `Imprimible` → sólo `imprimir()`
- `Escaneable` → sólo `escanear()`
- `FaxCapaz` → sólo `enviarFax()`
- `Mantenible` → los 5 métodos de hardware local

---

### 2. LSP – Liskov Substitution Principle ❌

**Problema:** `ImpresoraBasica` lanzaba `UnsupportedOperationException` en dos métodos:
```java
public void escanear(Documento documento) {
    throw new UnsupportedOperationException("No scanner");  // LSP roto
}
```
Un cliente que usara `ImpresoraService` sin saber el tipo concreto sufriría un crash inesperado.

**Solución:** `ImpresoraBasica` ya **no implementa** `Escaneable` ni `FaxCapaz`.
Cada clase implementa únicamente lo que puede hacer de verdad.

---

### 3. SRP – Single Responsibility Principle ❌ (múltiples clases)

| Clase | Responsabilidades mezcladas |
|---|---|
| `GestorImpresoras` | Gestión de impresoras + cola duplicada (bug: usaba `cola` inexistente) + logs + reporte + simulaciones |
| `Documento` | Datos + `analizarContenido()` + `imprimirContenido()` + `calcularMetricasInutiles()` |
| `ImpresoraMultifuncional` | Impresión + escaneo + fax + logs internos + validación de número + mantenimiento |

**Soluciones aplicadas:**
- `Documento` → sólo datos e inmutabilidad (campos `final`)
- `GestorImpresoras` → sólo orquesta; log delegado a `LogSistema`
- `ImpresoraMultifuncional` → log delegado a `LogSistema`; validación de fax en método privado cohesivo

---

### 4. OCP – Open/Closed Principle ✅ (objetivo del reto)

**Agregar `EscanerEnLaNube`** sin modificar ninguna clase existente:
```java
// Sólo se implementa Escaneable — nada más es necesario
public class EscanerEnLaNube implements Escaneable {
    @Override
    public void escanear(Documento documento) { ... }
}
```
En el sistema legacy esto era **imposible** porque `ImpresoraService` obligaba a implementar 7 métodos adicionales sin sentido para un servicio en la nube.

---

### 5. DIP – Dependency Inversion Principle ❌→✅

**Legacy:** `GestorImpresoras` dependía de `List<ImpresoraService>` (clase concreta implícita).

**Refactorizado:**
```java
private final List<Imprimible> impresoras = new ArrayList<>();
private final List<Escaneable> escáneres  = new ArrayList<>();
```
Depende de **abstracciones**, no de implementaciones. Permite agregar cualquier dispositivo nuevo.

---

### 6. Dead code y simulaciones eliminadas

| Código eliminado | Motivo |
|---|---|
| `simulacion()` en 5 clases | Bucles O(n²) sin efecto observable, no simulan nada real |
| `calcularMetricasInutiles()` | Nunca llamado externamente, resultado nunca usado |
| `simulacionCarga()` en `Documento` | Dead code con condición `if(total==-1)` nunca verdadera |
| `imprimirContenido()` en `Documento` | Responsabilidad del dispositivo, no del documento |
| `analizarContenido()` en `Documento` | Responsabilidad de un analizador externo |

**Bug corregido:** `GestorImpresoras.agregarDocumento()` referenciaba `cola` que no existía como campo (usaba `colaSistema`), causando error de compilación.

---

### 7. Lógica innecesariamente compleja

**Legacy:**
```java
public int calcularPesoImpresion() {
    int peso = 0;
    for (int i = 0; i < paginas; i++) { peso += 2; }  // bucle O(n) innecesario
    if (confidencial) { peso += 5; }
    return peso;
}
```

**Refactorizado:**
```java
public int calcularPesoImpresion() {
    return paginas * 2 + (confidencial ? 5 : 0);  // O(1), mismo resultado
}
```
