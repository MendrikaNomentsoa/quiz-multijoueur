-- Donnees de test pour la banque de quiz (Theme -> Quiz -> Question -> Choix)
-- Chargees automatiquement au demarrage grace a hibernate.hbm2ddl.import_files

-- Themes
INSERT INTO theme (id, nom, description) VALUES (1, 'Culture generale', 'Un peu de tout pour tester ses connaissances');
INSERT INTO theme (id, nom, description) VALUES (2, 'Cinema', 'Films, acteurs et realisateurs');

-- Quiz
INSERT INTO quiz (id, titre, theme_id) VALUES (1, 'Culture generale - Niveau facile', 1);
INSERT INTO quiz (id, titre, theme_id) VALUES (2, 'Films des annees 90', 2);

-- Questions du quiz 1
INSERT INTO question (id, enonce, ordre, duree_reponse_ms, quiz_id) VALUES (1, 'Quelle est la capitale de la France ?', 0, 15000, 1);
INSERT INTO question (id, enonce, ordre, duree_reponse_ms, quiz_id) VALUES (2, 'Combien y a-t-il de continents ?', 1, 15000, 1);
INSERT INTO question (id, enonce, ordre, duree_reponse_ms, quiz_id) VALUES (3, 'Quel est le plus grand ocean du monde ?', 2, 15000, 1);

-- Choix pour la question 1 (capitale de la France)
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (1, 'Paris', true, 1);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (2, 'Lyon', false, 1);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (3, 'Marseille', false, 1);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (4, 'Bordeaux', false, 1);

-- Choix pour la question 2 (nombre de continents)
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (5, '5', false, 2);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (6, '6', true, 2);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (7, '7', false, 2);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (8, '8', false, 2);

-- Choix pour la question 3 (plus grand ocean)
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (9, 'Atlantique', false, 3);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (10, 'Indien', false, 3);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (11, 'Pacifique', true, 3);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (12, 'Arctique', false, 3);

-- Questions du quiz 2
INSERT INTO question (id, enonce, ordre, duree_reponse_ms, quiz_id) VALUES (4, 'Qui realise "Pulp Fiction" (1994) ?', 0, 20000, 2);
INSERT INTO question (id, enonce, ordre, duree_reponse_ms, quiz_id) VALUES (5, 'Quel film remporte l''Oscar du meilleur film en 1995 ?', 1, 20000, 2);

-- Choix pour la question 4
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (13, 'Quentin Tarantino', true, 4);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (14, 'Martin Scorsese', false, 4);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (15, 'Steven Spielberg', false, 4);

-- Choix pour la question 5
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (16, 'Forrest Gump', true, 5);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (17, 'Pulp Fiction', false, 5);
INSERT INTO choix (id, texte, est_correct, question_id) VALUES (18, 'Le Roi Lion', false, 5);

-- Remise a niveau des sequences H2 pour eviter les collisions d'id lors des futurs persist()
ALTER TABLE theme ALTER COLUMN id RESTART WITH 3;
ALTER TABLE quiz ALTER COLUMN id RESTART WITH 3;
ALTER TABLE question ALTER COLUMN id RESTART WITH 6;
ALTER TABLE choix ALTER COLUMN id RESTART WITH 19;
