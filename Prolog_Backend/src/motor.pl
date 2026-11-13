:- module(motor_integracion, [resolver/4, resolver_con/5]).

:- reexport('evaluacion.pl', [resolver_con/5]).
:- use_module('modulos/constantes/reglas.pl', []).
:- use_module('modulos/factores/reglas.pl', []).
:- use_module('modulos/sumas/reglas.pl', []).
:- use_module('modulos/trigonometricas/reglas.pl', []).

resolver(Texto, Inferior, Superior, Resultado) :-
    resolver_con(integrar, Texto, Inferior, Superior, Resultado).

integrar(Intervalo, Expresion, Primitiva) :-
    modulo(Modulo),
    call(Modulo:regla(Expresion, Intervalo, motor_integracion:integrar(Intervalo), Primitiva)), !.

modulo(reglas_constantes).
modulo(reglas_factores).
modulo(reglas_sumas).
modulo(reglas_trigonometricas).
