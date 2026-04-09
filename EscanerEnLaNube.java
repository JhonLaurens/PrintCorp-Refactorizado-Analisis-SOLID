package impresoras.modelos;

import impresoras.interfaces.Escaneable;
import impresoras.nucleo.Documento;
import impresoras.infraestructura.LogSistema;

/**
 * Escáner que sube el documento directamente a la nube.
 *
 * OCP: clase completamente nueva, cero modificaciones al código existente.
 *
 * ISP: implementa SÓLO Escaneable, porque un servicio en la nube
 *      no tiene cabezal físico, no imprime en papel ni envía fax.
 *      En el legacy esto habría sido imposible sin tirar UnsupportedOperationException
 *      en 7 de los 8 métodos de ImpresoraService.
 *
 * LSP: puede usarse en cualquier lugar donde se espere un Escaneable
 *      sin ninguna sorpresa para el cliente.
 */
public class EscanerEnLaNube implements Escaneable {

    private final String bucketDestino;
    private int escaneos = 0;

    public EscanerEnLaNube(String bucketDestino) {
        this.bucketDestino = bucketDestino;
    }

    @Override
    public void escanear(Documento documento) {
        System.out.println("[EscanerNube] Subiendo '" + documento.getNombre()
                + "' → " + bucketDestino);

        // Aquí iría la llamada real al SDK de almacenamiento en la nube.
        escaneos++;

        LogSistema.registrar("[EscanerNube] Documento subido: " + documento.getNombre()
                + " | Destino: " + bucketDestino);

        System.out.println("[EscanerNube] Subida completada. Total subidas: " + escaneos);
    }
}
