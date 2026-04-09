package impresoras.nucleo;

import impresoras.interfaces.Escaneable;
import impresoras.interfaces.Imprimible;
import impresoras.infraestructura.LogSistema;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta el procesamiento de documentos sobre dispositivos registrados.
 *
 * SRP: sólo coordina qué dispositivo procesa qué documento.
 *   - Eliminada la cola duplicada (sólo existe ColaImpresion).
 *   - Eliminados los bucles de simulación.
 *   - Generación de reporte delegable a LogSistema si se desea.
 *
 * OCP: acepta cualquier Imprimible o Escaneable gracias a las interfaces ISP;
 *      agregar un nuevo dispositivo no requiere cambiar esta clase.
 */
public class GestorImpresoras {

    private final List<Imprimible> impresoras  = new ArrayList<>();
    private final List<Escaneable> escáneres   = new ArrayList<>();
    private final ColaImpresion    cola         = new ColaImpresion();

    // ── Registro de dispositivos ──────────────────────────────────────────────

    public void registrarImpresora(Imprimible impresora) {
        impresoras.add(impresora);
        System.out.println("[Gestor] Impresora registrada: " + impresora.getClass().getSimpleName());
    }

    public void registrarEscaner(Escaneable escaner) {
        escáneres.add(escaner);
        System.out.println("[Gestor] Escáner registrado: " + escaner.getClass().getSimpleName());
    }

    // ── Gestión de documentos ─────────────────────────────────────────────────

    public void agregarDocumento(Documento d) {
        cola.agregar(d);
        LogSistema.registrar("Documento encolado: " + d.getNombre());
    }

    // ── Procesamiento ─────────────────────────────────────────────────────────

    /** Envía cada documento de la cola a TODAS las impresoras registradas. */
    public void procesarImpresion() {
        if (impresoras.isEmpty()) {
            System.out.println("[Gestor] No hay impresoras registradas.");
            return;
        }
        while (!cola.estaVacia()) {
            Documento doc = cola.siguiente();
            for (Imprimible impresora : impresoras) {
                try {
                    impresora.imprimir(doc);
                } catch (Exception e) {
                    System.out.println("[Gestor] Error imprimiendo en dispositivo: " + e.getMessage());
                }
            }
        }
    }

    /** Escanea un documento puntual en todos los escáneres registrados. */
    public void escanearDocumento(Documento doc) {
        if (escáneres.isEmpty()) {
            System.out.println("[Gestor] No hay escáneres registrados.");
            return;
        }
        for (Escaneable escaner : escáneres) {
            try {
                escaner.escanear(doc);
            } catch (Exception e) {
                System.out.println("[Gestor] Error escaneando: " + e.getMessage());
            }
        }
    }

    // ── Reporte ───────────────────────────────────────────────────────────────

    public void generarReporte() {
        System.out.println("==== REPORTE DEL SISTEMA ====");
        System.out.println("Impresoras registradas : " + impresoras.size());
        System.out.println("Escáneres registrados  : " + escáneres.size());
        System.out.println("Documentos en cola     : " + cola.size());
        LogSistema.mostrar();
    }
}
