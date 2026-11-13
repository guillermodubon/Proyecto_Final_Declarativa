:- begin_tests(sumas).

:- use_module('../../../src/modulos/sumas/reglas.pl', [regla/4]).

integracion_simulada(x, x^2/2).
integracion_simulada(sin(x), -cos(x)).
integracion_simulada(cos(x), sin(x)).

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(sin(x), intervalo(0, 1), integracion_simulada, _).

:- end_tests(sumas).
