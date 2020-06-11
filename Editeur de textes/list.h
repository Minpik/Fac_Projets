#ifndef LIST_H
#define LIST_H

#include <stdlib.h>
#define LINE_SIZE 200

typedef struct line {
  char *text;
  int num;
  struct line *previous;
  struct line *next;
} line;

typedef struct list {
  struct line *first;
} list;

list *createList();
void clearList(list *list);
line *createLine(const char *text);
void insertLineAfter(line *new, line *after);
int lengthOfText(char *text);
int lengthOfLine(line *line);
void lineAddChar(line *line, char c, int index);
void lineRemoveChars(line *line, int startIndex, int endIndex);
void lineRemoveChar(line *line, int index);
void deleteLine(line *line);
void fusion(line *line);

#endif
