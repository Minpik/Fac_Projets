#ifndef GLOBAL_H
#define GLOBAL_H

#include "list.h"
#include <ncurses.h>

typedef struct cursorInfos {
  line *line;
  int pos;
  int x;
  int y;
} cursorInfos;
cursorInfos *createCursorInfos();
void cursorCpy(cursorInfos *dst, cursorInfos *src);
int cursorCmp(cursorInfos *c1, cursorInfos *c2);

#define FALSE 0
#define TRUE 1

#define NOMBRE_RACCOURCIS 3
#define COPIER 0
#define COUPER 1
#define COLLER 2

extern list *listLines;
extern cursorInfos *curInfos, *selectionBegin, *selectionEnd;
extern char *selectionBuffer;
extern int end;
extern WINDOW *mainWin, *bottomWin, *curWin;
extern char *commandsBuffer;
extern int wrapping, scrollV, scrollH;
extern int maxWidth;
extern int notice;
extern char **raccourcis;
extern int autoCut;
#endif
