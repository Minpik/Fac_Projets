#include "interface.h"
#include "globals.h"
#include "keyboard.h"
#include "mouse.h"
#include "files.h"
#include <string.h>
#include <signal.h>

int main() {
  // initialisation ncurses
  initscr();
  noecho();
  //raw();
  keypad(stdscr, TRUE);
  mousemask(BUTTON1_PRESSED | BUTTON1_RELEASED | BUTTON1_CLICKED, NULL);
  // differentes couleurs
	start_color();
	init_pair(1, COLOR_BLACK, COLOR_WHITE);
  init_pair(2, COLOR_RED, COLOR_BLACK);
  init_pair(3, COLOR_GREEN, COLOR_BLACK);
  // permet d'utiliser CTRL+C
	signal(SIGINT, handle);

  mainWin = stdscr;
  wresize(mainWin, LINES - 1, COLS);

  // creation fenetre d'en bas
  bottomWin = newwin(1, COLS, LINES - 1, 0);

  // on est sur la fenetre principale
  curWin = mainWin;

  listLines = createList();
	curInfos = createCursorInfos();
  curInfos->line = listLines->first;
	
	// variables qui stockent les infos de la selection
	selectionBegin = createCursorInfos();
	selectionEnd = createCursorInfos();

  // init buffer de l'invite de commandes
  commandsBuffer = malloc(sizeof(char) * 100);
  commandsBuffer[0] = '\0';
  
  // init des raccourcis
  raccourcisInit();
  
  loadConfig();
  
  // boucle principale
  while(!end) {
    print();
    getChar();
  }

  // on efface la liste
  clearList(listLines);

  free(commandsBuffer);

  raccourcisFree();
  
  if (selectionBuffer != NULL)
    free(selectionBuffer);

  free(curInfos);

  delwin(stdscr);
  endwin();

  return 0;
}

void handle() {
	keyboard(3); // ^X
	print();
}

void getChar() {
  int i = getch();
	keyboard(i);
  mouse(i);
}

void print() {
  printBottomWin();
  if (curWin == mainWin)
    printMainWin();
}

// affichage dans la fenetre principale
void printMainWin() {
  wmove(mainWin, 0, 0);
  werase(mainWin);

    int nbrLines = 0;
    line *current = listLines->first;
    int y = -1;
    int selectionPrinted = FALSE;
    while (current != NULL) {
      int i = scrollH;
      int maxWidthPrinted = FALSE;
			while (1) {
        if ((i - scrollH) % COLS == 0)
          y++;
        
        //if (current == curInfos->line && i == curInfos->pos) {
          //curInfos->x = i % COLS;
          //curInfos->y = y;
        //}
        
        if (!maxWidthPrinted && !selectionPrinted && i >= maxWidth) {
          attron(COLOR_PAIR(2));
          maxWidthPrinted = TRUE;
        }
        
        //if (*(current->text + i) != '\0') {
          // on commence l'affichage de la selection
          if (selectionEnd->line != NULL && current == selectionBegin->line 
                  && i == selectionBegin->pos) {
            attron(COLOR_PAIR(1));
            selectionPrinted = TRUE;
          }
          if (*(current->text + i) != '\0')
            if (y >= scrollV && y <= (LINES - 2) + scrollV)
              waddch(mainWin, *(current->text + i));
          // on termine l'affichage de la selection
          if (current == selectionEnd->line && i == selectionEnd->pos) {
            attroff(COLOR_PAIR(1));
            selectionPrinted = FALSE;
            if (maxWidthPrinted)
              attron(COLOR_PAIR(2));
          }
        //} else
        if (*(current->text + i) == '\0')
          break;
        
				i++;
        if (wrapping &&  (i - scrollH) >= COLS)
          break;
			}
        
      if (current->next != NULL && y >= scrollV && y <= (LINES - 2) + scrollV)
        waddch(mainWin, 10);
        
      nbrLines += (lengthOfLine(current) / COLS) + 1;
      current = current->next;
      
      if (maxWidthPrinted && !selectionPrinted)
        attroff(COLOR_PAIR(2));
    }
    
    attroff(COLOR_PAIR(1));

    int linesDiff = nbrLines - (LINES - 1);
    int scroll = scrollV - linesDiff;
    if (scroll > 0) {
    } else if (scroll < 0) {
      //wscrl(mainWin, scroll);
    }

  wmove(mainWin, curInfos->y, curInfos->x);
  
  // actualisation
  wrefresh(mainWin);
}

// affichage dans la fenetre du bas
void printBottomWin() {
  char str[100];
  werase(bottomWin);
  if (!notice) {
    if (curWin == mainWin) {
      sprintf(str, "line : %d, pos : %d", curInfos->line->num + 1, curInfos->pos);
      //touchwin(bottomWin);
      //mvwin(bottomWin, LINES - 1, strlen(str));
    } else {
      sprintf(str, "%s", commandsBuffer);
    }

    mvwprintw(bottomWin, 0, 0, str);
    
    // actualisation
    wrefresh(bottomWin);
  } else
     notice = FALSE; 
}
