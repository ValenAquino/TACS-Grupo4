#!/bin/sh
for script in /loadtest/scripts/*.js; do
  # saltar los helpers
  case "$script" in
    */helpers/*) continue ;;
  esac

  echo "==============================="
  echo "Corriendo: $script"
  echo "==============================="
  k6 run "$script"
done