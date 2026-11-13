:- encoding(utf8).
:- module(evaluacion_integracion, [resolver_con/5]).

:- use_module('entrada.pl').
:- meta_predicate resolver_con(3, +, +, +, -).

resolver_con(Integrar, Texto, Inferior, Superior, Resultado) :-
    catch(( leer_solicitud(Texto, Inferior, Superior, Expresion, A, B),
            ( once(call(Integrar, intervalo(A, B), Expresion, Primitiva)) -> true
            ; error_integracion('NO_SOPORTADA', "Todavía no hay una regla para esa expresión.")
            ),
            evaluar_intervalo(Primitiva, A, B, Valor, Fa, Fb),
            Resultado = ok(Primitiva, Valor, Fa, Fb)
          ), error(integracion(Codigo, Mensaje), _), Resultado = error(Codigo, Mensaje)).

evaluar_intervalo(Primitiva, A, B, Valor, Fa, Fb) :-
    sustituir(Primitiva, A, EnA), sustituir(Primitiva, B, EnB),
    ( catch(( VA is EnA, VB is EnB,
              Valor is float(VB - VA), Fa is float(VA), Fb is float(VB)
            ), error(evaluation_error(_), _), fail),
      maplist(finito, [Valor, Fa, Fb]) -> true
    ; error_integracion('DOMINIO', "La primitiva no se puede evaluar en los límites indicados.")
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
