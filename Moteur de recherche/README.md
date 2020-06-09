# Corpus

Nous avons fait réaliser plusieurs corpus (ou thème) pour notre moteur de recherche. Ceux-ci sont disponibles à l'adresse suivante :
https://drive.google.com/open?id=1LVqLHTTjeIsLHQ1jM5QciGzAU52Y8cCj

Vous pouvez également avoir votre propre corpus. Les indications en dessous vous aiderons à changer de thème.

# Etape 1 : calcul

Pour faire les calculs, vous devez vous trouver dans le repertoire principal, et faire :
javac Projet.java
java Projet

Le thème de notre moteur de recherche est le cinema.
Vous pouvez changer le thème du moteur de recherche pour les calculs en ajoutant en argument le titre de votre .xml (le format .xml est le seul format accepté).
Par exemple si vous avez un ensemble de page qui s'appelle "diderot.xml" alors il suffira de faire :
java Projet diderot

Il se peut que nos calculs soit assez lourd. Si vous avez un problème, vous pouvez lancer le programme sur lulu en ajoutant :
java -Xmx50g Projet
Tous les resultats générés nécessaires pour lancer le serveur sont sous format .txt.

# Etape 2 : le serveur

Pour lancer le serveur, il faut d'abord avoir effectué la première étape au-dessus. Ensuite, il faut se trouver dans le repetoire serveur et lancer la commande :
node serveur.js

Si vous voulez changer de thème comme à la première étape, il faudra également le faire pour le serveur. Pour le faire, rien de bien compliqué, vous ajoutez en argument de la comande le nom du fichier .xml. Par exemple si dans l'étape précédente, vous ave pris le thème "diderot.xml", alors il vous suffira de faire :
node serveur.js diderot

Une fois la commande effectué, un message affichera sur la ligne de commande qui indiquera que le serveur est lancé.
Vous pourrez donc acceder au serveur à l'adresse localhost:8080

Une fois sur le site, les recherches se font en remplissant la barre de recherche avec la recherche souhaitée et en appuyant sur la touche Entrer.
