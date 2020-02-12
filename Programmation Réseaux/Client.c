#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>
#include <ctype.h>

char *littleIndian(int size, int number) {
	char *str = calloc(sizeof(char), size + 1);
	char string_number[10];
	sprintf(string_number,"%d", number);
	strcat(str + (size - strlen(string_number)), string_number);

	return str;
}

int isDigit(char* s){
  for(int i = 0; i < strlen(s);i++){
    if (!isdigit(s[i])){
      return 0;
    }
  }
  return 1;
}

int isAllspace(char* s){
  for(int i = 0; i < strlen(s);i++){
    if (!(s[i] == ' ')){
      return 0;
    }
  }
  return 1;
}	


int isPhrase(char* s){
  if(isAllspace(s)){
    return 0;

  }
  for(int i = 0; i < strlen(s);i++){
    if ((!isalnum(s[i])) && (!(s[i] == ' '))){
      return 0;
    }
  }
  if(strlen(s) >1){
    return 1;
  }
  else
    return 0;
}


int isPseudoValid(char* s){
  for(int i = 0; i < strlen(s);i++){
    if (!isalnum(s[i])){
      return 0;
    }
  }

  if(strlen(s) < 9 && strlen(s) >1){
    return 1;
  }
  else
    return 0;
}

void *ecouteUDPA(void *arg) {
  //exit(0);
  char *ipPort =((char *)arg);

  char ip[20];
  char porttmp[10];
  sscanf(ipPort, "%s %s", ip, porttmp);
  int port = atoi(porttmp);
  free(ipPort);
  int sock=socket(PF_INET,SOCK_DGRAM,0);
  int ok=1;
  int r=setsockopt(sock,SOL_SOCKET,SO_REUSEPORT,&ok,sizeof(ok));
  struct sockaddr_in address_sock;
  address_sock.sin_family=AF_INET;
  address_sock.sin_port=htons(port);
  address_sock.sin_addr.s_addr=htonl(INADDR_ANY);
  r=bind(sock,(struct sockaddr *)&address_sock,sizeof(struct sockaddr_in));
  struct ip_mreq mreq;
  mreq.imr_multiaddr.s_addr=inet_addr(ip);
  mreq.imr_interface.s_addr=htonl(INADDR_ANY);
  r=setsockopt(sock,IPPROTO_IP,IP_ADD_MEMBERSHIP,&mreq,sizeof(mreq));
  if(r==0){
    char tampon[100];
    while(1){
      int rec=recv(sock,tampon,100,0);
      tampon[rec]='\0';
      if(strcmp(strtok(tampon," "),"MESA") == 0 ){
        char *tokenId;
        char *tokenMes;

        for (int i = 0; i < 1; i++) {
          tokenId = strtok(NULL, " ");
        }

        for (int i = 0; i < 1; i++) {
          tokenMes = strtok(NULL, " ");
        }
        char tmp[strlen(tokenMes)-3];
        memcpy(tmp,&tokenMes[0],strlen(tokenMes)-3);
        printf("MESSAGE MULTIPLE DE %s :\"%s\"\n", tokenId, tmp);
      }
      else if(strcmp(tampon, "SCOR") == 0 ) {
        char *tokenId;
        char *p;
        char *x;
        char *y;

        for (int i = 0; i < 1; i++) {
          tokenId = strtok(NULL, " ");
        }

        for (int i = 0; i < 1; i++) {
          p = strtok(NULL, " ");
        }
	for (int i = 0; i < 1; i++) {
          x = strtok(NULL, " ");
        }
	for (int i = 0; i < 1; i++) {
          y = strtok(NULL, " ");
        }
        char tmp[strlen(y)-3];
        memcpy(tmp,&y[0],strlen(y)-3);
        printf("MESSAGE MULTIPLE Player : %s a mangé un fantome !\nIl a %s point le fantome etait a la position x: %s et y: %s.\n", tokenId, p, x, tmp);

      }

      else if(strcmp(tampon, "FANT") == 0 ){
        char *x;
        char *y;

        for (int i = 0; i < 1; i++) {
          x = strtok(NULL, " ");
        }

        for (int i = 0; i < 1; i++) {
          y = strtok(NULL, " ");
        }
        char tmp[strlen(y)-3];
        memcpy(tmp,&y[0],strlen(y)-3);
        printf("MESSAGE MULTIPLE Un fantome c'est deplacé il est a la pos x : %s et y : %s.\n", x, tmp);
      }

      else if(strcmp(tampon, "END") == 0 ){
        char *id;
        char *p;

        for (int i = 0; i < 1; i++) {
          id = strtok(NULL, " ");
        }

        for (int i = 0; i < 1; i++) {
          p = strtok(NULL, " ");
        }
        char tmp[strlen(p)-3];
        memcpy(tmp,&p[0],strlen(p)-3);
        printf("MESSAGE MULTIPLE La parte est finie, %s est le vainqueur son score est de %s. \nUtilisez QUIT pour quitter la partie.\n", id, tmp);
      }
    }
  }
  
  return NULL;
}
void ecouteUDPP(void *arg) {
  //exit(0);
  int *ipPort =((int *)arg);
  int sock=socket(PF_INET,SOCK_DGRAM,0);
  sock=socket(PF_INET,SOCK_DGRAM,0);
  struct sockaddr_in address_sock;
  address_sock.sin_family=AF_INET;
  address_sock.sin_port=htons(*ipPort);
  address_sock.sin_addr.s_addr=htonl(INADDR_ANY);
  int r=bind(sock,(struct sockaddr *)&address_sock,sizeof(struct
                                                          sockaddr_in));
  if(r==0){
    char tampon[100];
    while(1){
      int rec=recv(sock,tampon,100,0);
      tampon[rec]='\0';
      if(strcmp(strtok(tampon," "),"MESP") == 0 ){
        char *tokenId;
        char *tokenMes;

        for (int i = 0; i < 1; i++) {
          tokenId = strtok(NULL, " ");
        }

        for (int i = 0; i < 1; i++) {
          tokenMes = strtok(NULL, " ");
        }
        char tmp[strlen(tokenMes)-3];
        memcpy(tmp,&tokenMes[0],strlen(tokenMes)-3);
        printf("MESSAGE UDP DE %s : \"%s\"\n", tokenId, tmp);
      }
    }
  } 
}



