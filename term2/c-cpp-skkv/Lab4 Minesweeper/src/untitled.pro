QT += core gui widgets
CONFIG += c++20

TARGET = Minesweeper
TEMPLATE = app

SOURCES += main.cpp \
           mainwindow.cpp \
           gameboard.cpp \
           cell.cpp \
           game_dialog.cpp \
           game_saver.cpp

HEADERS += mainwindow.h \
           gameboard.h \
           cell.h \
           game_dialog.h \
           game_saver.h

TRANSLATIONS += language_ru.ts \
                language_en.ts

RESOURCES += translations.qrc
