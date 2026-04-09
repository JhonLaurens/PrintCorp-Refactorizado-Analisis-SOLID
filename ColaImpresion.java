package impresoras.nucleo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Cola FIFO de documentos pendientes de impresión.
 *
 * SRP: sólo gestiona el orden de los documentos.
 *   - Eliminados los bucles de "simulación" que no aportaban nada.
 *   - Umbral de advertencia extraído como constante nombrada.
 */
public class ColaImpresion {

    private static final int UMBRAL_ADVERTENCIA = 50;

    private final Deque<Documento> cola = new ArrayDeque<>();

    public void agregar(Documento d) {
        if (d == null) {
            System.out.println("[Cola] Intento de agregar documento null ignorado.");
            return;
        }
        cola.addLast(d);
        System.out.println("[Cola] Documento agregado: " + d.getNombre());

        if (cola.size() > UMBRAL_ADVERTENCIA) {
            System.out.println("[Cola] Advertencia: cola grande (" + cola.size() + " documentos).");
        }
    }

    public Documento siguiente() {
        return cola.pollFirst();   // devuelve null si vacía, sin excepción
    }

    public boolean estaVacia() { return cola.isEmpty(); }
    public int     size()      { return cola.size(); }

    public void mostrar() {
        System.out.println("[Cola] Documentos pendientes:");
        cola.forEach(d -> System.out.println("  - " + d.getNombre()));
    }
}
