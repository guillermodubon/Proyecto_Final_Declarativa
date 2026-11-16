package org.example.integrales.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.regex.Pattern;

final class EntradaIntegracion {
    private static final Set<String> FUNCIONES = Set.of("sin", "cos", "cosh", "tanh");
    private static final String DIGITOS = "[0-9]+(?:[ _][0-9]+)*";
    private static final String EXPONENTE = "[eE][+-]?[0-9]+";
    private static final Pattern NUMERO = Pattern.compile(
            "0x[0-9a-fA-F]+|0o[0-7]+|0b[01]+|" + DIGITOS
                    + "(?:r[0-9]+|\\.[0-9]+(?:" + EXPONENTE + ")?|" + EXPONENTE + ")?");

    private final String texto;
    private int posicion;

    private EntradaIntegracion(String texto) {
        this.texto = texto;
    }

    static String expresion(String texto) {
        if (texto.indexOf('_') >= 0) throw invalida();
        var lector = new EntradaIntegracion(texto);
        String termino = lector.suma();
        lector.omitirEspacios();
        if (lector.actual() != '\0' || lector.posicion != texto.length()) throw invalida();
        return termino;
    }

    static Numero limite(String campo) {
        String texto = campo.replaceAll("(?U)\\s+", " ").trim();
        boolean negativo = texto.startsWith("-");
        if (negativo || texto.startsWith("+")) texto = texto.substring(1);
        if (!NUMERO.matcher(texto).matches()) throw invalida();
        return convertirNumero(texto, negativo);
    }

    private String suma() {
        String izquierda = producto();
        for (char op; (op = tomarOperador("+-")) != '\0'; ) {
            izquierda = binario(op, izquierda, producto());
        }
        return izquierda;
    }

    private String producto() {
        String izquierda = potencia();
        for (char op; (op = tomarOperador("*/")) != '\0'; ) {
            izquierda = binario(op, izquierda, potencia());
        }
        return izquierda;
    }

    private String potencia() {
        char signo = tomarOperador("+-");
        String base;
        if (signo == '\0') {
            base = primaria();
        } else if (signo == '-' && esDigito(actual())) {
            base = numero(true);
        } else if (actual() == '(') {
            base = "'" + signo + "'(" + parentesis() + ")";
        } else {
            return "'" + signo + "'(" + potencia() + ")";
        }
        return tomarOperador("^") == '^' ? binario('^', base, potencia()) : base;
    }

    private String primaria() {
        if (actual() == '(') return parentesis();
        if (esDigito(actual())) return numero(false);

        int inicio = posicion;
        while (Character.isLetter(actual())) posicion++;
        String nombre = texto.substring(inicio, posicion);
        if (nombre.equals("x")) return nombre;
        if (FUNCIONES.contains(nombre) && actual() == '(') {
            return nombre + "(" + parentesis() + ")";
        }
        throw invalida();
    }

    private String parentesis() {
        posicion++;
        String termino = suma();
        if (actual() != ')') throw invalida();
        posicion++;
        return termino;
    }

    private String numero(boolean negativo) {
        var token = NUMERO.matcher(texto).region(posicion, texto.length());
        if (!token.lookingAt()) throw invalida();
        posicion = token.end();
        return convertirNumero(token.group(), negativo).termino();
    }

    private char tomarOperador(String opciones) {
        omitirEspacios();
        char op = actual();
        if (opciones.indexOf(op) < 0) return '\0';
        posicion++;
        if ("+-*/^.".indexOf(actual()) >= 0) throw invalida();
        return op;
    }

    private void omitirEspacios() {
        while (Character.isWhitespace(actual()) || Character.isSpaceChar(actual())) posicion++;
    }

    private char actual() {
        return posicion < texto.length() ? texto.charAt(posicion) : '\0';
    }

    private static boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private static String binario(char op, String izquierda, String derecha) {
        return "'" + op + "'(" + izquierda + "," + derecha + ")";
    }

    private static Numero convertirNumero(String texto, boolean negativo) {
        String limpio = texto.replace(" ", "").replace("_", "");
        if (negativo) limpio = "-" + limpio;
        String magnitud = negativo ? limpio.substring(1) : limpio;

        int radix = magnitud.startsWith("0x") ? 16 : magnitud.startsWith("0o") ? 8 : magnitud.startsWith("0b") ? 2 : 10;
        if (radix != 10) {
            BigInteger entero = new BigInteger(magnitud.substring(2), radix);
            return Numero.entero(negativo ? entero.negate() : entero);
        }
        int separador = limpio.indexOf('r');
        if (separador >= 0) {
            var numerador = new BigInteger(limpio.substring(0, separador));
            var denominador = new BigInteger(limpio.substring(separador + 1));
            if (denominador.signum() == 0) throw invalida();
            return new Numero(numerador + "r" + denominador, numerador, denominador);
        }
        if (limpio.indexOf('.') >= 0 || limpio.indexOf('e') >= 0 || limpio.indexOf('E') >= 0) {
            double decimal = Double.parseDouble(limpio);
            if (!Double.isFinite(decimal)) throw invalida();
            var exacto = new BigDecimal(decimal);
            return new Numero(Double.toString(decimal), exacto.unscaledValue(), BigInteger.TEN.pow(exacto.scale()));
        }
        return Numero.entero(new BigInteger(limpio));
    }

    private static IllegalArgumentException invalida() {
        return new IllegalArgumentException();
    }

    record Numero(String termino, BigInteger numerador, BigInteger denominador) implements Comparable<Numero> {
        private static Numero entero(BigInteger valor) {
            return new Numero(valor.toString(), valor, BigInteger.ONE);
        }

        @Override
        public int compareTo(Numero otro) {
            return numerador.multiply(otro.denominador).compareTo(otro.numerador.multiply(denominador));
        }
    }
}
