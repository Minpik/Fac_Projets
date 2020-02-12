DROP FUNCTION IF EXISTS afficher_amis_de(i INTEGER);
CREATE OR REPLACE FUNCTION afficher_amis_de(i INTEGER) RETURNS VOID AS
$$
DECLARE
    ligne   RECORD;
    _id     Utilisateur.id%TYPE;
    _pseudo Utilisateur.pseudo%TYPE;
BEGIN
    FOR ligne IN SELECT * FROM AMI WHERE id_user_1 = i OR id_user_2 = i
        LOOP
            IF ligne.id_user_1 = i THEN
                _id := ligne.id_user_2;
            ELSE
                _id := ligne.id_user_1;
            END IF;
            _pseudo := (SELECT Utilisateur.pseudo
                        FROM Utilisateur
                        WHERE Utilisateur.id = _id);
            RAISE NOTICE 'vous êtes ami avec %', _pseudo;
        END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM afficher_amis_de(11);