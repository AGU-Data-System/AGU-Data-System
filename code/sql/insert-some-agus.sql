BEGIN TRANSACTION;

INSERT INTO dno
values (default, 'Sonorgas');

INSERT INTO agu
values ('PT1701000000000006JQ', 'Alfândega da Fé', 20, 85, 20, 50, 41.33134454715996, -6.968427934882252,
        'Alfândega da Fé', (SELECT id FROM dno WHERE name = 'Sonorgas')),
       ('PT1701000000000009JL', 'Alijó', 30, 85, 20, 20, 41.29741788010863, -7.477619913492303, 'Alijó',
        (SELECT id FROM dno WHERE name = 'Sonorgas')),
       ('PT1701000000000012JE', 'Carrazeda de Ansiães', 25, 85, 20, 52, 41.24742767159777, -7.317666075364068,
        'Cazerrada de Ansiães', (SELECT id FROM dno WHERE name = 'Sonorgas'));

INSERT INTO tank
values ('PT1701000000000006JQ', 1, 20, 85, 20, 50, 90, 100),
       ('PT1701000000000009JL', 1, 30, 85, 20, 20, 120, 100),
       ('PT1701000000000012JE', 1, 25, 85, 20, 52, 90, 100);

-- for test purposes
Insert into contacts
values ('ze-alfandega da fe', '123456789', 'emergency', 'PT1701000000000006JQ'),
       ('ze-alijo', '123456789', 'emergency', 'PT1701000000000009JL'),
       ('ze-carrazeda', '123456789', 'emergency', 'PT1701000000000012JE'),
       ('ana-alfandega da fe', '123456789', 'logistic', 'PT1701000000000006JQ'),
       ('ana-alijo', '123456789', 'logistic', 'PT1701000000000009JL'),
       ('ana-carrazeda', '123456789', 'logistic', 'PT1701000000000012JE');

-- Gas
insert into provider
values (1, 'PT1701000000000006JQ', 'gas'),
       (2, 'PT1701000000000009JL', 'gas'),
       (3, 'PT1701000000000012JE', 'gas');

-- -- Gas
-- insert into provider
-- values (1, 'PT1701000000000006JQ', 'gas'),
--        (2, 'PT1701000000000009JL', 'gas'),
--        (3, 'PT1701000000000012JE', 'gas');
--
-- insert into measure
-- values (now(), 'PT1701000000000006JQ', 1, 'level', 70, now(), 1),
--        (now(), 'PT1701000000000006JQ', 1, 'level', 80, now() + interval '1 day', 1),
--        (now(), 'PT1701000000000006JQ', 1, 'level', 60, now() + interval '2 day', 1);
--
-- -- Temperature
-- insert into provider
-- values (4, 'PT1701000000000006JQ', 'temperature'),
--        (5, 'PT1701000000000009JL', 'temperature'),
--        (6, 'PT1701000000000012JE', 'temperature');
--
-- insert into measure
-- values (now(), 'PT1701000000000006JQ', 4, 'min', 20, now()),
--        (now(), 'PT1701000000000006JQ', 4, 'max', 25, now()),
--        (now(), 'PT1701000000000006JQ', 4, 'min', 30, now() + interval '1 day'),
--        (now(), 'PT1701000000000006JQ', 4, 'max', 35, now() + interval '1 day');
--
--
COMMIT;
