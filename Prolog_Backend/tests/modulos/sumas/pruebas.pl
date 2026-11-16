:- encoding(utf8).
:- begin_tests(sumas).

:- use_module('../../../src/modulos/sumas/reglas.pl', [regla/4]).
:- use_module('../../../src/motor.pl', [resolver/4]).

integracion_simulada(x, x^2/2).
integracion_simulada(x^2, x^3/3).
integracion_simulada(sin(x), -cos(x)).
integracion_simulada(cos(x), sin(x)).

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(sin(x), intervalo(0, 1), integracion_simulada, _).


% g) La suma usa las primitivas de ambos terminos.
test(g_suma_con_funcion_simulada) :-
    regla(x+sin(x), intervalo(0, 1), integracion_simulada, F),
    assertion(F == x^2/2 + (-cos(x))).

test(g_suma_integral_definida) :-
    resolver("x+x^2", "0", "2", ok(F, V, Fa, Fb)),
    assertion(F == x^2/2 + x^3/3),
    assertion(abs(V - 14/3) < 1.0e-12),
    assertion(Fa =:= 0),
    assertion(abs(Fb - 14/3) < 1.0e-12).

% h) La resta conserva el orden y los signos.
test(h_resta_con_funcion_simulada) :-
    regla(x^2-cos(x), intervalo(0, 1), integracion_simulada, F),
    assertion(F == x^3/3 - sin(x)).

test(h_resta_integral_definida) :-
    resolver("x^2-x", "1", "2", ok(F, V, _, _)),
    assertion(F == x^3/3 - x^2/2),
    assertion(abs(V - 5/6) < 1.0e-12).

% s) La primitiva de tanh(x) es log(cosh(x)).
test(s_primitiva) :-
    regla(tanh(x), intervalo(0, 1), integracion_simulada, F),
    assertion(F == log(cosh(x))).

test(s_integral_definida) :-
    resolver("tanh(x)", "0", "1", ok(F, V, Fa, Fb)),
    assertion(F == log(cosh(x))),
    assertion(abs(V - log(cosh(1))) < 1.0e-12),
    assertion(abs(Fa) < 1.0e-12),
    assertion(abs(Fb - log(cosh(1))) < 1.0e-12).

% Los operadores deben poder combinarse y anidarse.
test(g_h_anidadas) :-
    resolver("(x+x^2)-x", "0", "2", ok(_, V, _, _)),
    assertion(abs(V - 8/3) < 1.0e-12).

test(s_combinada_con_resta) :-
    resolver("tanh(x)-x", "0", "1", ok(_, V, _, _)),
    assertion(abs(V - (log(cosh(1)) - 0.5)) < 1.0e-12).

test(g_con_termino_no_soportado) :-
    resolver("x+x^x", "0", "1", error('NO_SOPORTADA', _)).

:- end_tests(sumas).
