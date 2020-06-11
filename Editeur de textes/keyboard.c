#include "keyboard.h"
#include "globals.h"
#include "files.h"
#include "selection.h"
#include <ncurses.h>
#include <signal.h>
#include <string.h>

void keyboard(int i) {
  if (curWin == mainWin)
    keyboardMainWin(i);
  else
    keyboardBottomWin(i);
}

void keyboardMainWin(int i) {
  int keyFound = FALSE;
  
  for (int j = 0; j < NOMBRE_RACCOURCIS; j++) {
    if (strcmp(keyname(i), raccourcis[j]) == 0) {
      if (j == COPIER) {
        if (selectionBuffer != NULL)
          free(selectionBuffer);
          selectionBuffer = copy();
      } else if (j == COUPER) {
          if (selectionBuffer != NULL)
            free(selectionBuffer);
          selectionBuffer = cut(TRUE);
      } else if (j == COLLER)
         past();
      keyFound = TRUE;
    }
  }
  
  if (!keyFound) {
    keyFound = TRUE;
    if (i == KEY_UP) {
      // on regarde si on peut monter
      if (curInfos->y > 0 || scrollV > 0) {
        if (curInfos->y == 0) {
          scrollV--;
        } else
          curInfos->y--;

        // c'est la même ligne qu'en haut, curInfos->x ne change pas
        if (curInfos->pos - curInfos->x > 0) {
      curInfos->pos -= (curInfos->x + 1) + ((COLS - 1) - curInfos->x);
        } else {
      // on va à la ligne precedente
      curInfos->line = curInfos->line->previous;

      int length = lengthOfLine(curInfos->line);

      // modulo de la nouvelle ligne
      int newX = length % COLS;

      if (curInfos->x < newX) {
        int q = length / COLS;
        curInfos->pos = (q * COLS) + curInfos->x;
      } else {
        // on se place sur le dernier caractere
        curInfos->pos = length;

        curInfos->x = newX;
      }
        }
      }
    } else if (i == KEY_DOWN) {
      int length = lengthOfText(curInfos->line->text + curInfos->pos);
      // la ligne en dessous est la même que la courante
      if (curInfos->x + length >= COLS) {
        curInfos->y++;
        if (COLS <= length) {
          curInfos->pos += COLS;
        } else {
          curInfos->pos += length;
          curInfos->x = (curInfos->x + length) % COLS;
        }
        // la ligne en dessous est differente de la courante
      } else if (curInfos->line->next != NULL) {
        curInfos->y++;
        int nextLength = lengthOfLine(curInfos->line->next);
        // le curInfos->x plus grand que la ligne, on va donc à la fin
        if (curInfos->x >= nextLength) {
          curInfos->pos = nextLength;
          curInfos->x = nextLength;
        } else
          curInfos->pos = curInfos->x;
          curInfos->line = curInfos->line->next;
      }
      if (curInfos->y > LINES - 2) {
        curInfos->y = LINES - 2;
        scrollV++;
      }
    } else if (i == KEY_RIGHT) {
      if (curInfos->pos < lengthOfLine(curInfos->line)) {
        curInfos->pos++;
        curInfos->x++;
        if (curInfos->x >= COLS) {
          if (wrapping) {
            scrollH++;
            curInfos->x = COLS - 1;
          } else {
            curInfos->x = 0;
            curInfos->y++;
          }
        }
      }
    } else if (i == KEY_LEFT) {
      if (curInfos->pos > 0 && curInfos->x > 0) {
        curInfos->pos--;
        curInfos->x--;
      } else if ((wrapping && scrollH > 0)) {
        scrollH--;
        curInfos->pos--;
      }
    } else if (i == 27) { // echap
      changeCurWin();
      //end = TRUE;
    } else if (i == KEY_BACKSPACE || i == 127) { // backspace
      if (selectionBegin->line != NULL && selectionEnd->line != NULL)
        cut(FALSE);
      else {
        // on verifie qu'on n'est pas au debut du texte
        if (curInfos->pos != 0 || curInfos->line->previous != NULL) {
          // premier caractere de la ligne
          if (curInfos->x - 1 < 0) {
            curInfos->y--;
            if (curInfos->y < 0) {
              curInfos->y = 0;
              scrollV--;
            }

            if (curInfos->pos > 0)
              // tout à droite de la fenetre
              curInfos->x = COLS - 1;
            else
              // modulo de la ligne précedente
              curInfos->x = (lengthOfLine(curInfos->line->previous) % COLS);
          } else
            curInfos->x--;

          if (curInfos->pos > 0) {
            lineRemoveChar(curInfos->line, curInfos->pos - 1);
            curInfos->pos--;
          } else {
            line *previous = curInfos->line->previous;
            int previousLength = lengthOfLine(previous);
            fusion(previous);
            curInfos->line = previous;
            curInfos->pos = previousLength;
          }
        }
      }
    } else if (i == KEY_DC) { // delete
      int length = lengthOfLine(curInfos->line);
      // si on supprime le dernier caractere de la ligne
      // on doit donc la fusionner avec la suivante si elle existe
      if (curInfos->pos == length && curInfos->line->next != NULL)
        fusion(curInfos->line);
      else if (curInfos->pos != length)
        lineRemoveChar(curInfos->line, curInfos->pos);
    } else if (i >= 0 && i <= 127) {
      if (selectionBegin->line != NULL && selectionEnd->line != NULL)
        cut(FALSE);
      if (i == 10) { // entrer
        // on crée une nouvelle ligne avec le texte à droite du curseur
        line *new = createLine(curInfos->line->text + curInfos->pos);

        // on met un caratere de fin de string pour occulter tout ce qui
        // vient après le retour à la ligne
        curInfos->line->text[curInfos->pos] = '\0';

        insertLineAfter(new, curInfos->line);

        // on change de ligne
        curInfos->line = new;

        curInfos->x = 0;
        curInfos->y++;
        curInfos->pos = 0;
      } else {
        lineAddChar(curInfos->line, i, curInfos->pos);
        curInfos->pos++;
        curInfos->x++;
        if (curInfos->x >= COLS) {
          curInfos->x = 0;
          curInfos->y++;
        }
          if (autoCut) {
            if (maxWidth > 0 && lengthOfLine(curInfos->line) > maxWidth)
                lineWrapping(curInfos->line);
          }
      }
      if (curInfos->y > LINES - 2) {
        curInfos->y = LINES - 2;
        scrollV++;
      }
    } else
      keyFound = FALSE;
  }
  
  // on supprime la selection
  if (keyFound)
    removeSelection();
}

