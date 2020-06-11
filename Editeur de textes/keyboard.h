#ifndef KEYBOARD_H
#define KEYBOARD_H

#include "list.h"

void keyboard(int i);
void keyboardMainWin();
void keyboardBottomWin();
void changeCurWin();
void printNotice(char *value, int attr);
void raccourcisInit();
void raccourcisFree();
void cutLines();
void lineWrapping(line *line);

#endif
