#include "mouse.h"
#include "selection.h"

void mouse(int i) {  
  MEVENT event;
  
  if (i == KEY_MOUSE) {
    if (getmouse(&event) == OK) {
      // on check si le cursor est dans la fenetre principale
			if (event.y <= LINES - 2) {
        if (event.bstate & BUTTON1_PRESSED) {
          // sauvegade des infos du curseur au moment "pressed"
          getCursorInfos(&event, selectionBegin);
          // on recopie dans curInfos car à ce moment curInfos et selectionBegin
          // doivent etre les mêmes
          //cursorCpy(curInfos, selectionBegin);
          
          selectionEnd->line = NULL;
        } else if (event.bstate & BUTTON1_RELEASED) {
          if (selectionBegin->line != NULL) {
            // sauvegade des infos du curseur au moment "released"
            getCursorInfos(&event, selectionEnd);
            // on recopie dans curInfos car à ce moment curInfos et selectionEnd
            // doivent etre les mêmes
            //cursorCpy(curInfos, selectionEnd);
            // on echange les valeurs si necessaire pour que selectionBegin
            // soit le debut de la selection et selectionEnd la fin
            if (cursorCmp(selectionEnd, selectionBegin) == -1) {
              cursorInfos *tmp = selectionBegin;
              selectionBegin = selectionEnd;
              selectionEnd = tmp;
            }
            
            // on evite d'avoir ces caracteres dans une selection
            if ((*(selectionEnd->line->text + selectionEnd->pos) == '\n'
              || *(selectionEnd->line->text + selectionEnd->pos) == '\0')
                && selectionEnd->pos > 0) {
              selectionEnd->pos--;
            }
            
            // on rend le curseur invisible
            curs_set(0);
          }
        } else if (event.bstate & BUTTON1_CLICKED) {
          getCursorInfos(&event, curInfos);
          removeSelection();
        }
      }
    }
  }
}

void getCursorInfos(MEVENT *event, cursorInfos *infos) {
  int y = 0; // la position sur l'ecran de la ligne courante
  int length = 0;
  line *current = listLines->first;
  
  
  int i = 0;
  int x = 0;
  while (current != NULL) {
    x = 0;
    while (1) {
      if (x % COLS == 0)
        i++;
      
      if (i > scrollV)
        break;
       
      if (*(current->text + x) == '\0')
        break;
      x++;
    }
    if (i > scrollV)
      break;
    current = current->next;
  }

  int first = TRUE;
  // on parcourt toutes les lignes
  while (current != NULL) {
    length = lengthOfLine(current);

    if (first) {
      length -= x;
      first = FALSE;
    }
    int n = length / COLS;
    // on arrete si le curseur se trouve dans la ligne courante
    if (y + n >= event->y)
      break;
    
    x = 0;

    if (current->next != NULL)
      current = current->next;
    else // pas de ligne suivante, on arrête
      break;

    y += n + 1;
  }

  int n = ((event->y - y) * COLS) + event->x;
  if (n >= length) {
    infos->x = length % COLS;
    infos->y = (current->next != NULL) ? event->y : y + (length / COLS);
  } else {
    infos->x = event->x;
    infos->y = event->y;
  }
  
  /*int chars = ((event->y - y) * COLS) + infos->x;
	int i = 0, count = 0;
	while (*(current->text + i) != '\0') {
    if (count >= chars)
	break;
    count += (*(current->text + i) != '\t') ? 1 : 4;
    i++;
	}
  
	infos->x = count % COLS;
	infos->pos = count;*/
  infos->pos = ((infos->y - y) * COLS) + infos->x + x;
  infos->line = current;

  /*char str[10];
  sprintf(str, "%d", event->y);
  mvwprintw(bottomWin, 0, 0, str);*/
}
