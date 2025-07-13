#ifndef GAMESAVER_H
#define GAMESAVER_H

#include "gameboard.h"

#include <QCoreApplication>
#include <QDir>
#include <QSettings>

class GameSaver
{
  public:
	void saveGame(const GameBoard &board);
	bool loadGame(GameBoard &board);
};

#endif	  // GAMESAVER_H
