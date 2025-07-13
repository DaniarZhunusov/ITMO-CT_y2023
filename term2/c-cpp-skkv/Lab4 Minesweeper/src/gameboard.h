#ifndef GAMEBOARD_H
#define GAMEBOARD_H

#include "cell.h"

#include <QVector>
#include <QWidget>

class GameBoard : public QWidget
{
	Q_OBJECT

  public:
	GameBoard(QWidget *parent = nullptr);
	void newGame();
	void setupBoard(int width = 10, int height = 10, int mines = 10);
	int rowCount() const;
	int columnCount() const;
	int mineCount() const;
	void setDebugMode(bool enabled);
	QVector< QVector< Cell * > > cells() const;
	void setCell(int row, int col, Cell *cell);
	bool isFirstClick() const;
	void setFirstClick(bool firstClick);
	int getFlaggedCells() const;

  private slots:
	void revealCell(int row, int col);
	void flagCell(int row, int col);
	void quickReveal(int row, int col);

  signals:
	void flaggedCellsChanged(int remainingMines);

  private:
	QVector< QVector< Cell * > > cells_;
	int rows;
	int cols;
	bool debugMode = false;
	int minesCount;
	bool firstClick;
	bool gameIsOver;
	int flaggedCells;
	int totalPlacedFlags;
	int getTotalPlacedFlags() const { return totalPlacedFlags; }
	void createBoard();
	void revealAllCells(bool win);
	void revealMines();
	void placeMines(int initialRow, int initialCol);
	void calculateAdjacentMines();
	void revealEmptyArea(int row, int col);
	bool isValidCell(int row, int col);
	bool canRevealCell(int row, int col) const;
	void resizeEvent(QResizeEvent *event) override;
	void resizeCells(const QSize &newSize);
};

#endif	  // GAMEBOARD_H
