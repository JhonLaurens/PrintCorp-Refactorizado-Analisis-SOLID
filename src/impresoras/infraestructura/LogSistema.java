package impresoras.infraestructura;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro centralizado de eventos del sistema.
 *
 * SRP: sólo registra y muestra logs.
 * Nota: en un sistema real se usaría SLF4J/Logback;
 *       aquí se mantiene simple por alcance del reto.
 */
public class LogSistema {

    private static final int MAX_LOGS   = 1000;
    private static final int TRIM_HASTA = 500;

    private static final List<String> logs = new ArrayList<>();

    private LogSistema() { /* utilidad estática */ }

    public static void registrar(String mensaje) {
        String entrada = LocalDateTime.now() + " - " + mensaje;
        logs.add(entrada);
        if (logs.size() > MAX_LOGS) {
            limpiar();
        }
    }

    public static void mostrar() {
        System.out.println("==== LOG SISTEMA ====");
        logs.forEach(System.out::println);
    }

    private static void limpiar() {
        List<String> recientes = new ArrayList<>(logs.subList(logs.size() - TRIM_HASTA, logs.size()));
        logs.clear();
        logs.addAll(recientes);
    }
}
