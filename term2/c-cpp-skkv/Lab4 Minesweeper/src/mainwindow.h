#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include "gameboard.h"

#include <QCheckBox>
#include <QMainWindow>
#include <QTranslator>

class MainWindow : public QMainWindow
{
	Q_OBJECT

  public:
	MainWindow(QWidget *parent = nullptr, bool debugMode = false);
	~MainWindow();

  protected:
	void closeEvent(QCloseEvent *event) override;
	// bool eventFilter(QObject *obj, QEvent *event) override;

  private slots:
	void newGame();
	void toggleDebugMode(bool checked);
	void switchLanguage(QAction *action);
	void setLeftHandedMode(bool enabled);
	void updateMineCountDisplay(int remainingMines);

  private:
	void createActions();
	void createMenus();
	void createToolBars();
	void loadLanguage(const QString &language);
	void initStatusBar();

	GameBoard *gameBoard;
	QMenu *gameMenu;
	QMenu *languageMenu;
	QToolBar *gameToolBar;
	QAction *newGameAction;
	QAction *exitAction;
	QAction *debugModeAction;
	QCheckBox *leftHandedCheckbox;
	QTranslator translator;
	QString currentLanguage;
	bool debugMode;
	bool leftHandedMode = false;

	void retranslateUi();
	QString iniFilePath() const;
	void saveSettings();
	void loadSettings();
};

#endif	  // MAINWINDOW_H
