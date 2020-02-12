DROP FUNCTION IF EXISTS afficher_projets_suivis_de(i INTEGER);
CREATE OR REPLACE FUNCTION afficher_projets_suivis_de(i INTEGER) RETURNS VOID AS
$$
DECLARE
    _id  Fan.id_utilisateur%TYPE;
    _nom Projet.nom%TYPE;
BEGIN
    FOR _id IN SELECT id_projet FROM Fan WHERE id_utilisateur = i
        LOOP
            _nom := (SELECT nom FROM Projet WHERE id = _id);
            RAISE NOTICE 'vous suivez le projet %', _nom;
        END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM afficher_projets_suivis_de(11);
