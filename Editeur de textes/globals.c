#include "globals.h"

list *listLines = NULL;
cursorInfos *curInfos = NULL, *selectionBegin = NULL, *selectionEnd = NULL;
char *selectionBuffer = NULL;
int end = 0;
WINDOW *mainWin = NULL, *bottomWin = NULL, *curWin = NULL;
char *commandsBuffer = NULL;
int wrapping = TRUE, scrollV = 0, scrollH = 0;
int maxWidth = 10;
int notice = FALSE;
char **raccourcis = NULL;
int autoCut = FALSE;

cursorInfos *createCursorInfos() {
	cursorInfos *cI = malloc(sizeof(cursorInfos));
	cI->line = NULL;
	cI->pos = 0;
	cI->x = 0;
	cI->y = 0;
	
	return cI;
}

void cursorCpy(cursorInfos *dst, cursorInfos *src) {
	dst->line = src->line;
	dst->pos = src->pos;
	dst->x = src->x;
	dst->y = src->y;
}

int cursorCmp(cursorInfos *c1, cursorInfos *c2) {
	if (c1->line == c2->line) {
		if (c1->pos > c2->pos)
			return 1;
		else if (c1->pos == c2->pos)
			return 0;
		else
			return -1;
	} else if (c1->line->num > c2->line->num)
		return 1;
	else
		return -1;
}
