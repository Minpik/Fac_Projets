DROP FUNCTION IF EXISTS afficher_dons_de(i Utilisateur.id%TYPE);
CREATE OR REPLACE FUNCTION afficher_dons_de(i Utilisateur.id%TYPE) RETURNS VOID AS
$$
DECLARE
    _ligne RECORD;
    _nom   Projet.nom%TYPE;
BEGIN
    FOR _ligne IN SELECT id_projet, valeur, date
                  FROM Don
                  WHERE id_utilisateur = i
        LOOP
            _nom := (SELECT nom FROM Projet WHERE id = _ligne.id_projet);
            RAISE NOTICE 'vous avez fait un don à % de % euros le %', _nom,
                _ligne.valeur, _ligne.date::date;
        END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT *
FROM afficher_dons_de(11);