:- begin_tests(constantes).

:- use_module('../../../src/modulos/constantes/reglas.pl', [regla/4]).

sin_regla(_, _) :- fail.

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(sin(x), intervalo(0, 1), sin_regla, _).

:- end_tests(constantes).
