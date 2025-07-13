#include "game_dialog.h"

#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QVBoxLayout>

GameDialog::GameDialog(QWidget *parent) :
	QDialog(parent), widthInput(new QLineEdit), heightInput(new QLineEdit), minesInput(new QLineEdit)
{
	QVBoxLayout *layout = new QVBoxLayout;

	layout->addWidget(new QLabel(tr("Width:")));
	layout->addWidget(widthInput);
	layout->addWidget(new QLabel(tr("Height:")));
	layout->addWidget(heightInput);
	layout->addWidget(new QLabel(tr("Mines:")));
	layout->addWidget(minesInput);

	QPushButton *okButton = new QPushButton(tr("OK"));
	connect(okButton, &QPushButton::clicked, this, &GameDialog::accept);
	layout->addWidget(okButton);

	setLayout(layout);
}

int GameDialog::getWidth() const
{
	return widthInput->text().toInt();
}

int GameDialog::getHeight() const
{
	return heightInput->text().toInt();
}

int GameDialog::getMines() const
{
	return minesInput->text().toInt();
}

void GameDialog::accept()
{
	QDialog::accept();
}
