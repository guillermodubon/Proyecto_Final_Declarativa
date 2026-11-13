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
        ├── cli.pl
        ├── protocolo.pl
        ├── entrada.pl
        ├── evaluacion.pl
        ├── motor.pl
        └── modulos/
            ├── constantes/reglas.pl
            ├── factores/reglas.pl
            ├── sumas/reglas.pl
            └── trigonometricas/reglas.pl
```

## Ejecutar

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd compile exec:java
```
