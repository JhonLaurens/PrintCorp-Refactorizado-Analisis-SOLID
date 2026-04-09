package impresoras.nucleo;

import java.time.LocalDateTime;

/**
 * Entidad de dominio pura.
 *
 * SRP: sólo representa los datos de un documento.
 *   - Eliminado: analizarContenido()      → era responsabilidad de un Analizador
 *   - Eliminado: calcularMetricasInutiles() → dead code, nunca usado externamente
 *   - Eliminado: imprimirContenido()       → responsabilidad del dispositivo
 *   - Simplificado: calcularPesoImpresion() → fórmula directa, sin bucle innecesario
 */
public class Documento {

    private final String nombre;
    private final int paginas;
    private final String contenido;
    private final boolean confidencial;
    private final LocalDateTime fechaCreacion;

    public Documento(String nombre, int paginas, String contenido, boolean confidencial) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre requerido");
        if (paginas <= 0)                       throw new IllegalArgumentException("Páginas debe ser > 0");

        this.nombre        = nombre;
        this.paginas       = paginas;
        this.contenido     = contenido != null ? contenido : "";
        this.confidencial  = confidencial;
        this.fechaCreacion = LocalDateTime.now();
    }

    public String        getNombre()      { return nombre; }
    public int           getPaginas()     { return paginas; }
    public String        getContenido()   { return contenido; }
    public boolean       esConfidencial() { return confidencial; }
    public LocalDateTime getFecha()       { return fechaCreacion; }

    /** Peso de impresión: 2 unidades por página + 5 si es confidencial. */
    public int calcularPesoImpresion() {
        return paginas * 2 + (confidencial ? 5 : 0);
    }

    @Override
    public String toString() {
        return String.format("Documento[%s, %d pág., confidencial=%b]", nombre, paginas, confidencial);
    }
}
