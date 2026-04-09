package impresoras;

import impresoras.modelos.EscanerEnLaNube;
import impresoras.modelos.ImpresoraBasica;
import impresoras.modelos.ImpresoraMultifuncional;
import impresoras.nucleo.Documento;
import impresoras.nucleo.GestorImpresoras;

public class Main {

    public static void main(String[] args) {

        GestorImpresoras gestor = new GestorImpresoras();

        // ── Registro de dispositivos ──────────────────────────────────────────
        ImpresoraMultifuncional hp     = new ImpresoraMultifuncional("HP OfficeJet");
        ImpresoraBasica         canon  = new ImpresoraBasica("Canon Basic");
        EscanerEnLaNube         nube   = new EscanerEnLaNube("s3://printcorp-docs");

        gestor.registrarImpresora(hp);
        gestor.registrarImpresora(canon);
        gestor.registrarEscaner(hp);      // HP también escanea
        gestor.registrarEscaner(nube);    // Nuevo: escáner en la nube, sin tocar nada existente

        // ── Documentos ───────────────────────────────────────────────────────
        Documento reporte  = new Documento("Reporte Q1", 10, "Contenido del reporte...", false);
        Documento contrato = new Documento("Contrato NDA", 5,  "Texto legal confidencial.", true);

        gestor.agregarDocumento(reporte);
        gestor.agregarDocumento(contrato);

        // ── Procesamiento ─────────────────────────────────────────────────────
        System.out.println("\n--- Procesando impresión ---");
        gestor.procesarImpresion();

        System.out.println("\n--- Escaneando documento suelto ---");
        gestor.escanearDocumento(reporte);

        System.out.println("\n--- Enviando fax (sólo HP puede) ---");
        hp.enviarFax("3001234567", contrato);

        // ── Reporte final ─────────────────────────────────────────────────────
        System.out.println();
        gestor.generarReporte();
    }
}
