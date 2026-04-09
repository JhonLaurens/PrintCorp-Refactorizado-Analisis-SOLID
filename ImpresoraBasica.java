package impresoras.modelos;

import impresoras.interfaces.Imprimible;
import impresoras.interfaces.Mantenible;
import impresoras.nucleo.Documento;

/**
 * Impresora básica: sólo imprime y admite mantenimiento físico.
 *
 * ISP: implementa ÚNICAMENTE las interfaces que puede cumplir.
 *      Ya no está forzada a tener escanear() ni enviarFax().
 *
 * LSP: cada método implementado tiene comportamiento real y útil.
 *      No hay UnsupportedOperationException escondidos.
 *
 * SRP: eliminadas las simulaciones de carga (bucles vacíos).
 */
public class ImpresoraBasica implements Imprimible, Mantenible {

    private final String modelo;
    private int impresiones;

    public ImpresoraBasica(String modelo) {
        this.modelo = modelo;
    }

    // ── Imprimible ────────────────────────────────────────────────────────────

    @Override
    public void imprimir(Documento documento) {
        System.out.println("[" + modelo + "] Imprimiendo: " + documento.getNombre());
        for (int p = 1; p <= documento.getPaginas(); p++) {
            System.out.println("  Página " + p + "/" + documento.getPaginas());
        }
        impresiones++;
        System.out.println("[" + modelo + "] Impresión completa. Total acumulado: " + impresiones);
    }

    // ── Mantenible ────────────────────────────────────────────────────────────

    @Override
    public void limpiarCabezal() {
        System.out.println("[" + modelo + "] Limpiando cabezal.");
    }

    @Override
    public void autodiagnostico() {
        System.out.println("[" + modelo + "] Autodiagnóstico OK.");
    }

    @Override
    public void calibrar() {
        System.out.println("[" + modelo + "] Calibración completa.");
    }

    @Override
    public void actualizarFirmware() {
        System.out.println("[" + modelo + "] Firmware actualizado.");
    }

    @Override
    public void sincronizarRed() {
        System.out.println("[" + modelo + "] Red sincronizada.");
    }
}
