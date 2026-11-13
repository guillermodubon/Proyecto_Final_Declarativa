:- begin_tests(factores).

:- use_module('../../../src/modulos/factores/reglas.pl', [regla/4]).

integracion_simulada(cos(x), sin(x)).

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(cos(x), intervalo(0, 1), integracion_simulada, _).

:- end_tests(factores).