void checkCommand() {
  char name[15], infos[20];
  sscanf(commandsBuffer, "%s %[^\n]s", name, infos);

  if (strcmp(name, "quit") == 0) {
    end = TRUE;
  } else if (strcmp(name, "save") == 0) {
    saveFile(infos);
    changeCurWin();
  } else if (strcmp(name, "load") == 0) {
    if (fopen(infos,"r") != NULL){
      loadFile(infos);
      printNotice("Fichier chargé", COLOR_PAIR(3));
    } else
      printNotice("Fichier inconnu", COLOR_PAIR(2));
  } else if (strcmp(name, "set") == 0) {
    int error = FALSE;
    char variable[10], value[10];
    sscanf(infos, "%s %s", variable, value);
    if (strcmp(variable, "max_width") == 0)
      maxWidth = atoi(value);
    else if (strcmp(variable, "copy") == 0)
      strcpy(raccourcis[COPIER], keyname(value[0]));
    else if (strcmp(variable, "cut") == 0)
      strcpy(raccourcis[COUPER], keyname(value[0]));
    else if (strcmp(variable, "paste") == 0)
      strcpy(raccourcis[COLLER], keyname(value[0]));
    else if (strcmp(variable, "auto_cut") == 0) {
      if (atoi(value) == TRUE) {
        autoCut = TRUE;
        cutLines();
      } else
        autoCut = FALSE;
    } else if (strcmp(variable, "wrapping") == 0) {
      wrapping = atoi(value);
    } else
      error = TRUE;
    
    if (!error) {
      saveConfig();
      printNotice("Changement effectué", COLOR_PAIR(3));
    }
  } else if (strcmp(name, "cut") == 0) {
    cutLines();
  } else
    printNotice("Commande inconnue", COLOR_PAIR(2));
}

void keyboardBottomWin(int i) {
  if (i == KEY_UP) {
    wscrl(mainWin, -1);
  } else if (i == KEY_DOWN) {
    wscrl(mainWin, 1);
  } else if (i == 27) { // echap
    changeCurWin();
  } else if (i == KEY_BACKSPACE || i == 127) { // backspace
    int length = strlen(commandsBuffer);
    if (length > 0)
      commandsBuffer[length - 1] = '\0';
  } else if (i == 10) { // entrer
    checkCommand();
    changeCurWin();
  } else if (i >= 0 && i <= 127) {
    int length = strlen(commandsBuffer);
    commandsBuffer[length] = i;
    commandsBuffer[length + 1] = '\0';
  }
}

void changeCurWin() {
  if (curWin == mainWin) {
    curWin = bottomWin;
    commandsBuffer[0] = '\0';
  } else {
    curWin = mainWin;
  }
}

void printNotice(char *value, int attr) {
  werase(bottomWin);
  wattron(bottomWin, attr);
  mvwprintw(bottomWin, 0, 0, value);
  wattroff(bottomWin, attr);
  wrefresh(bottomWin);
  notice = TRUE;
}

void raccourcisInit() {
  raccourcis = malloc(sizeof(char*) * NOMBRE_RACCOURCIS);
  for (int i = 0; i < NOMBRE_RACCOURCIS; i++) {
    raccourcis[i] = malloc(sizeof(char) * 3);
    if (i == COPIER)
      strcpy(raccourcis[i], "^C");
    if (i == COUPER)
      strcpy(raccourcis[i], "^X");
    if (i == COLLER)
      strcpy(raccourcis[i], "^D");
  }
}

void raccourcisFree() {
  for (int i = 0; i < NOMBRE_RACCOURCIS; i++)
    free(raccourcis[i]);
  free(raccourcis);
}

void cutLines() {
    line *current = listLines->first;
    while (current != NULL) {
        if (lengthOfLine(current) > maxWidth)
            lineWrapping(current);
        current = current->next;
    }
}

void lineWrapping(line *line) {
    struct line *current = line;
    int start = FALSE;
    int length = lengthOfLine(current);
    int i = length - 1;
    while (i > 0) {
        if (!start && *(current->text + i) != ' ')
            start = TRUE;
        else if (*(current->text + i) == ' ') {
            if (i <= maxWidth) {
                int diff = 1;
                //if (!start && *(current->text + i) == ' ')
                    //diff = 0;
                if (current->next == NULL) {
                    struct line *new = createLine(line->text + i + diff);
                    insertLineAfter(new, current);
                } else {
                    memmove(current->next->text + (length - i) - 1,
                            current->next->text,
                            lengthOfLine(current->next));
                    memmove(current->next->text,
                            current->text + i + diff,
                            (length - i) - 1);
                }
                if (curInfos->pos > i) {
                    curInfos->line = curInfos->line->next;
                    curInfos->pos = curInfos->pos - (i + diff);
                    curInfos->x = curInfos->pos;
                    curInfos->y++;
                }
                current->text[i + diff] = '\0';
                break;
            }
        }
        i--;
    }
    //}
}
