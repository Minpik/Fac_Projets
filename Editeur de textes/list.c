#include "list.h"
#include <string.h>
#include <stdio.h>

list *createList() {
  list *list = malloc(sizeof(struct list));
  
  list->first = createLine("");
  
  return list;
}

void clearList(list *list) {
  line *current = list->first;
  
  while(current != NULL) {
    line *next = current->next;
    free(current->text);
    free(current);
    current = next;
  }

  free(list);
}

line *createLine(const char *text) {
  line *line = malloc(sizeof(struct line));
  line->text = malloc(sizeof(char) * LINE_SIZE);
  strcpy(line->text, text);
  int length = lengthOfLine(line);
  if (line->text[length - 1] == '\n')
    line->text[length - 1] = '\0';

  line->num = 0;
  line->previous = NULL;
  line->next = NULL;

  return line;
}

void insertLineAfter(line *new, line *after) {
  new->previous = after;
  new->next = after->next;
  
  if (after->next != NULL)
    after->next->previous = new;

  after->next = new;

  new->num = after->num + 1;
  struct line *current = new->next;
  while (current != NULL) {
    current->num++;
    current = current->next;
  }
}

int lengthOfText(char *text) {
  int i = 0;
  while (*(text + i) != '\0')
    i++;

  return i;
}

int lengthOfLine(line *line) {
  return lengthOfText(line->text);
}

void lineAddChar(line *line, char c, int index) {
  if (index < 0 || index > strlen(line->text))
    return;

  // on deplace le text d'un cran vers la droite
  char *text = line->text;
  memmove(text + index + 1, text + index, strlen(text + index) + 1);
  text[index] = c;
}

void lineRemoveChar(line *line, int index) {
  if (index < 0 || index > strlen(line->text))
    return;

  // on deplace le text vers la gauche
  char *text = line->text;
  memmove(text + index, text + index + 1, strlen(text + index) + 1);
}

void lineRemoveChars(line *line, int startIndex, int endIndex) {
  if (startIndex < 0 || startIndex >= endIndex)
    return;
  
  int length = strlen(line->text);
  if (endIndex > length)
    endIndex = length;

  // on deplace le text vers la gauche
  char *text = line->text;
  memmove(text + startIndex, text + endIndex, strlen(text + endIndex) + 1);
}

void deleteLine(line *line) {
  struct line *previous = line->previous;
  struct line *next = line->next;

  if (previous != NULL)
    previous->next = line->next;

  if (next != NULL) {
    next->previous = line->previous;

    struct line *current = next;
    while (current != NULL) {
      current->num--;
      current = current->next;
    }
  }
  
  free(line->text);
  free(line);
}

// fusion une ligne avec la suivante
void fusion(line *line) {
  int length = lengthOfLine(line);
  struct line *next = line->next;
  // on deplace le text sur la ligne précedente
  memmove(line->text + length, next->text,
	  strlen(next->text) + 1);
  deleteLine(next);
}
