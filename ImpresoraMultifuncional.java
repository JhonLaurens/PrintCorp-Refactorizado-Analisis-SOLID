package impresoras.modelos;

import impresoras.interfaces.Escaneable;
import impresoras.interfaces.FaxCapaz;
import impresoras.interfaces.Imprimible;
import impresoras.interfaces.Mantenible;
import impresoras.infraestructura.LogSistema;
import impresoras.nucleo.Documento;

/**
 * Impresora multifuncional: imprime, escanea, envía fax y admite mantenimiento.
 *
 * ISP: implementa exactamente las cuatro interfaces que corresponden
 *      a sus capacidades reales.
 *
 * LSP: todos los métodos tienen implementación real.
 *      Puede sustituir a cualquier Imprimible, Escaneable o FaxCapaz
 *      sin sorpresas para el cliente.
 *
 * SRP: la lógica de log delegada a LogSistema (ya no lleva lista interna).
 *      La validación de fax encapsulada en método privado cohesivo.
 *      Eliminadas las simulaciones de carga (bucles vacíos).
 */
public class ImpresoraMultifuncional implements Imprimible, Escaneable, FaxCapaz, Mantenible {

    private static final int UMBRAL_TINTA = 10;

    private final String modelo;
    private int tinta = 100;
    private int impresiones = 0;

    public ImpresoraMultifuncional(String modelo) {
        this.modelo = modelo;
    }

    // ── Imprimible ────────────────────────────────────────────────────────────

    @Override
    public void imprimir(Documento documento) {
        LogSistema.registrar("[" + modelo + "] Inicio impresión: " + documento.getNombre());

        if (tinta < UMBRAL_TINTA) {
            System.out.println("[" + modelo + "] Advertencia: tinta baja (" + tinta + "%)");
        }

        if (documento.esConfidencial()) {
            LogSistema.registrar("[" + modelo + "] Documento confidencial en cola.");
        }

        for (int p = 1; p <= documento.getPaginas(); p++) {
            System.out.println("[" + modelo + "] Imprimiendo página " + p + "/" + documento.getPaginas());
        }

        tinta = Math.max(0, tinta - documento.getPaginas());
        impresiones++;

        LogSistema.registrar("[" + modelo + "] Impresión completada: " + documento.getNombre());
        System.out.println("[" + modelo + "] Documento impreso: " + documento.getNombre()
                + " | Tinta restante: " + tinta + "%");
    }

    // ── Escaneable ────────────────────────────────────────────────────────────

    @Override
    public void escanear(Documento documento) {
        System.out.println("[" + modelo + "] Escaneando: " + documento.getNombre()
                + " (" + documento.getPaginas() + " pág.)");
        LogSistema.registrar("[" + modelo + "] Escaneo completado: " + documento.getNombre());
    }

    // ── FaxCapaz ──────────────────────────────────────────────────────────────

    @Override
    public void enviarFax(String numero, Documento documento) {
        if (!esNumeroValido(numero)) {
            System.out.println("[" + modelo + "] Número de fax inválido: " + numero);
            return;
        }
        System.out.println("[" + modelo + "] Enviando fax a " + numero
                + ": " + documento.getNombre());
        LogSistema.registrar("[" + modelo + "] Fax enviado a " + numero);
    }

    private boolean esNumeroValido(String numero) {
        return numero != null && numero.length() >= 5;
    }

    // ── Mantenible ────────────────────────────────────────────────────────────

    @Override
    public void limpiarCabezal() {
        System.out.println("[" + modelo + "] Limpiando cabezal.");
    }

    @Override
    public void autodiagnostico() {
        System.out.println("[" + modelo + "] Autodiagnóstico completo.");
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
