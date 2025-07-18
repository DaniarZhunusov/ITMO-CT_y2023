### [Билет 380.  Проталкивание отрицаний](PushingNegative.java)

Напишите программу, преобразующую логические формулы в форму, в которой отрицания применяются только к переменным, но не к константам и составным выражениям. Составные выражения упрощаются по формуле де Моргана и двойного отрицания.

- Логическая формула может содержать:
    - константы: «0» - ложь и «1» - истина;
    - переменные: «a»...«z»;
    - логические связки: «&» - конъюнкция, «|» -дизъюнкция, «~» - отрицание;
    - скобки.
- Логическая формула задается в виде аргумента командной строки, например: «~(a & b) | ~c».
- Преобразованная формула должна водиться на консоль. В формуле должны отсутствовать лишние скобки. Для приведенного примера логической формулы результат должен быть «~a | ~b | ~c».

### [Билет 53.  Линейная электронная таблица](LinearSpreadsheet.java)

Вычислить значения в линейной электронной таблице.

- Линейная электронная таблица содержить пронумерованные ячейки. Нумерация ячеек начинается с единицы.
- Каждая ячейка содержит формулу. Формулы состоят из:
    - констант: целые числа;
    - ссылок на ячейки: «$номерЯчейки»;
    - операций: «+», «-», «*», «/».
    - круглых скобок.
- Содержание электронной таблицы задается в виде аргумента командной строки, в котором формулы разделены символом «|», например: «10 | 20 | $1 *$2».
- Вычисленные значения должны выводиться на консоль. Для приведенного примера результат должен быть «10 | 20 | 200».
- Гарантируется отсутствие циклических ссылок.
