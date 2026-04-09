package impresoras.interfaces;

/**
 * Operaciones de mantenimiento de hardware local.
 * ISP: separada de la lógica de impresión/escaneo.
 * Un EscanerEnLaNube no necesita limpiar cabezales físicos.
 */
public interface Mantenible {
    void limpiarCabezal();
    void autodiagnostico();
    void calibrar();
    void actualizarFirmware();
    void sincronizarRed();
}
