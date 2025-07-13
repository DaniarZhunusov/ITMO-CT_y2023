#include "gameboard.h"

#include <QGridLayout>
#include <QMessageBox>
#include <QRandomGenerator>
#include <QResizeEvent>

GameBoard::GameBoard(QWidget *parent) : QWidget(parent), rows(10), cols(10), minesCount(10), firstClick(true)
{
	setupBoard();
}

// Установка дебаг мода
void GameBoard::setDebugMode(bool enabled)
{
	debugMode = enabled;
	revealMines();
}

// Функция которая помечает мины при дебаге
void GameBoard::revealMines()
{
	for (auto &row : cells_)
	{
		for (auto &cell : row)
		{
			if (cell->hasMine())
			{
				cell->setText(debugMode ? "*" : "");
			}
		}
	}
}

// Функция начала новой игры
void GameBoard::newGame()
{
	firstClick = true;
	setupBoard(rows, cols, minesCount);
	emit flaggedCellsChanged(minesCount);
}

// Функция установки поля игры
void GameBoard::setupBoard(int width, int height, int mines)
{
	rows = height;
	cols = width;
	minesCount = mines;
	firstClick = true;
	createBoard();
}

// Функция создания поля игры
void GameBoard::createBoard()
{
	gameIsOver = false;
	flaggedCells = 0;
	totalPlacedFlags = 0;

	// Удаляем старый layout и все его виджеты
	if (layout())
	{
		QLayoutItem *item;
		while ((item = layout()->takeAt(0)) != nullptr)
		{
			delete item->widget();
			delete item;
		}
		delete layout();
	}

	// Создаем новый QGridLayout
	QGridLayout *gridLayout = new QGridLayout(this);
	gridLayout->setSpacing(0);	  // убираем пустое пространство между клетками
	gridLayout->setContentsMargins(0, 0, 0, 0);	   // убираем отступы

	cells_.resize(rows);
	for (int row = 0; row < rows; ++row)
	{
		cells_[row].resize(cols);
		for (int col = 0; col < cols; ++col)
		{
			Cell *cell = new Cell(this);
			cells_[row][col] = cell;
			gridLayout->addWidget(cell, row, col);
			connect(cell, &Cell::leftClicked, this, [this, row, col]() { revealCell(row, col); });
			connect(cell, &Cell::rightClicked, this, [this, row, col]() { flagCell(row, col); });
			connect(cell, &Cell::middleClicked, this, [this, row, col]() { quickReveal(row, col); });
		}
	}

	setLayout(gridLayout);

	// Устанавливаем минимальные и максимальные размеры для растягивания
	setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
	setMinimumSize(450, 450);	 // Устанавливаем минимальный размер окна

	// Перераспределяем клетки
	resizeCells(size());
}

// Получение кол-ва строк
int GameBoard::rowCount() const
{
	return rows;
}

// Получение кол-ва столбцов
int GameBoard::columnCount() const
{
	return cols;
}

// Получение кол-ва мин
int GameBoard::mineCount() const
{
	return minesCount;
}

// Получение матрицы ячеек
QVector< QVector< Cell * > > GameBoard::cells() const
{
	return cells_;
}

// Установка ячейки в матрице
void GameBoard::setCell(int row, int col, Cell *cell)
{
	cells_[row][col] = cell;
	QGridLayout *gridLayout = qobject_cast< QGridLayout * >(layout());
	if (gridLayout)
	{
		gridLayout->addWidget(cell, row, col);
	}
}

bool GameBoard::isFirstClick() const
{
	return firstClick;
}

void GameBoard::setFirstClick(bool firstClick)
{
	this->firstClick = firstClick;
}

int GameBoard::getFlaggedCells() const
{
	return flaggedCells;
}

bool GameBoard::canRevealCell(int row, int col) const
{
	// Проверяем, не закончена ли игра
	if (gameIsOver)
		return false;

	// Проверяем, не раскрыта ли уже клетка
	if (cells_[row][col]->isRevealed())
		return false;

	// Проверяем, не помечена ли клетка флажком
	if (cells_[row][col]->isFlagged())
		return false;

	// Проверяем, не помечена ли клетка вопросительным знаком
	if (cells_[row][col]->isQuestioned())
		return false;

	return true;
}

