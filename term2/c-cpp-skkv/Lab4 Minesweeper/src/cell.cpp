#include "cell.h"

#include <QApplication>
#include <QMouseEvent>
#include <QResizeEvent>

Cell::Cell(QWidget *parent) : QPushButton(parent), mine(false), revealed(false), flagged(false), adjacentMinesCount(0)
{
	setFixedSize(25, 25);
}

// Функция раскрытия ячейки
void Cell::reveal()
{
	if (!revealed)
	{
		revealed = true;
		// Если является миной, то перекрашивается в красной и помечается как М, иначе просто показывает кол-во мин
		// рядом
		if (mine)
		{
			setText("M");
			setStyleSheet("background-color: red;");
		}
		else
		{
			setText(QString::number(adjacentMinesCount));
			setEnabled(false);
		}
	}
}

// Включение флажка (на лкм)
bool Cell::toggleFlag()
{
	if (!revealed)
	{
		if (flagged)	// Если сейчас установлен флаг, переключаем на знак вопроса
		{
			flagged = false;
			questioned = true;
			setText("?");
		}
		else if (questioned)	// Если сейчас установлен знак вопроса, убираем его
		{
			questioned = false;
			setText("");
		}
		else	// Если ничего не установлено, ставим флаг
		{
			flagged = true;
			setText("F");
		}
	}
	if (mine)
		return true;
	else
		return false;
}

bool Cell::isQuestioned() const
{
	return questioned;
}

// Пометка при скм
void Cell::highlight()
{
	setStyleSheet("background-color: yellow;");
}

// Перекрашивается в оранжевый мина на которую наступили
void Cell::looseMine()
{
	setStyleSheet("background-color: orange;");
}

// Перекрашивается в зеленый при выигрыше
void Cell::winMine()
{
	setStyleSheet("background-color: green;");
}

// Установка мины в ячейку
void Cell::setMine(bool mine)
{
	this->mine = mine;
}

// Проверка есть ли мина
bool Cell::hasMine() const
{
	return mine;
}

// Устанавливает кол-во мин рядом
void Cell::setAdjacentMines(int count)
{
	adjacentMinesCount = count;
}

// Получение кол-ва мин рядом
int Cell::adjacentMines() const
{
	return adjacentMinesCount;
}

// Проверка раскрыта ли ячейка
bool Cell::isRevealed() const
{
	return revealed;
}

// Проверка помечена ли ячейка флажком
bool Cell::isFlagged() const
{
	return flagged;
}

int Cell::row() const
{
	return cellRow;
}

int Cell::col() const
{
	return cellCol;
}

// Переопределение евента нажатия мыши
void Cell::mousePressEvent(QMouseEvent *event)
{
	// Проверяем, включен ли режим левши
	bool isLeftHandedMode = QApplication::instance()->property("leftHandedMode").toBool();

	// Если включен режим левши, меняем местами левую и правую кнопку
	Qt::MouseButton button = event->button();
	if (isLeftHandedMode)
	{
		if (button == Qt::LeftButton)
		{
			button = Qt::RightButton;
		}
		else if (button == Qt::RightButton)
		{
			button = Qt::LeftButton;
		}
	}

	// Обрабатываем нажатия в зависимости от кнопки
	if (button == Qt::LeftButton)
	{
		emit leftClicked();
	}
	else if (button == Qt::RightButton)
	{
		emit rightClicked();
	}
	else if (button == Qt::MiddleButton)
	{
		emit middleClicked();
	}
}

// Переопределение события изменения размера
void Cell::resizeEvent(QResizeEvent *event)
{
	int size = qMin(event->size().width(), event->size().height());
	setFixedSize(size, size);
}
