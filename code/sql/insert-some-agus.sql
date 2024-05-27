BEGIN TRANSACTION;

INSERT INTO dno
values (default, 'SNG'),
       (default, 'TGG'),
       (default, 'DUR');

INSERT INTO agu (cui, name, min_level, max_level, critical_level, load_volume, latitude, longitude,
                 location_name, dno_id)
values ('PT1701000000000006JQ', 'Alfândega da Fé', 20, 85, 20, 50, 41.33134454715996, -6.968427934882252,
        'Alfândega da Fé', (SELECT dno.id FROM dno WHERE dno.name = 'SNG')),
       ('PT1701000000000009JL', 'Alijó', 30, 85, 20, 35, 41.29741788010863, -7.477619913492303,
        'Alijó', (SELECT dno.id FROM dno WHERE dno.name = 'SNG')),
       ('PT1701000000000012JE', 'Carrazeda de Ansiães', 25, 85, 20, 52, 41.24742767159777, -7.317666075364068,
        'Cazerrada de Ansiães', (SELECT dno.id FROM dno WHERE dno.name = 'SNG')),
       ('PT1604900140400001EW', 'Alpiarça', 30, 85 , 20, 33, 39.265361, -8.549333,
        'Alpiarça', (SELECT dno.id FROM dno WHERE dno.name = 'TGG')),
       ('PT1702900130100001ED', 'Amarante', 30, 85 , 20, 33, 41.288194, -8.086528,
        'Amarante', (SELECT dno.id FROM dno WHERE dno.name = 'DUT')),
       ('PT1701000000000030ZV', 'Amares', 20, 85 , 20, 30, 41.621372, -8.347767,
        'Amares', (SELECT dno.id FROM dno WHERE dno.name = 'SNG'));

INSERT INTO tank (agu_cui, number, min_level, max_level, critical_level, load_volume, correction_factor, capacity)
values ('PT1701000000000006JQ', 1, 20, 85, 20, 50, 10.0, 90),
       ('PT1701000000000009JL', 1, 30, 85, 20, 20, 5.0, 120),
       ('PT1701000000000012JE', 1, 25, 85, 20, 52, 12.0, 120),
       ('PT1604900140400001EW', 1, 30, 85, 20, 33, 3.0, 120),
       ('PT1702900130100001ED', 1, 30, 85, 20, 33, 3.0, 120),
       ('PT1701000000000030ZV', 1, 20, 85, 20, 30, 0.0, 120);

-- for test purposes
Insert into contacts (name, phone, type, agu_cui)
values ('ze-alfandega da fe', '123456789', 'emergency', 'PT1701000000000006JQ'),
       ('ze-alijo', '123456789', 'emergency', 'PT1701000000000009JL'),
       ('ze-carrazeda', '123456789', 'emergency', 'PT1701000000000012JE'),
       ('ze-alpiarca', '123456789', 'emergency', 'PT1604900140400001EW'),
       ('ze-amarante', '123456789', 'emergency', 'PT1702900130100001ED'),
       ('ze-amares', '123456789', 'emergency', 'PT1701000000000030ZV'),
       ('ana-alfandega da fe', '123456789', 'logistic', 'PT1701000000000006JQ'),
       ('ana-alijo', '123456789', 'logistic', 'PT1701000000000009JL'),
       ('ana-carrazeda', '123456789', 'logistic', 'PT1701000000000012JE'),
       ('ana-alpiarca', '123456789', 'logistic', 'PT1604900140400001EW'),
       ('ana-amarante', '123456789', 'logistic', 'PT1702900130100001ED'),
       ('ana-amares', '123456789', 'logistic', 'PT1701000000000030ZV');

-- -- Gas
-- insert into provider (id, agu_cui, provider_type)
-- values (1, 'PT1701000000000006JQ', 'gas'),
--        (2, 'PT1701000000000009JL', 'gas'),
--        (3, 'PT1701000000000012JE', 'gas');
--
-- insert into measure (timestamp, agu_cui, provider_id, tag, data, prediction_for, tank_number)
-- values (now(), 'PT1701000000000006JQ', 1, 'level', 70, now(), 1),
--        (now(), 'PT1701000000000006JQ', 1, 'level', 80, now() + interval '1 day', 1),
--        (now(), 'PT1701000000000006JQ', 1, 'level', 60, now() + interval '2 day', 1);
--
-- -- Temperature
-- insert into provider (id, agu_cui, provider_type)
-- values (4, 'PT1701000000000006JQ', 'temperature'),
--        (5, 'PT1701000000000009JL', 'temperature'),
--        (6, 'PT1701000000000012JE', 'temperature');
--
-- insert into measure (timestamp, agu_cui, provider_id, tag, data, prediction_for)
-- values (now(), 'PT1701000000000006JQ', 4, 'min', 20, now()),
--        (now(), 'PT1701000000000006JQ', 4, 'max', 25, now()),
--        (now(), 'PT1701000000000006JQ', 4, 'min', 30, now() + interval '1 day'),
--        (now(), 'PT1701000000000006JQ', 4, 'max', 35, now() + interval '1 day');
--
--
COMMIT;