// Раскрытие ячейки
void GameBoard::revealCell(int row, int col)
{
	if (!canRevealCell(row, col))
		return;

	if (firstClick)
	{
		placeMines(row, col);
		calculateAdjacentMines();
		firstClick = false;
	}

	cells_[row][col]->reveal();

	if (cells_[row][col]->adjacentMines() == 0)
	{
		revealEmptyArea(row, col);
	}
	else
	{
		cells_[row][col]->setText(QString::number(cells_[row][col]->adjacentMines()));
		cells_[row][col]->setEnabled(false);
	}

	if (cells_[row][col]->hasMine())
	{
		revealAllCells(false);
		cells_[row][col]->looseMine();
		QMessageBox::information(this, tr("A loss!"), tr("You hit a mine!"));
		gameIsOver = true;
	}
	else
	{
		if (cells_[row][col]->adjacentMines() == 0)
		{
			revealEmptyArea(row, col);
		}
		else
		{
			cells_[row][col]->setText(QString::number(cells_[row][col]->adjacentMines()));
			cells_[row][col]->setEnabled(false);
		}
	}
}

// Раскрытие всех ячеек
void GameBoard::revealAllCells(bool win)
{
	for (auto &row : cells_)
	{
		for (auto &cell : row)
		{
			cell->reveal();
			if (win && cell->hasMine())
				cell->winMine();
		}
	}
}

// Установка флага на ячейку
void GameBoard::flagCell(int row, int col)
{
	if (gameIsOver)
		return;

	Cell *cell = cells_[row][col];
	bool wasFlagged = cell->isFlagged();
	// bool wasQuestioned = cell->isQuestioned();  // Проверка, был ли знак вопроса
	bool flagSuccess = cell->toggleFlag();	  // Переключаем флажок (устанавливаем или снимаем)

	if (cell->isFlagged())	  // Если флажок установлен
	{
		totalPlacedFlags++;	   // Увеличиваем общий счетчик установленных флажков
		if (flagSuccess)
		{
			flaggedCells++;	   // Увеличиваем счетчик правильно установленных флажков
		}
	}
	else if (wasFlagged && !cell->isFlagged())	  // Если флажок снят
	{
		totalPlacedFlags--;	   // Уменьшаем общий счетчик установленных флажков
		if (wasFlagged && flagSuccess)
		{
			flaggedCells--;	   // Уменьшаем счетчик правильно установленных флажков
		}
	}

	// Обновляем отображение количества оставшихся мин
	emit flaggedCellsChanged(minesCount - totalPlacedFlags);

	// Проверка условия победы
	if (flaggedCells == minesCount)
	{
		bool allMinesCorrectlyFlagged = true;

		for (int row = 0; row < rows; ++row)
		{
			for (int col = 0; col < cols; ++col)
			{
				if (cells_[row][col]->hasMine() && !cells_[row][col]->isFlagged())
				{
					allMinesCorrectlyFlagged = false;
					break;
				}
				if (!cells_[row][col]->hasMine() && cells_[row][col]->isFlagged())
				{
					allMinesCorrectlyFlagged = false;
					break;
				}
			}
		}

		if (allMinesCorrectlyFlagged)
		{
			QMessageBox::information(this, tr("Congratulations!"), tr("You found all the mines and won!"));
			revealAllCells(true);
			gameIsOver = true;
		}
	}
}

