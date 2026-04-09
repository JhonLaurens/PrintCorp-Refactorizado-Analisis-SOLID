package impresoras.interfaces;

import impresoras.nucleo.Documento;

/**
 * Contrato mínimo para dispositivos que pueden enviar fax.
 * ISP: sólo las impresoras multifuncionales lo implementan.
 */
public interface FaxCapaz {
    void enviarFax(String numero, Documento documento);
}
