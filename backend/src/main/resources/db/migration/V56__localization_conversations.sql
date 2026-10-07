UPDATE conversations
SET dialog_es = '¡Hola! @NAME, mi nombre es Liria'
WHERE id = 1;

UPDATE conversations
SET dialog_es = 'Este es un diálogo condicional para la variable isSorenInGroup'
WHERE id = 7;

UPDATE conversations
SET dialog_es = 'Este es el diálogo si la variable es verdadera'
WHERE id = 8;

UPDATE conversations
SET dialog_es = 'Este es el diálogo alternativo si la variable es falsa'
WHERE id = 9;

UPDATE conversations
SET dialog_es = 'Adiós'
WHERE id = 11;