// Функция раскрытия ячеек на СКМ
void GameBoard::quickReveal(int row, int col)
{
	if (!cells_[row][col]->isRevealed() || cells_[row][col]->adjacentMines() == 0)
	{
		return;
	}

	int flaggedMines = 0;
	int hiddenCells = 0;
	QVector< QPair< int, int > > hiddenPositions;

	for (int dr = -1; dr <= 1; ++dr)
	{
		for (int dc = -1; dc <= 1; ++dc)
		{
			int nr = row + dr;
			int nc = col + dc;
			if (isValidCell(nr, nc))
			{
				if (cells_[nr][nc]->isFlagged())
				{
					flaggedMines++;
				}
				else if (!cells_[nr][nc]->isRevealed())
				{
					hiddenCells++;
					hiddenPositions.append(qMakePair(nr, nc));
				}
			}
		}
	}

	if (flaggedMines == cells_[row][col]->adjacentMines())
	{
		for (const auto &pos : hiddenPositions)
		{
			revealCell(pos.first, pos.second);
		}
	}
	else
	{
		for (const auto &pos : hiddenPositions)
		{
			cells_[pos.first][pos.second]->highlight();
		}
	}
}

// Установка мин
void GameBoard::placeMines(int initialRow, int initialCol)
{
	int placedMines = 0;
	while (placedMines < minesCount)
	{
		int row = QRandomGenerator::global()->bounded(rows);
		int col = QRandomGenerator::global()->bounded(cols);
		if ((row != initialRow || col != initialCol) && !cells_[row][col]->hasMine())
		{
			cells_[row][col]->setMine(true);
			placedMines++;
		}
	}
	revealMines();
}

// Расчет кол-ва мин рядом с ячейкой
void GameBoard::calculateAdjacentMines()
{
	for (int row = 0; row < rows; ++row)
	{
		for (int col = 0; col < cols; ++col)
		{
			if (!cells_[row][col]->hasMine())
			{
				int adjacentMines = 0;
				for (int dr = -1; dr <= 1; ++dr)
				{
					for (int dc = -1; dc <= 1; ++dc)
					{
						int nr = row + dr;
						int nc = col + dc;
						if (isValidCell(nr, nc) && cells_[nr][nc]->hasMine())
						{
							adjacentMines++;
						}
					}
				}
				cells_[row][col]->setAdjacentMines(adjacentMines);
			}
		}
	}
}

// Раскрытие пустой арены поля
void GameBoard::revealEmptyArea(int row, int col)
{
	QVector< QPair< int, int > > stack;
	stack.push_back({ row, col });

	while (!stack.isEmpty())
	{
		auto [r, c] = stack.back();
		stack.pop_back();

		for (int dr = -1; dr <= 1; ++dr)
		{
			for (int dc = -1; dc <= 1; ++dc)
			{
				int nr = r + dr;
				int nc = c + dc;
				if (isValidCell(nr, nc) && !cells_[nr][nc]->isRevealed() && !cells_[nr][nc]->isFlagged())
				{
					cells_[nr][nc]->reveal();
					if (cells_[nr][nc]->adjacentMines() == 0)
					{
						stack.push_back({ nr, nc });
					}
				}
			}
		}
	}
}

// Проверка на валидность ячейки
bool GameBoard::isValidCell(int row, int col)
{
	return row >= 0 && row < rows && col >= 0 && col < cols;
}

// Переопределение функции изменения размера окна
void GameBoard::resizeEvent(QResizeEvent *event)
{
	QWidget::resizeEvent(event);
	resizeCells(event->size());
}

/// Функция перераспределения клеток
void GameBoard::resizeCells(const QSize &newSize)
{
	// Новые размеры окна
	int newWidth = newSize.width();
	int newHeight = newSize.height();

	// Вычисление соотношения сторон
	float aspectRatio = static_cast< float >(cols) / static_cast< float >(rows);

	// Вычисление нового размера ячеек
	int cellSize;
	if (newWidth / aspectRatio <= newHeight)
	{
		cellSize = newWidth / cols;
	}
	else
	{
		cellSize = newHeight / rows;
	}

	// Центральное размещение ячеек в окне
	int xOffset = (newWidth - cellSize * cols) / 2;
	int yOffset = (newHeight - cellSize * rows) / 2;

	for (int row = 0; row < rows; ++row)
	{
		for (int col = 0; col < cols; ++col)
		{
			cells_[row][col]->setFixedSize(cellSize, cellSize);
			cells_[row][col]->move(xOffset + col * cellSize, yOffset + row * cellSize);
		}
	}
}