void ecouteTCP(){
  struct sockaddr_in adress_sock;
  adress_sock.sin_family = AF_INET;
  adress_sock.sin_port = htons(4242);
  inet_aton("127.0.0.1",&adress_sock.sin_addr);
  int descr=socket(PF_INET,SOCK_STREAM,0);
  int r=connect(descr,(struct sockaddr *)&adress_sock,
                sizeof(struct sockaddr_in));
  char* id = "";
  int etat = 0;
  int seed = time(NULL);
  srand(seed);
  int ip = rand()%(8000-3000) +3000;

  void text (){
    while(1){
      if(etat == 0){
        printf("\n-Tapez NEW plus votre pseudo pour créer une nouvelle partie\n-Tapez REG plus votre pseudo plus le numéro de la partie pour rejoindre une partie\n");
      }
      if(etat <= 1){
        printf("\n-Tapez UNREG pour vous desinscrire\n-Tapez SIZE plus le numéro de la partie pour connaitre la taille\n-Tapez LIST plus le numéro de la partie pour connaitre la liste des joueurs\n-Tapez GAMES pour connaitre la liste des games non commencé\n");
      }
      if(etat == 1){
        printf("-Tapez START pour indiquer que vous etes pret a jouer en attendant les autres joueurs\n");
      }

      if(etat == 2){
        printf("\n\n-Tapez UP suivi du nombre de case que vous voulez vous deplacer\n-Tapez DOWN suivi du nombre de case que vous voulez vous deplacer\n-Tapez LEFT suivi du nombre de case que vous voulez vous deplacer\n-Tapez RIGT suivi du nombre de case que vous voulez vous deplacer\n-Tapez QUIT pour abandooner la partie\n-Tapez GLIST pour avoir la liste des joueur de la partie\n-Tapez ALL suivi d'un message pour envoyer un message a tout les joueurs\n-Tapez SEND suivi de l'id d'un joueur puis d'un message pour enoyer un message au joueur a l'id correspondant\n\n");
      }
      
      char mess[100];
      fgets(mess, 100, stdin);
      mess[strlen(mess) -1] = '\0';
      char arg1[10], arg2[10], arg3[10];
      sscanf(mess, "%s %s %s", arg1, arg2, arg3);
      if(etat == 0){
        if(strcmp("NEW", arg1) == 0){
          if(isPseudoValid(arg2)){
            id = malloc(strlen(arg2));
            strcpy(id,arg2);
            char ipc[20];
            snprintf (ipc, sizeof ipc, "%d",ip);
            char *new_str =malloc(sizeof(char) * (strlen(arg1) + strlen(id) + strlen(ipc) + 6));
        
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str,id);
            strcat(new_str," ");
            strcat(new_str, ipc);
            //strcat(new_str,atoi(const char *nptr); ); 
            strcat(new_str,"***\n"); 

            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
          else{
            printf("Erreur Syntaxe NEW \n\n");
          }
        }

        else if(strcmp("REG", arg1) == 0){
          if(isPseudoValid(arg2) && isDigit(arg3)){
            int seed = time(NULL);
            srand(seed);
            int ip = rand()%(8000-3000) +3000; 
            char ipc[20];
            snprintf (ipc, sizeof ipc, "%d",ip);
            id = malloc(strlen(arg2));
            strcpy(id,arg2);
            char *new_str =malloc(sizeof(char) * (strlen(arg1) + strlen(id) + strlen(ipc) + strlen(arg3)  + 7));
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str, id);
            strcat(new_str," ");
            strcat(new_str,ipc);
            strcat(new_str," ");
            strcat(new_str,arg3);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
          else{
            printf("Erreur Syntaxe REG\n\n");
          } 
        }
      }
      if(etat <= 1){
        if(strcmp("UNREG", arg1) == 0){
          send(descr,"UNREG***\n",10,0);
          break;
        }

        else if(strcmp("SIZE", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 5));
            strcpy(new_str,arg1);
            strcat(new_str,"? ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("LIST", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 5));
            strcpy(new_str,arg1);
            strcat(new_str,"? ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }

        else if(strcmp("GAMES", arg1) == 0){
          send(descr,"GAMES?***\n",10,0);
          break;
        }
      }
      if(etat == 1){
        if(strcmp("START", arg1) == 0){
	  printf("La partie commence attendez les autres joueurs\n");
          send(descr,"START***\n",9,0);
          break;
        }
      }
      if(etat == 2){
        if(strcmp("UP", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 4));
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("DOWN", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 4));
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("LEFT", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 4));
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("RIGHT", arg1) == 0){
          if(isDigit(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2)  + 4));
            strcpy(new_str,arg1);
            strcat(new_str," ");
            strcat(new_str,arg2);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("QUIT", arg1) == 0){
          send(descr,"QUIT***\n",9,0);
          break;
        }
        else if(strcmp("GLIST", arg1) == 0){
          send(descr,"GLIST?***\n",10,0);
          break;
        }
        else if(strcmp("ALL", arg1) == 0){
          char tmp[strlen(mess)-4];
          memcpy(tmp,&mess[4],strlen(mess));
          if(isPhrase(tmp)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(tmp)  + 5));
            strcpy(new_str,arg1);
            strcat(new_str,"? ");
            strcat(new_str,tmp);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
        else if(strcmp("SEND", arg1) == 0){
          char tmp[strlen(mess)-(6 +strlen(arg2))];
          memcpy(tmp,&mess[6 + strlen(arg2)],strlen(mess));
          if(isPhrase(tmp) && isPseudoValid(arg2)){
            char *new_str =malloc(sizeof(char) * (strlen(arg1)+ strlen(arg2) + strlen(tmp)  + 5));
            strcpy(new_str,arg1);
            strcat(new_str,"? ");
            strcat(new_str,arg2);
            strcat(new_str," ");
            strcat(new_str,tmp);
            strcat(new_str,"***\n");
            send(descr,new_str,strlen(new_str),0);
            free(new_str);
            break;
          }
        }
      }
      
      printf("Mauvaise commande \n");
  
    }
  }


  while(r!=-1){

    char buff[100];
    int size_rec=recv(descr,buff,99*sizeof(char),0);
    buff[size_rec]='\0';
    
    if(strcmp(strtok(buff," "),"GAMES") == 0 ) {
      char *token;
      for (int i = 0; i < 1; i++) {
        token = strtok(NULL, " ");
      }
      char tmp[strlen(token)-3];
      if(strlen(token) > 7){
        memcpy(tmp,&token[0],strlen(token)-7);
      }
      else {
	
        memcpy(tmp,&token[0],strlen(token)-3);
      }

      printf("Bienvenu dans LABYRANTHE\n\nIl y a actuellement %s parties disponibles\n",tmp);

      for(int i = 0; i< atoi(token); i++){
        char* num;
        char* nbj;
        for(int j = 0; j < 1; j++){
          num = strtok(NULL, " ");
        }
        for(int j = 0; j < 1; j++){
          nbj = strtok(NULL, " ");
        }
        char tmp2[strlen(nbj)-3];
	if(i == atoi(token) -1){
          memcpy(tmp2,&nbj[0],strlen(nbj)-3);
        }
	else
          memcpy(tmp2,&nbj[0],strlen(nbj)-7);
        printf("Partie numéro: %s nombre de joeurs: %s\n", num, tmp2);

      }
      text();
    }
    else if(strcmp(buff, "REGOK") == 0 ) {
      char *num;
      for (int i = 0; i < 1; i++) {
        num = strtok(NULL, " ");
      }
      char num_partie[strlen(num)-3];
      memcpy(num_partie,&num[0],strlen(num)-3);
      int n = atoi(num);
      printf("Vous etes inscrit dans la partie %d \n", n);
      etat = 1;	
      text();
	
    }

    else if(strcmp(buff, "REGNO***") == 0 ) {
      printf("Votre inscription a été refusé \n");
      text();
	
    }

    else if(strcmp(buff, "UNREGOK") == 0 ) {
      char *num;
      for (int i = 0; i < 1; i++) {
        num = strtok(NULL, " ");
      }
      char num_partie[strlen(num)-3];
      memcpy(num_partie,&num[0],strlen(num)-3);
      int n = atoi(num);
      printf("Vous etes déinscrit de la partie %d \n", n);
      etat = 0;	
      text();
	
    }
    else if(strcmp(buff, "DUNNO***") == 0 ) {
      printf("Opération non autorisée \n");
      text();
	
    }

    else if(strcmp(buff, "SIZE!") == 0 ) {
      char *partie;
      char *hauteur;
      char *largeur;
      for (int i = 0; i < 1; i++) {
        partie = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        hauteur = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        largeur = strtok(NULL, " ");
      }
      char str[strlen(largeur)-3];
      memcpy(str,&largeur[0],strlen(largeur)-3);
      printf("La partie numéro %s est de hauteur %s et de largeur %s \n", partie, hauteur, str);
      text();
	
    }

    else if(strcmp(buff, "LIST!") == 0 ) {
      char *nbp;
      char* nb;

      for (int i = 0; i < 1; i++) {
        nbp = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        nb = strtok(NULL, " ");
      }
      char tmp[strlen(nb)-10];
      memcpy(tmp,&nb[0],strlen(nb)-10);


      printf("Dans la partie numero %s il y a %s joueur(s)\n",nbp,tmp);

      for(int i = 0; i< atoi(nb); i++){
        char* idj;
        for(int j = 0; j < 1; j++){
          idj = strtok(NULL, " ");
        }
        char tmp2[strlen(idj)-3];
	if(i == atoi(nb) -1){
          memcpy(tmp2,&idj[0],strlen(idj)-3);
        }
	else
          memcpy(tmp2,&idj[0],strlen(idj)-10);
        printf("PLAYER : %s\n", tmp2);

      }
      text();
    }

   
    else if(strcmp(buff, "WELCOME") == 0 ) {
      char *m;
      char *h;
      char *w;
      char *f;
      char *tokenip;
      char *tokenport;

      for (int i = 0; i < 1; i++) {
        m = strtok(NULL, " ");
      }

      for (int i = 0; i < 1; i++) {
        h = strtok(NULL, " ");
      }

      for (int i = 0; i < 1; i++) {
        w = strtok(NULL, " ");
      }

      for (int i = 0; i < 1; i++) {
        f = strtok(NULL, " ");
      }

      for (int i = 0; i < 1; i++) {
        tokenip = strtok(NULL, " ");
      }

      for (int i = 0; i < 1; i++) {
        tokenport = strtok(NULL, " ");
      }
      //exit(0);
      char str[strlen(tokenport)-3];
      memcpy(str,&tokenport[0],strlen(tokenport)-3);
      char *tmp = malloc(sizeof(char) * (strlen(tokenip)+strlen(str)+2));
      strcpy(tmp,tokenip);
      strcat(tmp," ");
      strcat(tmp,str);

      pthread_t th1;
      pthread_t th2;
      pthread_create(&th1, NULL, ecouteUDPA, tmp);
      pthread_create(&th2, NULL, (void * (*)(void*))ecouteUDPP,&ip );
      etat = 2;
      printf("Bienvenue dans la partie numéro %s de hauteur %s et de largeur %s.\nIl y a %s fantomes atrrapez les !\n",m, h, w, f);
      text();

    }

    else if(strcmp(buff, "POS") == 0 ) {
      char *idj;
      char *x;
      char *y;
      for (int i = 0; i < 1; i++) {
        idj = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        x = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        y = strtok(NULL, " ");
      }
      char str[strlen(y)-3];
      memcpy(str,&y[0],strlen(y)-3);
      printf("Player: %s est a la position x : %s et y: %s \n", idj, x, str);
      text();
	
    }

    else if(strcmp(buff, "MOV") == 0 ) {
      char *x;
      char *y;
      for (int i = 0; i < 1; i++) {
        x = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        y = strtok(NULL, " ");
      }
      char str[strlen(y)-3];
      memcpy(str,&y[0],strlen(y)-3);
      printf("Vous etes maintenant a la position x : %s et y: %s \n", x, str);
      text();
	
    } 

    else if(strcmp(buff, "MOF") == 0 ) {
      char *x;
      char *y;
      char *p;
      for (int i = 0; i < 1; i++) {
        x = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        y = strtok(NULL, " ");
      }
      for (int i = 0; i < 1; i++) {
        p = strtok(NULL, " ");
      }
      char str[strlen(p)-3];
      memcpy(str,&p[0],strlen(p)-3);
      printf("Vous avez capturé un fantome !\nVous etes maintenant a la position x : %s et y: %s et vous avez %s point ! \n", x, y, str);
      text();
	
    }    

    else if(strcmp(buff,"BYE***") == 0 ) {
      printf("Partie terminer a bientot.\n");
      break;
    }

    else if(strcmp(buff, "GLIST!") == 0 ) {
      char *s;

      for (int i = 0; i < 1; i++) {
        s = strtok(NULL, " ");
      }
      char tmp[strlen(s)-10];
      memcpy(tmp,&s[0],strlen(s)-11);


      printf("Il y a %s joueur(s) dans la partie.\n",tmp);

      for(int i = 0; i< atoi(s); i++){
        char* idj;
        char* x;
        char* y;
        char* p;        
        for(int j = 0; j < 1; j++){
          idj = strtok(NULL, " ");
        }
	for(int j = 0; j < 1; j++){
          x = strtok(NULL, " ");
        }
	for(int j = 0; j < 1; j++){
          y = strtok(NULL, " ");
        }
	for(int j = 0; j < 1; j++){
          p = strtok(NULL, " ");
        }
        char tmp2[strlen(p)-3];
	if(i == atoi(s) -1){
          memcpy(tmp2,&p[0],strlen(p)-3);
        }
	else
          memcpy(tmp2,&p[0],strlen(p)-11);
        printf("PLAYER : %s position x: %s position y: %s et a %s point.\n", idj, x, y, tmp2);

      }
      text();
    }

    else if(strcmp(buff,"ALL!***") == 0 ) {
      printf("Votre message a bien été envoyé.\n");
      text();
    }

    else if(strcmp(buff,"SEND!***") == 0 ) {
      printf("Votre message a bien été envoyé.\n");
      text();
    }

    else if(strcmp(buff,"NOSEND!***") == 0 ) {
      printf("Votre message ne peut pas etre envoyé.\n");
      text();
    }

    else{
      text();
    }
  }
}


int main() {
  
  ecouteTCP();

  return 0;
}
