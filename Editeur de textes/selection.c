#include "selection.h"
#include "globals.h"
#include <string.h>

char *copy() {
  if (selectionBegin->line == NULL || selectionEnd->line == NULL)
		return NULL;
  
  int length = lengthOfSelection();
	char *str = malloc(sizeof(char) * (length +  1));
  // au moin 2 lignes dans la selection
	if (selectionBegin->line != selectionEnd->line) {
    // debut
    strcat(str, selectionBegin->line->text + selectionBegin->pos);
    strcat(str, "\n");
		
    // milieu, si necessaire
		line *current = selectionBegin->line->next;
		while (current != NULL && current != selectionEnd->line) {
			strcat(str, current->text);
      strcat(str, "\n");
			current = current->next;
		}
    
    // fin
    strncat(str, selectionEnd->line->text, selectionEnd->pos + 1);
	} else // 1 seule ligne
    strncpy(str, selectionBegin->line->text + selectionBegin->pos,
            (selectionEnd->pos - selectionBegin->pos) + 1);
	
  str[length] = '\0';

  // on change la position du curseur à l'ecran et dans le buff
  curInfos->pos = selectionBegin->pos;
  curInfos->x = selectionBegin->x;
  curInfos->y = selectionBegin->y;

	return str;
}

char *cut(int cpy) {
  if (selectionBegin->line == NULL || selectionEnd->line == NULL)
    return NULL;
  
  char *str;
  if (cpy == TRUE)
    str = copy();

  if (selectionBegin->line != selectionEnd->line) {
    // on supprime toutes les lignes entre la premiere et la derniere
    line *current = selectionBegin->line->next;
    while (current != NULL && current != selectionEnd->line) {
      line *next = current->next;
      deleteLine(current);
      current = next;
    }
    
    // on copie le reste de la derniere ligne à la suiter de la premiere
    /*int endPos = (*(selectionEnd->line->text + selectionEnd->pos) == '\n') ?
      selectionEnd->pos : selectionEnd->pos + 1;*/
    strcpy(selectionBegin->line->text + selectionBegin->pos,
           selectionEnd->line->text + selectionEnd->pos + 1);
    
    // on supprime la derniere
    deleteLine(selectionEnd->line);
	} else // on seule ligne donc on la decoupe
      lineRemoveChars(selectionEnd->line, selectionBegin->pos,
                      selectionEnd->pos + 1);
  
  // on change le ligne courante
  curInfos->line = selectionBegin->line;
  
  // si cpy == TRUE on le fait déjà
  if (cpy == FALSE) {
    // on change la position du curseur à l'ecran et dans le buff
    curInfos->pos = selectionBegin->pos;
    curInfos->x = selectionBegin->x;
    curInfos->y = selectionBegin->y;
  }
  
  return str;
}

void past() {
  if (selectionBuffer == NULL)
    return;

  char *str = selectionBuffer;
  // on enleve d'abord la selection
  cut(FALSE);
  
  line *current = curInfos->line;
  int pos = curInfos->pos;
  int addY = 0;

  // la selection peut etre composee de plusieurs retour a la ligne
  // entre chaque retour à la ligne on considere que c'est une partie
  // chaque tour de boucle traite une partie de la selection
  // la boucle qui s'arrete quand on a parcouru toute la selection
  while (1) {
    int i = 0;
    // on calcule la taille de cette partie
    while(*(str + i) != '\n' && *(str + i) != '\0') {
      i++;
    }
    
    // c'est une des parties de la selection
    if (*(str + i) == '\n') {
      // creation d'une nouvelle ligne qui contient ce qui se trouve
      // à partir du curseur
      line *new = createLine(current->text + pos);

      // on l'insere juste apres la ligne courante
      insertLineAfter(new, current);
      
      // on copy la partie de la selection dans la ligne courante
      // où se trouve le curseur
      memmove(current->text + pos, str, i);
      *(current->text + pos + i) = '\0';
      str = str + i + 1;
      
      // on recommencera a la position 0 et sur la nouvelle ligne
      current = new;
      pos = 0;
      
      addY += lengthOfLine(current->previous) / COLS;
      addY++;

    } else if (*(str + i) == '\0') { // c'est la derniere partie
      memmove(current->text + pos + i,
              current->text + pos,
              strlen(current->text + pos) + 1);
      memmove(current->text + pos, str, i);
      
      addY += ((pos % COLS) + i) / COLS;
      
      // on change le ligne courante et la postion du curseur dans le buffer
      curInfos->line = current;
      curInfos->pos = pos + i;
      int oldY = curInfos->y;
      curInfos->y += addY;
      if (curInfos->y > LINES - 2) {
        curInfos->y = 0;
        scrollV += oldY + addY;
      }
                  
              char str[10];
  sprintf(str, "%d", addY);
  mvwprintw(bottomWin, 0, 0, str);
            wrefresh(bottomWin);
      curInfos->x = curInfos->pos % COLS;
      break;
    }
  }
}

int lengthOfSelection() {
	if (selectionBegin->line == NULL || selectionEnd->line == NULL)
		return -1;
	
	int length;
	if (selectionBegin->line != selectionEnd->line) {
		length = strlen(selectionBegin->line->text) - selectionBegin->pos;
		length += selectionEnd->pos + 1 + 1;
		
		line *current = selectionBegin->line->next;
		while (current != NULL && current != selectionEnd->line) {
			length += strlen(current->text) + 1;
			current = current->next;
		}
	} else
		length = (selectionEnd->pos - selectionBegin->pos) + 1;
  
  /*if (*(selectionEnd->line->text + selectionEnd->pos) == '\0')
		length--;*/
	
	return length;
}

void removeSelection() {
  selectionBegin->line = NULL;
  selectionEnd->line = NULL;
  // on refait apparaitre le curseur
  curs_set(1);
}
