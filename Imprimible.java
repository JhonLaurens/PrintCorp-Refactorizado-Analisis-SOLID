package impresoras.interfaces;

import impresoras.nucleo.Documento;

/**
 * Contrato mínimo para cualquier dispositivo capaz de imprimir.
 * ISP: separada del resto de capacidades del dispositivo.
 */
public interface Imprimible {
    void imprimir(Documento documento);
}
