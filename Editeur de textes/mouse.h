#ifndef MOUSE_H
#define MOUSE_H

#include <ncurses.h>
#include "globals.h"

void mouse(int i);
void getCursorInfos(MEVENT *event, cursorInfos *infos);

#endif
