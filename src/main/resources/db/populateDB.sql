--
-- Clear table data
--
DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;

-- Restart sequence
ALTER SEQUENCE global_seq RESTART WITH 100000;

--
-- Fill test data up
--
INSERT INTO users (name, email, password) VALUES
  ('User', 'user@yandex.ru', 'password'),
  ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

INSERT INTO meals (description, datetime, calories, user_id) VALUES
    ('ЗавтракА','2019-10-21 08:00:00',  500, 100001),
    ('ОбедА',   '2019-10-21 13:00:00', 1000, 100001),
    ('УжинА',   '2019-10-21 20:00:00',  400, 100001),
    ('Завтрак', '2019-10-21 08:00:00',  400, 100000),
    ('Обед',    '2019-10-21 13:00:00', 1000, 100000),
    ('Ужин',    '2019-10-21 20:00:00',  500, 100000),
    ('Завтрак', '2019-10-22 08:15:00',  500, 100000),
    ('Обед',    '2019-10-22 13:15:00', 1000, 100000),
    ('Ужин',    '2019-10-22 19:45:00',  600, 100000),
    ('Завтрак', '2019-10-23 08:30:00',  350, 100000),
    ('Обед',    '2019-10-23 13:30:00', 1100, 100000),
    ('Ужин',    '2019-10-23 20:30:00',  500, 100000)
;
