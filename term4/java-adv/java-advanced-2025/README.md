# Тесты к курсу «Технологии Java»

[Условия домашних заданий](https://www.kgeorgiy.info/courses/java-advanced/homeworks.html)


## Домашнее задание 15. TextStatistics

Тестирование

 * Базовый вариант (`text-statistics`)

Тестовый модуль: [info.kgeorgiy.java.advanced.i18n](artifacts/info.kgeorgiy.java.advanced.i18n.jar)

На сервере производится тестирование на полном наборе тестов.
Число реально прошедших тестов можно увидеть в строке
`!!!  Passed: X of Y`.


## Домашнее задание 14. HelloNonblockingUDP

Интерфейсы

 * `HelloUDPNonblockingClient` должен реализовывать интерфейс
    [HelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPNonblockingServer` должен реализовывать интерфейс
    [HelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * Базовый вариант (`client` и `server`)
 * Простая модификация (`new-client` и `new-server`)
    * `HelloUDPNonblockingClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
 * Сложная модификация (`new-client-i18n` и `new-server-i18n`)
    * `HelloUDPNonblockingClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
    * На противоположной стороне находится многоязычная система,
      дающая ответы на различных языках.
 * Продвинутая модификация (`new-client-evil` и `new-server-evil`)
    * `HelloUDPNonblockingClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
    * На противоположной стороне находится старая многоязычная система,
      не полностью соответствующая последней версии спецификации.


## Домашнее задание 11. HelloUDP

Интерфейсы

 * `HelloUDPClient` должен реализовывать интерфейс
    [HelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPServer` должен реализовывать интерфейс
    [HelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * Базовый вариант (`client` и `server`)
 * Простая модификация (`new-client` и `new-server`)
    * `HelloUDPClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
 * Сложная модификация (`new-client-i18n` и `new-server-i18n`)
    * `HelloUDPClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
    * На противоположной стороне находится многоязычная система,
      дающая ответы на различных языках.
 * Продвинутая модификация (`new-client-evil` и `new-server-evil`)
    * `HelloUDPClient` должен реализовывать интерфейс
      [NewHelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloClient.java).
    * На противоположной стороне находится старая многоязычная система,
      не полностью соответствующая последней версии спецификации.

Тестовый модуль: [info.kgeorgiy.java.advanced.hello](artifacts/info.kgeorgiy.java.advanced.hello.jar)

Исходный код тестов:

 * [Клиент](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClientTest.java)
 * [Сервер](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServerTest.java)


## Домашнее задание 10. Web Crawler

Тесты используют только внутренние данные и ничего не скачивают из интернета.

Тестирование
 
 * простой вариант (`easy`):
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/EasyCrawlerTest.java)
 * сложный вариант (`hard`):
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/HardCrawlerTest.java)
 * простая модификация (`new-easy`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewEasyCrawlerTest.java)
 * сложная модификация (`new-hard`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewHardCrawlerTest.java)
 * продвинутый вариант (`advanced`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawlerTest.java)

[Интерфейсы и вспомогательные классы](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/)

Тестовый модуль: [info.kgeorgiy.java.advanced.crawler](artifacts/info.kgeorgiy.java.advanced.crawler.jar)


## Домашнее задание 9. Параллельный запуск

Тестирование

 * простой вариант (`scalar`):
    [тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ScalarMapperTest.java)
 * сложный вариант (`list`):
    [тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ListMapperTest.java)
 * продвинутый вариант (`advanced`):
    * При и после закрытия потоки в `ParallelMapper::map` должны завершаться
      с `IllegalStateException`
    * Класс `IterativeParallelism` должен реализовывать интерфейс
      [AdvancedIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/AdvancedMapperTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.mapper](artifacts/info.kgeorgiy.java.advanced.mapper.jar)


## Домашнее задание 8. Итеративный параллелизм

Тестирование

 * простой вариант (`scalar`):
    * Класс должен реализовывать интерфейс
      [ScalarIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ScalarIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ScalarIPTest.java)
 * сложный вариант (`list`):
    * Класс должен реализовывать интерфейс
      [ListIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ListIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ListIPTest.java)
 * продвинутый вариант (`advanced`):
    * Класс должен реализовывать интерфейс
      [AdvancedIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIPTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.iterative](artifacts/info.kgeorgiy.java.advanced.iterative.jar)


## Домашнее задание 6, 7. JarImplementor

Класс `Implementor` должен дополнительно реализовывать интерфейс
[JarImpler](modules/info.kgeorgiy.java.advanced.implementor.tools/info/kgeorgiy/java/advanced/implementor/tools/JarImpler.java).

Скрипты, `MANIFEST.MF` и `.jar-файл` должны находиться в каталоге `scripts`
в корне репозитория.
Скомпилированный Javadoc должен находиться в каталоге `javadoc`
в корне репозитория.

В скриптах вы можете рассчитывать на то, что репозиторий курса
лежит рядом с вашим репозиторием в каталоге `java-advanced-2025`.

Вы можете использовать код из [Compiler.java](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Compiler.java)
в своём решении (но не сам класс).

Исходный код

 * простой вариант (`interface`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor.tools/info/kgeorgiy/java/advanced/implementor/tools/InterfaceJarImplementorTest.java)
 * сложный вариант (`class`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor.tools/info/kgeorgiy/java/advanced/implementor/tools/ClassJarImplementorTest.java)
 * продвинутый вариант (`advanced`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor.tools/info/kgeorgiy/java/advanced/implementor/tools/AdvancedJarImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor.tools](artifacts/info.kgeorgiy.java.advanced.implementor.tools.jar)


## Домашнее задание 5. Implementor

Класс `Implementor` должен реализовывать интерфейс
[Impler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java).

Исходный код

 * простой вариант (`interface`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceImplementorTest.java)
 * сложный вариант (`class`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassImplementorTest.java)
 * продвинутый вариант (`advanced`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)


## Домашнее задание 4. Сплитераторы и коллекторы

Исходный код

 * простой вариант (`easy`):
    * Класс `Lambda` должен реализовывать интерфейс
      [EasyLambda](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/EasyLambda.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/EasyLambdaTest.java)
 * сложный вариант (`hard`):
    * Класс `Lambda` должен реализовывать интерфейс
      [HardLambda](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/HardLambda.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/HardLambdaTest.java)
 * продвинутый вариант (`advanced`):
    * Класс `Lambda` должен реализовывать интерфейс
      [AdvancedLambda](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/AdvancedLambda.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.lambda/info/kgeorgiy/java/advanced/lambda/AdvancedLambdaTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.lambda](artifacts/info.kgeorgiy.java.advanced.lambda.jar)


## Домашнее задание 3. Студенты

Исходный код

 * простой вариант (`StudentQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQueryTest.java)
 * сложный вариант (`GroupQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQueryTest.java)
 * продвинутый вариант (`AdvancedQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQueryTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.student](artifacts/info.kgeorgiy.java.advanced.student.jar)


## Домашнее задание 2. ArraySortedSet

Исходный код

 * простой вариант (`SortedSet`):
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/SortedSetTest.java)
 * сложный вариант (`NavigableSet`):
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/NavigableSetTest.java)
 * продвинутый вариант (`AdvancedSet`):
    * `ArraySet` должен дополнительно реализовывать интерфейс [AdvancedSet](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/AdvancedSet.java)
        * Метод `toMap` должен создавать новый `Map`, отображающий элементы множества в заданное значение.
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/AdvancedSetTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.arrayset](artifacts/info.kgeorgiy.java.advanced.arrayset.jar)


## Домашнее задание 1. Обход файлов

Исходный код

 * простой вариант (`Walk`):
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/WalkTest.java)
 * сложный вариант (`RecursiveWalk`):
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)
 * продвинутый вариант (`AdvancedWalk`):
    * Третьим аргументом командной строки может быть задан алгоритм хеширования: `sha-256` или `md5`.
    * [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/AdvancedWalkTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.walk](artifacts/info.kgeorgiy.java.advanced.walk.jar)

Для того, чтобы протестировать программу:

 * Скачайте
    * тесты
        * [базовый модуль](artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [тестовый модуль](artifacts/info.kgeorgiy.java.advanced.walk.jar) (свой для каждого ДЗ)
    * [библиотеки](lib)
 * Откомпилируйте решение домашнего задания
 * Протестируйте домашнее задание
    * Текущая директория должна:
       * содержать все скачанные `.jar` файлы;
       * содержать скомпилированное решение;
       * __не__ содержать скомпилированные самостоятельно тесты.
    * Запустите тесты:
        `java -cp . -p . -m <тестовый модуль> <вариант> <полное имя класса>`
    * Пример для простого варианта ДЗ-1:
        `java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>`
