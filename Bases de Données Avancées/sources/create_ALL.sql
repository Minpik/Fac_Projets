DROP TABLE If exists Utilisateur CASCADE;
DROP TABLE If exists Ami;
DROP TABLE If exists Date;
DROP TABLE If exists Projet CASCADE;
DROP TABLE If exists Don;
DROP TABLE If exists Fan;
/*DROP TABLE If exists Objectif CASCADE;*/
DROP TABLE If exists Recompense;
DROP TABLE If exists Commentaire_projet;
DROP TABLE If exists News CASCADE;
DROP TABLE If exists Commentaire_news;
DROP TABLE If exists Categorie CASCADE;
DROP TABLE If exists Association CASCADE;

CREATE TABLE IF NOT EXISTS Utilisateur
(
    id     SERIAL PRIMARY KEY,
    pseudo VARCHAR(100) NOT NULL UNIQUE,
    nom    VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email  VARCHAR(100) NOT NULL UNIQUE,
    mdp    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Ami
(
    id_user_1 INTEGER CHECK (id_user_1 != id_user_2)
        REFERENCES Utilisateur (id) ON DELETE CASCADE               NOT NULL,
    id_user_2 INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (id_user_1, id_user_2)
);

CREATE TABLE IF NOT EXISTS Categorie
(
    id  SERIAL PRIMARY KEY,
    nom varchar(100) NOT NULL
);

/* E : en cours de financement, F : financé, T : terminé */
CREATE TABLE IF NOT EXISTS Projet
(
    id           SERIAL PRIMARY KEY,
    etat         CHAR DEFAULT 'E' CHECK (etat IN ('E', 'F', 'T'))      NOT NULL,
    nom          VARCHAR(100)                                          NOT NULL,
    id_categorie INTEGER REFERENCES Categorie (id) ON DELETE CASCADE   NOT NULL,
    id_createur  INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    objectif     INTEGER CHECK (objectif > 0)                          NOT NULL,
    description  text                                                  NOT NULL,
    date_debut   TIMESTAMP CHECK (date_debut < date_fin)               NOT NULL,
    date_fin     TIMESTAMP                                             NOT NULL
);

CREATE TABLE IF NOT EXISTS Don
(
    id             SERIAL PRIMARY KEY,
    id_projet      INTEGER REFERENCES Projet (id),
    id_utilisateur INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    valeur         SMALLINT CHECK (valeur > 0),
    message        varchar(255),
    date           TIMESTAMP                                             NOT NULL
);

CREATE TABLE IF NOT EXISTS Fan
(
    id_projet      INTEGER REFERENCES Projet (id) ON DELETE CASCADE      NOT NULL,
    id_utilisateur INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (id_projet, id_utilisateur)
);

CREATE TABLE IF NOT EXISTS Commentaire_projet
(
    id             SERIAL PRIMARY KEY,
    id_projet      INTEGER REFERENCES Projet (id) ON DELETE CASCADE      NOT NULL,
    id_utilisateur INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    valeur         text                                                  NOT NULL,
    date           TIMESTAMP                                             NOT NULL
);

CREATE TABLE IF NOT EXISTS News
(
    id        SERIAL PRIMARY KEY,
    id_projet INTEGER REFERENCES Projet (id) ON DELETE CASCADE NOT NULL,
    valeur    text                                             NOT NULL,
    date      TIMESTAMP                                        NOT NULL
);

CREATE TABLE IF NOT EXISTS Commentaire_news
(
    id             SERIAL PRIMARY KEY,
    id_news        INTEGER REFERENCES News (id) ON DELETE CASCADE        NOT NULL,
    id_utilisateur INTEGER REFERENCES Utilisateur (id) ON DELETE CASCADE NOT NULL,
    valeur         text                                                  NOT NULL,
    date           TIMESTAMP                                             NOT NULL
);

/*CREATE TABLE IF NOT EXISTS Objectif
(
    id        SERIAL PRIMARY KEY,
    id_projet INTEGER REFERENCES Projet (id) ON DELETE CASCADE NOT NULL,
    seuil     INTEGER CHECK (seuil > 0)                        NOT NULL,
    atteint   BOOLEAN DEFAULT FALSE                            NOT NULL
);*/

CREATE TABLE IF NOT EXISTS Recompense
(
    id          SERIAL PRIMARY KEY,
    id_projet   INTEGER REFERENCES Projet (id) ON DELETE CASCADE NOT NULL,
    description text                                             NOT NULL,
    seuil       INTEGER CHECK (seuil > 0 )                       NOT NULL
);

CREATE TABLE IF NOT EXISTS Association
(
    id   SERIAL PRIMARY KEY,
    nom  varchar(100) NOT NULL,
    site varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Date
(
    id    SERIAL PRIMARY KEY,
    annee SMALLINT CHECK (annee > 0)             NOT NULL,
    mois  SMALLINT CHECK (mois BETWEEN 1 AND 12) NOT NULL,
    jour  SMALLINT CHECK (jour BETWEEN 1 AND 30) NOT NULL
);

/**************************** FUNCTION ************************************/

CREATE OR REPLACE FUNCTION incrementer_annee_date(v INTEGER) RETURNS VOID AS
$$
BEGIN
    UPDATE Date SET annee = annee + v WHERE id = 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION incrementer_mois_date(v INTEGER) RETURNS VOID AS
$$
BEGIN
    UPDATE Date SET mois = mois + v WHERE id = 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION incrementer_jour_date(v INTEGER) RETURNS VOID AS
$$
BEGIN
    UPDATE Date SET jour = jour + v WHERE id = 1;
END;
$$ LANGUAGE plpgsql;