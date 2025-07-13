#include "game_saver.h"

#include <QSettings>

// Сохранение состояния игры
void GameSaver::saveGame(const GameBoard &board)
{
	QSettings settings("game_state.ini", QSettings::IniFormat);

	settings.beginGroup("GameBoard");
	settings.setValue("rows", board.rowCount());
	settings.setValue("cols", board.columnCount());
	settings.setValue("minesCount", board.mineCount());
	settings.setValue("firstClick", board.isFirstClick());	  // Сохранение флага первого клика
	settings.endGroup();

	settings.beginWriteArray("Cells");
	for (int row = 0; row < board.rowCount(); ++row)
	{
		for (int col = 0; col < board.columnCount(); ++col)
		{
			settings.setArrayIndex(row * board.columnCount() + col);
			const Cell *cell = board.cells()[row][col];
			settings.setValue("hasMine", cell->hasMine());
			settings.setValue("isFlagged", cell->isFlagged());
			settings.setValue("isRevealed", cell->isRevealed());
			settings.setValue("adjacentMines", cell->adjacentMines());
		}
	}
	settings.endArray();
}

// Загрузка состояния игры
bool GameSaver::loadGame(GameBoard &board)
{
	QSettings settings("game_state.ini", QSettings::IniFormat);

	settings.beginGroup("GameBoard");
	bool ok;
	int rows = settings.value("rows", 10).toInt(&ok);
	int cols = settings.value("cols", 10).toInt(&ok);
	int minesCount = settings.value("minesCount", 10).toInt(&ok);
	bool firstClick = settings.value("firstClick", true).toBool();	  // Загрузка флага первого клика
	settings.endGroup();

	board.setupBoard(cols, rows, minesCount);
	board.setFirstClick(firstClick);	// Установка флага первого клика

	int size = settings.beginReadArray("Cells");
	if (size != rows * cols)
	{
		settings.endArray();
		return false;
	}

	for (int i = 0; i < size; ++i)
	{
		settings.setArrayIndex(i);
		int row = i / cols;
		int col = i % cols;
		Cell *cell = board.cells()[row][col];
		cell->setMine(settings.value("hasMine", false).toBool());
		if (settings.value("isFlagged", false).toBool())
		{
			cell->toggleFlag();
		}
		if (settings.value("isRevealed", false).toBool())
		{
			cell->reveal();
		}
		cell->setAdjacentMines(settings.value("adjacentMines", 0).toInt());
		if (cell->isRevealed() && !cell->hasMine())
		{
			cell->setText(QString::number(cell->adjacentMines()));
			cell->setEnabled(false);
		}
	}
	settings.endArray();

	return true;
}
