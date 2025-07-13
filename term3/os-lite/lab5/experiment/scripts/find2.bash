#!/bin/bash

run_and_check_test() {
  n="$1" 
  echo "Тестируем значение N = $n"

  ./run2.bash "$n" &
  wait

  if dmesg | grep -q 'Out of memory: Killed process'; then
    echo "Ошибка: Аварийная остановка при N = $n"
    return 1
  else
    echo "Успешное выполнение при N = $n"
    return 0
  fi
}

min=1000000 
max=7500000 
max_n=0  

echo "Запуск бинарного поиска для определения безопасного N"

while (( min <= max )); do
  mid=$(( (min + max) / 2 )) 

  if run_and_check_test "$mid"; then
    max_n="$mid"
    min=$((mid + 1))
  else
    max=$((mid - 1))
  fi
done

echo "Максимальное значение N, при котором нет аварий: $max_n"
