DO $$
    BEGIN
        FOR i IN 1..10
            LOOP
                EXECUTE FORMAT('INSERT INTO Utilisateur(pseudo, nom,' ||
                               'prenom, email, mdp)
                    VALUES (''pseudo%s'', ''nom%s'', ''prenom%s'',' ||
                               '''test%s@hotmail.fr'', ''123456'');',
                               i, i, i, i);
            END LOOP;
    END $$;

INSERT INTO Utilisateur(pseudo, nom, prenom, email, mdp)
VALUES ('rhodier', 'pierre', 'rhodier', 'rhodier@hotmail.fr', 123456);

INSERT INTO Ami
VALUES (1, 2),
       (1, 3),
       (2, 4),
       (2, 6),
       (3, 4),
       (3, 8),
       (5, 8),
       (6, 3),
       (10, 5),
       (10, 8),
       (4, 11);

INSERT INTO Categorie(nom)
VALUES ('Medical'),
       ('Voyage'),
       ('Association'),
       ('Education'),
       ('Loisir'),
       ('Autre');

INSERT INTO Projet(nom, id_categorie, id_createur, objectif, description,
                   date_debut, date_fin)
VALUES ('projet1', 1, 1, 100, 'description', current_timestamp,
        current_timestamp + INTERVAL '1' DAY ),
       ('projet2', 2, 2, 5000, 'description', current_timestamp,
        current_timestamp + INTERVAL '2' DAY),
       ('projet3', 2, 2, 400, 'description', current_timestamp,
        current_timestamp + INTERVAL '3' DAY),
       ('projet4', 2, 2, 150,  'description', current_timestamp,
        current_timestamp + INTERVAL '4' DAY),
       ('projet5', 5, 5, 3333, 'description', current_timestamp,
        current_timestamp + INTERVAL '5' DAY),
       ('projet6', 6, 6, 250, 'description', current_timestamp,
        current_timestamp + INTERVAL '6' DAY);

INSERT INTO Don(id_projet, id_utilisateur, valeur, message, date)
VALUES (1, 1, 10, 'don1', now()),
       (4, 2, 25, 'don1', now()),
       (5, 3, 100, 'don1', now()),
       (3, 4, 5, 'don1', now()),
       (2, 10, 142, 'don1', now()),
       (6, 5, 2, 'don1', now()),
       (2, 7, 44, 'don1', now()),
       (6, 7, 5, 'don2', now()),
       (4, 6, 100, 'don1', now()),
       (1, 1, 80, 'don2', now());

INSERT INTO Fan
VALUES (6, 4),
       (2, 6),
       (1, 1),
       (5, 1),
       (2, 4),
       (4, 2),
       (6, 9),
       (5, 6),
       (3, 10),
       (1, 8),
       (1, 11),
       (5, 11);

INSERT INTO Commentaire_projet(id_projet, id_utilisateur, valeur, date)
VALUES (1, 1, 'comment1', current_timestamp),
       (3, 6, 'comment1', current_timestamp),
       (6, 8, 'comment1', current_timestamp),
       (2, 1, 'comment2', current_timestamp),
       (4, 6, 'comment2', current_timestamp),
       (5, 3, 'comment1', current_timestamp),
       (6, 6, 'comment3', current_timestamp),
       (3, 10, 'comment1', current_timestamp),
       (6, 9, 'comment1', current_timestamp),
       (1, 8, 'comment1', current_timestamp);

INSERT INTO News(id_projet, valeur, date)
VALUES (5, 'news1', current_timestamp),
       (5, 'news2', current_timestamp),
       (4, 'news1', current_timestamp),
       (6, 'news1', current_timestamp),
       (1, 'news1', current_timestamp),
       (2, 'news1', current_timestamp),
       (2, 'news2', current_timestamp),
       (2, 'news3', current_timestamp),
       (3, 'news1', current_timestamp);

INSERT INTO Commentaire_news(id_news, id_utilisateur, valeur, date)
VALUES (1, 1, 'comment1', current_timestamp),
       (5, 1, 'comment2', current_timestamp),
       (3, 2, 'comment1', current_timestamp),
       (1, 3, 'comment1', current_timestamp),
       (4, 4, 'comment1', current_timestamp),
       (8, 4, 'comment2', current_timestamp),
       (2, 6, 'comment1', current_timestamp),
       (7, 6, 'comment2', current_timestamp),
       (4, 7, 'comment1', current_timestamp),
       (9, 10, 'comment1', current_timestamp);

INSERT INTO Recompense(id_projet, description, seuil)
VALUES (1, 'recompense1', 50),
       (1, 'recompense2', 100),
       (1, 'recompense3', 200),
       (2, 'recompense1', 15),
       (2, 'recompense2', 30),
       (2, 'recompense3', 50),
       (2, 'recompense4', 100),
       (5, 'recompense1', 20),
       (5, 'recompense2', 70),
       (6, 'recompense1', 150);

INSERT INTO Association(nom, site)
VALUES ('Petits Princes', 'https://www.petitsprinces.com'),
       ('Toutes à l’école', 'http://www.toutes-a-l-ecole.org'),
       ('Ange Hélène', 'http://www.angehelene.org'),
       ('Handicap International', 'http://www.handicap-international.fr'),
       ('Fondation recherche médicale', 'https://www.frm.org'),
       ('Un cadeau pour la vie', 'http://www.uncadeaupourlavie.fr'),
       ('Pour un sourire d’enfant', 'http://www.pse.ong'),
       ('Perce-Neige', 'http://www.perce-neige.org');

INSERT INTO Date(annee, mois, jour)
VALUES (extract(YEAR FROM now()), extract(MONTH FROM now()),
        extract(DAY FROM now()));