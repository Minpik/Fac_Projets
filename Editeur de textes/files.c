#include <stdio.h>
#include <stdlib.h>
#include "files.h"
#include "globals.h"
#include <string.h>

void loadFile(char *nameFile) {
  list *list = malloc(sizeof(struct list));
  int firstl = 0;
  FILE *fp = NULL;
  char str[100];

  /* opening file for reading */
  fp = fopen(nameFile , "r");
  if(fp == NULL)
		return;

  line *last = NULL; 
  while(fgets (str, 100, fp) !=NULL) {
    line *cur = createLine(str);
    if (firstl == 0){
      list->first = cur;
      firstl++;
    }		
    else {
      insertLineAfter(cur, last);
    }

    last = cur;
  }

	clearList(listLines);
	listLines = list;
	curInfos->line = list->first;
  curInfos->pos = 0;
  curInfos->x = 0;
  curInfos->y = 0;

  fclose(fp);
}

void saveFile(char *nameFile) {
  FILE* fichier = NULL;
  fichier = fopen(nameFile, "w");
  line *cur = listLines->first;
  while (cur != NULL) {
    fputs(cur->text, fichier);
    cur = cur->next;
  }

  fclose(fichier);
}

void loadConfig() {
  FILE *file = fopen("config.txt" , "r");
  if(file == NULL)
		return;
  
  char str[50], variable[10], value[10];
  fgets(str, 60, file);
  
  sscanf(str, "%s %s", variable, value);
  maxWidth = atoi(value);
  
  for (int i = 0; i < NOMBRE_RACCOURCIS; i++) {
    fgets(str, 60, file);
    sscanf(str, "%s %s", variable, value);
    if (i == COPIER)
      strcpy(raccourcis[COPIER], value);
    else if (i == COUPER)
      strcpy(raccourcis[COUPER], value);
    else if (i == COLLER)
      strcpy(raccourcis[COLLER], value);
  }
  
  fgets(str, 60, file);
  sscanf(str, "%s %s", variable, value);
  autoCut = atoi(value);
  
  fclose(file);
}

void saveConfig() {
  FILE* file = NULL;
  file = fopen("config.txt", "w");
  
  fprintf(file, "max_width: %d\n", maxWidth);
  
  for (int i = 0; i < NOMBRE_RACCOURCIS; i++) {
    if (i == COPIER)
      fprintf(file, "copy: %s\n", raccourcis[COPIER]);
    else if (i == COUPER)
      fprintf(file, "cut: %s\n", raccourcis[COUPER]);
    else if (i == COLLER)
      fprintf(file, "paste: %s\n", raccourcis[COLLER]);
  }
  
  fprintf(file, "auto_cut: %d\n", autoCut);
  
  fclose(file);
}
