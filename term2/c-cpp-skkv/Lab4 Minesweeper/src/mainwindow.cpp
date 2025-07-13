#include "mainwindow.h"

#include "game_saver.h"
#include "gameboard.h"

#include <QActionGroup>
#include <QCheckBox>
#include <QInputDialog>
#include <QLocale>
#include <QMenuBar>
#include <QMessageBox>
#include <QMouseEvent>
#include <QSettings>
#include <QStatusBar>
#include <QToolBar>
#include <QTranslator>

MainWindow::MainWindow(QWidget *parent, bool debugMode) : QMainWindow(parent), debugMode(debugMode)
{
	gameBoard = new GameBoard(this);
	setCentralWidget(gameBoard);

	createActions();
	createMenus();
	createToolBars();
	initStatusBar();	// Инициализация строки состояния

	connect(gameBoard, &GameBoard::flaggedCellsChanged, this, &MainWindow::updateMineCountDisplay);

	loadSettings();
	loadLanguage(currentLanguage);

	GameSaver gameSaver;
	gameSaver.loadGame(*gameBoard);

	if (debugMode)
	{
		debugModeAction->setChecked(false);
		toggleDebugMode(false);
	}
}

MainWindow::~MainWindow()
{
	saveSettings();
}

void MainWindow::createActions()
{
	newGameAction = new QAction(tr("&New Game"), this);
	connect(newGameAction, &QAction::triggered, this, &MainWindow::newGame);

	exitAction = new QAction(tr("&Exit"), this);
	connect(exitAction, &QAction::triggered, this, &MainWindow::close);

	if (debugMode)
	{
		debugModeAction = new QAction(tr("&Debug Mode"), this);
		debugModeAction->setCheckable(true);
		connect(debugModeAction, &QAction::toggled, this, &MainWindow::toggleDebugMode);
	}
}

void MainWindow::createMenus()
{
	gameMenu = menuBar()->addMenu(tr("&Game"));
	gameMenu->addAction(newGameAction);
	gameMenu->addSeparator();
	gameMenu->addAction(exitAction);

	if (debugMode)
	{
		gameMenu->addSeparator();
		gameMenu->addAction(debugModeAction);
	}

	languageMenu = menuBar()->addMenu(tr("&Language"));
	QAction *englishAction = new QAction("English", this);
	QAction *russianAction = new QAction("Русский", this);

	languageMenu->addAction(englishAction);
	languageMenu->addAction(russianAction);

	connect(languageMenu, &QMenu::triggered, this, &MainWindow::switchLanguage);
}

void MainWindow::createToolBars()
{
	gameToolBar = addToolBar(tr("Game"));
	gameToolBar->addAction(newGameAction);

	if (debugMode)
	{
		gameToolBar->addAction(debugModeAction);
	}

	// Создание флажка для режима левши
	leftHandedCheckbox = new QCheckBox(tr("Left handed mode"), this);
	leftHandedCheckbox->setChecked(leftHandedMode);	   // Устанавливаем флажок в начальное состояние

	// Добавление флажка в тулбар
	gameToolBar->addWidget(leftHandedCheckbox);

	// Подключение сигнала переключения флажка к слоту
	connect(leftHandedCheckbox, &QCheckBox::toggled, this, &MainWindow::setLeftHandedMode);
}

void MainWindow::newGame()
{
	bool ok;
	int width = QInputDialog::getInt(this, tr("Set Width"), tr("Width:"), 10, 1, 100, 1, &ok);
	if (!ok)
		return;

	int height = QInputDialog::getInt(this, tr("Set Height"), tr("Height:"), 10, 1, 100, 1, &ok);
	if (!ok)
		return;

	int mines = QInputDialog::getInt(this, tr("Set Mines"), tr("Mines:"), 10, 1, width * height - 1, 1, &ok);
	if (!ok)
		return;

	if ((width == 1 && height == 1 && mines == 1) || (mines >= width * height))
	{
		QMessageBox::warning(
			this,
			tr("Invalid Input"),
			tr("Please enter valid values:\n"
			   "- Width and Height must be greater than 1\n"
			   "- Number of mines must be greater than 0 and less than the total number of cells."));
		newGame();	  // Рекурсивный вызов, если данные некорректны
	}
	else
	{
		gameBoard->setupBoard(width, height, mines);
		initStatusBar();
	}
}

void MainWindow::closeEvent(QCloseEvent *event)
{
	GameSaver gameSaver;
	gameSaver.saveGame(*gameBoard);
	QMainWindow::closeEvent(event);
}

void MainWindow::toggleDebugMode(bool checked)
{
	gameBoard->setDebugMode(checked);
}

void MainWindow::switchLanguage(QAction *action)
{
	QString language = action->text();
	if (language == "English")
	{
		loadLanguage("en");
	}
	else if (language == "Русский")
	{
		loadLanguage("ru");
	}
}

void MainWindow::loadLanguage(const QString &language)
{
	if (currentLanguage != language)
	{
		currentLanguage = language;
		QLocale locale = QLocale(language);
		QLocale::setDefault(locale);
		QString languageFile = QString(":/translations/language_%1.qm").arg(language);
		translator.load(languageFile);
		qApp->installTranslator(&translator);
		retranslateUi();
	}
}

void MainWindow::retranslateUi()
{
	newGameAction->setText(tr("&New Game"));
	exitAction->setText(tr("&Exit"));
	if (debugMode)
	{
		debugModeAction->setText(tr("&Debug Mode"));
	}
	gameMenu->setTitle(tr("&Game"));
	languageMenu->setTitle(tr("Language"));
	gameToolBar->setWindowTitle(tr("Game"));
	if (leftHandedCheckbox)
	{
		leftHandedCheckbox->setText(tr("Left handed mode"));
	}
}

QString MainWindow::iniFilePath() const
{
	return QCoreApplication::applicationDirPath() + QDir::separator() + "settings.ini";
}

void MainWindow::saveSettings()
{
	QSettings settings(iniFilePath(), QSettings::IniFormat);
	settings.setValue("language", currentLanguage);
	settings.setValue("leftHandedMode", leftHandedMode);	// Сохранение состояния режима левши
}

void MainWindow::loadSettings()
{
	QSettings settings(iniFilePath(), QSettings::IniFormat);
	currentLanguage = settings.value("language", "en").toString();
	leftHandedMode = settings.value("leftHandedMode", false).toBool();	  // Загрузка состояния режима левши
	loadLanguage(currentLanguage);
}

void MainWindow::setLeftHandedMode(bool enabled)
{
	leftHandedMode = enabled;
	qApp->setProperty("leftHandedMode", enabled);	 // Установка свойства для глобального доступа
}

void MainWindow::initStatusBar()
{
	statusBar()->showMessage(tr("Remaining mines: %1").arg(gameBoard->mineCount()));
}

void MainWindow::updateMineCountDisplay(int remainingMines)
{
	statusBar()->showMessage(tr("Remaining mines: %1").arg(remainingMines));
}
