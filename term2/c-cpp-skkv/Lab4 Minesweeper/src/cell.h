#ifndef CELL_H
#define CELL_H

#include <QPushButton>

class Cell : public QPushButton
{
	Q_OBJECT

  public:
	Cell(QWidget *parent = nullptr);
	void reveal();
	bool toggleFlag();
	void highlight();
	void looseMine();
	void winMine();
	void setMine(bool mine);
	bool hasMine() const;
	void setAdjacentMines(int count);
	int adjacentMines() const;
	bool isRevealed() const;
	bool isFlagged() const;
	bool isQuestioned() const;	  // Проверка, установлен ли знак вопроса
	int row() const;
	int col() const;

  signals:
	void leftClicked();
	void rightClicked();
	void middleClicked();

  protected:
	void mousePressEvent(QMouseEvent *event) override;
	void resizeEvent(QResizeEvent *event) override;

  private:
	bool mine;
	bool revealed;
	bool flagged;
	bool questioned;	// Новый статус для знака вопроса
	int adjacentMinesCount;
	int cellRow;
	int cellCol;
};

#endif	  // CELL_H
