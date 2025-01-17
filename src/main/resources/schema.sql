-- Exemplo de schema.sql ajustado:
CREATE TABLE  users (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);


INSERT INTO
    users(nome, email)
VALUES
    ('Kainom', 'kainom@eleuterio.com');

INSERT INTO
    users (nome, email)
VALUES
    ('Eleuterio', 'eleuterio@kainom.com');
