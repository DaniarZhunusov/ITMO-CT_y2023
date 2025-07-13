#ifndef GAME_DIALOG_H
#define GAME_DIALOG_H

#include <QDialog>
#include <QLineEdit>

class GameDialog : public QDialog
{
	Q_OBJECT

  public:
	GameDialog(QWidget *parent = nullptr);
	int getWidth() const;
	int getHeight() const;
	int getMines() const;

  private slots:
	void accept();

  private:
	QLineEdit *widthInput;
	QLineEdit *heightInput;
	QLineEdit *minesInput;
};

#endif	  // GAME_DIALOG_H
