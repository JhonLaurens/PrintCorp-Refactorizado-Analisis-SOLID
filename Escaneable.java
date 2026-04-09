package impresoras.interfaces;

import impresoras.nucleo.Documento;

/**
 * Contrato mínimo para cualquier dispositivo capaz de escanear.
 * ISP: un escáner en la nube puede implementar sólo esto
 * sin verse forzado a implementar imprimir() ni enviarFax().
 */
public interface Escaneable {
    void escanear(Documento documento);
}
