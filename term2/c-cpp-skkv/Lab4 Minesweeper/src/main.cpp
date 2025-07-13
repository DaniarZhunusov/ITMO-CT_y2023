#include "mainwindow.h"

#include <QApplication>
#include <QDir>
#include <QLocale>
#include <QSettings>
#include <QTranslator>

int main(int argc, char *argv[])
{
	QApplication app(argc, argv);

	// Загрузка настроек
	QString iniFilePath = QCoreApplication::applicationDirPath() + QDir::separator() + "settings.ini";
	QSettings settings(iniFilePath, QSettings::IniFormat);
	QString language = settings.value("language", QLocale::system().name()).toString();

	QTranslator translator;
	QString translationFile = QString(":/translations/language_%1.qm").arg(language);

	if (translator.load(translationFile))
	{
		app.installTranslator(&translator);
	}

	bool debugMode = false;
	if (argc > 1 && QString(argv[1]) == "dbg")
	{
		debugMode = true;
	}

	MainWindow mainWindow(nullptr, debugMode);
	mainWindow.show();

	return app.exec();
}
