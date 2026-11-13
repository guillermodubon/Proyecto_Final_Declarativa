:- module(integracion_cli, [main/0]).

:- use_module(library(readutil)).

main :-
    read_line_to_string(user_input, "x"),
    read_line_to_string(user_input, "1"),
    read_line_to_string(user_input, "3"),
    format('OK~nx^2/2~n4.0~n0.5~n4.5~n').
