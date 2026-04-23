TRUNCATE TABLE todos RESTART IDENTITY;

INSERT INTO todos (id, title, description, completed, created_at, updated_at)
VALUES
    (100, 'seed one', 'first seeded', false, now(), now()),
    (101, 'seed two', 'second seeded', true, now(), now());

SELECT setval('todos_id_seq', 101, true);