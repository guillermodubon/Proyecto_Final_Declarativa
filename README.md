# Calculadora de integrales

Interfaz de escritorio en Java Swing conectada a un backend de integración simbólica en SWI-Prolog.

```text
Proyecto_Final_Declarativa/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
│   └── wrapper/
├── Java_Frontend/
│   └── src/main/
│       ├── java/org/example/integrales/
│       │   ├── Main.java
│       │   ├── core/
│       │   ├── ui/
│       │   └── modulos/
│       │       ├── constantes/
│       │       ├── factores/
│       │       ├── sumas/
│       │       └── trigonometricas/
│       └── resources/fonts/
└── Prolog_Backend/
    └── src/
        └── integrales.pl
```

## Literales resueltos

- **a)** Constante numérica: `k`.
- **b)** Variable: `x`.
- **c)** Constante por la variable: `k*x`.
- **d)** Potencia con exponente entero: `x^n`.
- **f)** Constante por una función: `k*u(x)`.
- **g)** Suma de funciones: `u(x) + v(x)`.
- **h)** Resta de funciones: `u(x) - v(x)`.
- **i)** Exponencial de base constante: `a^x`.
- **k)** Seno: `sin(x)`.
- **l)** Coseno: `cos(x)`.
- **r)** Coseno hiperbólico: `cosh(x)`.
- **s)** Tangente hiperbólica: `tanh(x)`.

## Ejecutar

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd compile exec:java
```
