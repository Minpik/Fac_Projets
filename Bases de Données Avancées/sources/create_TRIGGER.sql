DROP FUNCTION IF EXISTS check_deja_ami() CASCADE;
DROP FUNCTION IF EXISTS check_possibilite_faire_don() CASCADE;
DROP FUNCTION IF EXISTS update_projet_etat() CASCADE;
DROP FUNCTION IF EXISTS date_change() CASCADE;
DROP FUNCTION IF EXISTS check_projet_termine() CASCADE;
DROP FUNCTION IF EXISTS prevenir_donateurs_fans() CASCADE;

DROP TRIGGER IF EXISTS before_insert_ami ON Ami;
DROP TRIGGER IF EXISTS before_insert_don ON Don;
DROP TRIGGER IF EXISTS after_insert_don ON Don;
DROP TRIGGER IF EXISTS after_update_date ON Date;
DROP TRIGGER IF EXISTS after_update_projet ON Projet;
DROP TRIGGER IF EXISTS after_insert_news ON News;

CREATE OR REPLACE FUNCTION check_deja_ami() RETURNS TRIGGER AS
$$
DECLARE
    ligne RECORD;
BEGIN
    FOR ligne IN SELECT id_user_2 FROM Ami WHERE id_user_1 = NEW.id_user_2
        LOOP
            IF ligne.id_user_2 = NEW.id_user_1 THEN
                RAISE EXCEPTION 'Relation déjà existante';
            END IF;
        END LOOP;
    /*RAISE NOTICE '% devient %', OLD.annee, NEW.annee;*/
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_ami
    BEFORE INSERT
    ON Ami
    FOR EACH ROW
EXECUTE PROCEDURE check_deja_ami();

/****************************************************************/

CREATE OR REPLACE FUNCTION check_possibilite_faire_don() RETURNS TRIGGER AS
$$
DECLARE
BEGIN
    IF (SELECT Projet.etat FROM Projet WHERE id = NEW.id_projet) = 'T' THEN
        RAISE EXCEPTION 'Impossible de financer ce projet (Terminé)';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_don
    BEFORE INSERT
    ON Don
    FOR EACH ROW
EXECUTE PROCEDURE check_possibilite_faire_don();

/****************************************************************/

CREATE OR REPLACE FUNCTION update_projet_etat() RETURNS TRIGGER AS
$$
DECLARE
    total_dons INTEGER;
    objectif   INTEGER;
    ligne      RECORD;
    _id        Utilisateur.id%TYPE;
    _pseudo    Utilisateur.pseudo%TYPE;
BEGIN
    IF (SELECT Projet.etat FROM Projet WHERE id = NEW.id_projet) != 'F' THEN
        total_dons :=
                (SELECT SUM(valeur) FROM Don WHERE id_projet = NEW.id_projet);
        objectif :=
                (SELECT Projet.objectif FROM Projet WHERE id = NEW.id_projet);
        IF (total_dons >= objectif) THEN
            UPDATE Projet SET etat = 'F' WHERE id = NEW.id_projet;
        END IF;
    END IF;

    FOR ligne IN SELECT *
                 FROM AMI
                 WHERE id_user_1 = NEW.id_utilisateur
                    OR id_user_2 = NEW.id_utilisateur
        LOOP
            IF ligne.id_user_1 = NEW.id_utilisateur THEN
                _id := ligne.id_user_2;
            ELSE
                _id := ligne.id_user_1;
            END IF;
            _pseudo := (SELECT Utilisateur.pseudo
                        FROM Utilisateur
                        WHERE Utilisateur.id = _id);
            RAISE NOTICE 'Vous prévenez % de votre don, une invitation pour participer lui a été envoyée', _pseudo;
        END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_don
    AFTER INSERT
    ON Don
    FOR EACH ROW
EXECUTE PROCEDURE update_projet_etat();

/****************************************************************/

CREATE OR REPLACE FUNCTION date_change() RETURNS TRIGGER AS
$$
DECLARE
    ligne_date    Date%ROWTYPE;
    ligne         RECORD;
    date_actuelle date;
    diff          INTEGER;
BEGIN
    SELECT * INTO ligne_date FROM date WHERE id = NEW.id;
    date_actuelle := CONCAT(ligne_date.annee, '-', ligne_date.mois, '-',
                            ligne_date.jour);

    FOR ligne IN SELECT id, date_fin, etat FROM Projet
        LOOP
            IF ligne.etat != 'T' THEN
                IF date_actuelle >= ligne.date_fin::date THEN
                    RAISE NOTICE 'projet % terminé', ligne.id;
                    UPDATE PROJET SET etat = 'T' WHERE id = ligne.id;
                ELSE
                    diff := (extract(day from
                                     (ligne.date_fin - date_actuelle)));
                    IF diff = 1 THEN
                        RAISE NOTICE 'Plus que 1 jour pour le projet %',
                                (SELECT nom FROM Projet WHERE id = ligne.id);
                    END IF;
                END IF;
            END IF;
        END LOOP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_update_date
    AFTER UPDATE
    ON Date
    FOR EACH ROW
EXECUTE PROCEDURE date_change();

/****************************************************************/

CREATE OR REPLACE FUNCTION check_projet_termine() RETURNS TRIGGER AS
$$
DECLARE
    ligne RECORD;
BEGIN
    IF NEW.etat = 'F' AND OLD.etat != 'F' THEN
        FOR ligne IN SELECT DISTINCT id_utilisateur, pseudo
                     FROM Don
                              INNER JOIN Utilisateur
                                         ON Don.id_utilisateur = Utilisateur.id
                     WHERE id_projet = NEW.id
            LOOP
                RAISE NOTICE 'Le projet % est maintenant financé, merci % d''avoir participé', NEW.nom, ligne.pseudo;
            END LOOP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_update_projet
    AFTER UPDATE
    ON Projet
    FOR EACH ROW
EXECUTE PROCEDURE check_projet_termine();

/****************************************************************/

CREATE OR REPLACE FUNCTION prevenir_donateurs_fans() RETURNS TRIGGER AS
$$
DECLARE
    ligne      RECORD;
    projet_nom Projet.nom%TYPE;
BEGIN
    /*projet_nom := (SELECT nom FROM Projet WHERE id = NEW.id_projet);*/
    SELECT nom INTO projet_nom FROM Projet WHERE id = NEW.id_projet;
    FOR ligne IN SELECT DISTINCT id_utilisateur, pseudo
                 FROM Fan
                          INNER JOIN Utilisateur
                                     ON Fan.id_utilisateur = Utilisateur.id
                 WHERE id_projet = NEW.id_projet
        LOOP
            RAISE NOTICE '%, une news du projet % vient de sortir', ligne.pseudo, projet_nom;
        END LOOP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_news
    AFTER INSERT
    ON News
    FOR EACH ROW
EXECUTE PROCEDURE prevenir_donateurs_fans();


SELECT DISTINCT id_utilisateur, pseudo
FROM Fan
         INNER JOIN Utilisateur ON Fan.id_utilisateur = Utilisateur.id
WHERE id_projet = 1