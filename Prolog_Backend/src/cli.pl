:- module(integracion_cli, [main/0]).

:- use_module('motor.pl').
:- use_module('protocolo.pl').

main :- ejecutar(resolver).
