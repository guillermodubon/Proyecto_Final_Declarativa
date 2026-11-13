:- module(integracion_cli, [main/0]).

:- use_module('../src/evaluacion.pl').
:- use_module('../src/protocolo.pl').

main :-
    current_prolog_flag(argv, [Nombre]),
    modulo(Nombre, Modulo),
    source_file(integracion_cli:main, Archivo),
    file_directory_name(Archivo, Directorio),
    format(atom(Relativa), '../src/modulos/~w/reglas.pl', [Nombre]),
    directory_file_path(Directorio, Relativa, Ruta),
    use_module(Ruta, []),
    ejecutar(resolver_modulo(Modulo)).

modulo(constantes, reglas_constantes).
modulo(factores, reglas_factores).
modulo(sumas, reglas_sumas).
modulo(trigonometricas, reglas_trigonometricas).

resolver_modulo(Modulo, Expresion, A, B, Resultado) :-
    resolver_con(integrar(Modulo), Expresion, A, B, Resultado).

integrar(Modulo, Intervalo, Expresion, Primitiva) :-
    Modulo:regla(Expresion, Intervalo, integracion_cli:integrar_parte(Modulo, Intervalo), Primitiva).

integrar_parte(Modulo, Intervalo, Expresion, Primitiva) :-
    ( integrar(Modulo, Intervalo, Expresion, Primitiva) -> true
    ; primitiva_simulada(Expresion, Primitiva)
    ).

primitiva_simulada(x, x^2/2).
primitiva_simulada(x^2, x^3/3).
primitiva_simulada(sin(x), -cos(x)).
primitiva_simulada(cos(x), sin(x)).
