package org.example.integrales.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public final class ClienteProlog implements ServicioIntegracion {
    private static final String CONSULTA = """
            catch(
                (read_term(user_input, solicitud(E,A,B), []), integrales:resolver(E,A,B,R)),
                _, R=error('ERROR_INTERNO')),
            ( R=ok(Primitiva,Valor,Fa,Fb) ->
                term_string(Primitiva, Texto, [quoted(true)]),
                format('OK~n~s~n~w~n~w~n~w~n', [Texto,Valor,Fa,Fb])
            ; R=error(Codigo), format('ERROR~n~w~n', [Codigo])
            )
            """.replace('\n', ' ');

    private final Path cli;

    public ClienteProlog(Path cli) {
        this.cli = cli.toAbsolutePath().normalize();
    }

    @Override
    public Resultado resolver(Solicitud solicitud) throws Exception {
        List<String> campos = List.of(solicitud.expresion(), solicitud.a(), solicitud.b());
        if (campos.stream().anyMatch(s -> s.isBlank() || s.length() > 512
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0)) {
            return new Error("ENTRADA_INVALIDA", "Ingresá la expresión y ambos límites (máximo 512 caracteres por campo).");
        }
        String expresion;
        try {
            expresion = EntradaIntegracion.expresion(solicitud.expresion());
        } catch (IllegalArgumentException e) {
            return interpretarError("EXPRESION_INVALIDA");
        }
        EntradaIntegracion.Numero a;
        EntradaIntegracion.Numero b;
        try {
            a = EntradaIntegracion.limite(solicitud.a());
            b = EntradaIntegracion.limite(solicitud.b());
        } catch (IllegalArgumentException e) {
            return interpretarError("LIMITES_INVALIDOS");
        }
        if (a.compareTo(b) > 0) return interpretarError("INTERVALO_INVALIDO");

        if (!Files.isRegularFile(cli)) throw new IOException("No se encontró el archivo de inicio de Prolog.");
        List<String> comando = List.of(System.getenv().getOrDefault("SWIPL", "swipl"),
                "-q", "-f", "none", "-s", cli.toString(), "-g", CONSULTA, "-t", "halt");
        Process proceso = new ProcessBuilder(comando)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        try {
            var lectura = CompletableFuture.supplyAsync(() -> leerSalida(proceso));
            try (var entrada = proceso.outputWriter(StandardCharsets.UTF_8)) {
                entrada.write("solicitud(" + expresion + "," + a.termino() + "," + b.termino() + ").");
                entrada.newLine();
            }
            if (!proceso.waitFor(10, TimeUnit.SECONDS)) throw new IOException("SWI-Prolog tardó demasiado en responder.");
            List<String> salida = lectura.get(1, TimeUnit.SECONDS);
            if (proceso.exitValue() != 0 || salida.isEmpty()) {
                throw new IOException("Respuesta inválida de SWI-Prolog.");
            }
            if (salida.get(0).equals("OK") && salida.size() == 5) {
                return new Exito(salida.get(1), Double.parseDouble(salida.get(2)),
                        Double.parseDouble(salida.get(3)), Double.parseDouble(salida.get(4)));
            }
            if (salida.get(0).equals("ERROR") && salida.size() == 2) {
                return interpretarError(salida.get(1));
            }
            throw new IOException("Respuesta desconocida de SWI-Prolog.");
        } finally {
            if (proceso.isAlive()) proceso.destroyForcibly();
        }
    }

    private static Error interpretarError(String codigo) throws IOException {
        return switch (codigo) {
            case "EXPRESION_INVALIDA" -> new Error(codigo, "Usá x, números, operadores y funciones admitidas.");
            case "LIMITES_INVALIDOS" -> new Error(codigo, "Los límites deben ser números finitos.");
            case "INTERVALO_INVALIDO" -> new Error(codigo, "El límite inferior debe ser menor o igual al superior.");
            case "NO_SOPORTADA" -> new Error(codigo, "Todavía no hay una regla para esa expresión.");
            case "DOMINIO" -> new Error(codigo, "La primitiva no se puede evaluar en los límites indicados.");
            case "POTENCIA_NEGATIVA_EN_CERO" -> new Error("DOMINIO",
                    "Con exponente negativo el intervalo no puede contener a x = 0 (la integral diverge).");
            case "ERROR_INTERNO" -> new Error(codigo, "No se pudo completar el cálculo en SWI-Prolog.");
            default -> throw new IOException("Respuesta desconocida de SWI-Prolog.");
        };
    }

    private static List<String> leerSalida(Process proceso) {
        try (var salida = proceso.inputReader(StandardCharsets.UTF_8)) {
            return salida.lines().toList();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }
}
