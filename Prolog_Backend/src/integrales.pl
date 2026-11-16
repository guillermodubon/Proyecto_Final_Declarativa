% Integrantes del proyecto
% Guillermo Alejandro Hernández Dubón — 00106423
% Marden Eliuth Larios Orellana — 00029223
% Gabriel Alejandro Batres Flores — 00103923
% Joel Mauricio Barrera Zepeda — 00014220

:- encoding(utf8).
:- module(integrales, [resolver/4]).

resolver(Expresion, A, B, Resultado) :-
    catch(( ( once(integrar(intervalo(A, B), Expresion, Primitiva)) -> true
            ; error_integracion('NO_SOPORTADA')
            ),
            evaluar_intervalo(Primitiva, A, B, Valor, Fa, Fb),
            Resultado = ok(Primitiva, Valor, Fa, Fb)
          ), error(integracion(Codigo), _), Resultado = error(Codigo)).

error_integracion(Codigo) :- throw(error(integracion(Codigo), _)).

evaluar_intervalo(Primitiva, A, B, Valor, Fa, Fb) :-
    sustituir(Primitiva, A, EnA), sustituir(Primitiva, B, EnB),
    ( catch(( VA is EnA, VB is EnB,
              Valor is float(VB - VA), Fa is float(VA), Fb is float(VB)
            ), error(evaluation_error(_), _), fail),
      maplist(finito, [Valor, Fa, Fb]) -> true
    ; error_integracion('DOMINIO')
    ).

finito(N) :- float_class(N, Clase), memberchk(Clase, [zero, subnormal, normal]).

sustituir(x, Valor, Valor) :- !.
sustituir(Termino, X, Resultado) :-
    compound(Termino), !,
    compound_name_arguments(Termino, Nombre, Args),
    maplist(sustituir_en(X), Args, Nuevos),
    compound_name_arguments(Resultado, Nombre, Nuevos).
sustituir(Termino, _, Termino).

sustituir_en(X, Termino, Resultado) :- sustituir(Termino, X, Resultado).

% a, b, d: constantes, variable y potencias enteras de x.
integrar(_, K, Primitiva) :-
    number(K), !,
    integrar_constante(K, Primitiva).
integrar(_, x, x^2/2) :- !.
integrar(Intervalo, x^N, log(abs(x))) :-
    integer(N), N =:= -1, !,
    sin_cero(Intervalo).
integrar(Intervalo, x^N, Primitiva) :-
    integer(N), N < -1, !,
    sin_cero(Intervalo),
    M is -(N + 1),
    potencia_negativa(M, Primitiva).
integrar(_, x^N, Primitiva) :-
    integer(N), N >= 0, !,
    M is N + 1,
    potencia_positiva(M, Primitiva).

% c, f, i: factores constantes y exponenciales de base constante.
integrar(_, K*x, Primitiva) :-
    number(K), !,
    integrar_multiplo_de_x(K, Primitiva).
integrar(_, x*K, Primitiva) :-
    number(K), !,
    integrar_multiplo_de_x(K, Primitiva).
integrar(Intervalo, K*U, Primitiva) :-
    number(K), contiene_x(U), !,
    constante_por_funcion(K, U, Intervalo, Primitiva).
integrar(_, A^x, Primitiva) :-
    number(A), A > 0, !,
    exponencial(A, Primitiva).

% g, h, s: suma, resta y tangente hiperbólica.
integrar(Intervalo, U+V, FU+FV) :-
    integrar(Intervalo, U, FU),
    integrar(Intervalo, V, FV).
integrar(Intervalo, U-V, FU-FV) :-
    integrar(Intervalo, U, FU),
    integrar(Intervalo, V, FV).
integrar(_, tanh(x), log(cosh(x))).

% k, l, r: seno, coseno y coseno hiperbólico.
integrar(_, sin(x), -cos(x)).
integrar(_, cos(x), sin(x)).
integrar(_, cosh(x), sinh(x)).

integrar_constante(0, 0) :- !.
integrar_constante(1, x) :- !.
integrar_constante(K, K*x).

potencia_positiva(1, x) :- !.
potencia_positiva(M, x^M/M).

potencia_negativa(1, -1/x) :- !.
potencia_negativa(M, -1/(M*x^M)).

% Una potencia negativa no admite intervalos que incluyan x = 0.
sin_cero(intervalo(A, B)) :-
    ( A =< 0, 0 =< B -> error_integracion('POTENCIA_NEGATIVA_EN_CERO')
    ; true
    ).

integrar_multiplo_de_x(0, 0) :- !.
integrar_multiplo_de_x(1, x^2/2) :- !.
integrar_multiplo_de_x(K, K*x^2/2).

constante_por_funcion(0, _, _, 0) :- !.
constante_por_funcion(1, U, Intervalo, Primitiva) :- !,
    integrar(Intervalo, U, Primitiva).
constante_por_funcion(K, U, Intervalo, K*Primitiva) :-
    integrar(Intervalo, U, Primitiva).

exponencial(1, x) :- !.
exponencial(A, A^x/log(A)).

contiene_x(x) :- !.
contiene_x(Termino) :-
    compound(Termino),
    compound_name_arguments(Termino, _, Argumentos),
    member(Argumento, Argumentos),
    contiene_x(Argumento).
