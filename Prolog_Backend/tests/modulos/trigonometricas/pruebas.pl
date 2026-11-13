:- begin_tests(trigonometricas).

:- use_module('../../../src/modulos/trigonometricas/reglas.pl', [regla/4]).

sin_regla(_, _) :- fail.

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(x^2, intervalo(0, 1), sin_regla, _).

:- end_tests(trigonometricas).
