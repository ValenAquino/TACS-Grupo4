#!/bin/sh
if [ -z "$1" ]; then
  # sin argumentos → corre todos
  for script in /loadtest/scripts/*.js; do
    case "$script" in */helpers/*) continue ;; esac
    echo "==============================="
    echo "Corriendo: $script"
    echo "==============================="
    k6 run "$script"
  done
else
  # con argumento → corre ese archivo
  k6 run "/loadtest/scripts/$1"
fi