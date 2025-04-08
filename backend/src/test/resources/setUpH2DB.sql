CREATE TABLE IF NOT EXISTS hello_db (
    id INT PRIMARY KEY,
    message VARCHAR(255)
);

INSERT INTO hello_db (id, message)
SELECT 1, 'Hello World!'
WHERE NOT EXISTS (SELECT 1 FROM hello_db WHERE id = 1);