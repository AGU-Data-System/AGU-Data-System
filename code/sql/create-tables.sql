begin transaction;

-- Drop views
DROP VIEW IF EXISTS temperature_measures;
DROP VIEW IF EXISTS gas_measures;

-- Drop tables
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS measure;
DROP TABLE IF EXISTS provider;
DROP TABLE IF EXISTS tank;
DROP TABLE IF EXISTS agu;
DROP TABLE IF EXISTS dno;
DROP TABLE IF EXISTS loads;
DROP TABLE IF EXISTS transport_company;

-- Drop domains
DROP DOMAIN IF EXISTS CUI;
DROP DOMAIN IF EXISTS PHONE;
DROP DOMAIN IF EXISTS PERCENTAGE;
DROP DOMAIN IF EXISTS LONGITUDE;
DROP DOMAIN IF EXISTS LATITUDE;

-- Domains
CREATE DOMAIN CUI as varchar check (value ~ '^PT[0-9]{16}[A-Z]{2}$');
CREATE DOMAIN PHONE as varchar check (value ~ '^[0-9]{9}$');
CREATE DOMAIN PERCENTAGE as integer check (value >= 0 and value <= 100);
CREATE DOMAIN LONGITUDE as numeric(9, 6) check (value >= -180 and value <= 180);
CREATE DOMAIN LATITUDE as numeric(9, 6) check (value >= -90 and value <= 90);

-- Tables
create table if not exists transport_company
(
    name varchar primary key
);

create table if not exists loads
(
    reference        varchar primary key,
    company_name     varchar,
    time_of_day      varchar check (time_of_day in ('morning', 'afternoon')) not null,
    amount           numeric(6, 3)                                           not null,
    distance         numeric(6, 3)                                           not null,
    load_timestamp   timestamp with time zone,
    unload_timestamp timestamp with time zone,

    foreign key (company_name) references transport_company (name)
);

create table if not exists dno
(
    id   int generated always as identity,
    name varchar check (length(name) > 0) unique not null,

    primary key (id)
);

create table if not exists agu
(
    cui            CUI primary key,
    name           varchar check (length(name) > 0) unique not null,
    min_level      PERCENTAGE                              not null,
    max_level      PERCENTAGE                              not null,
    critical_level PERCENTAGE                              not null,
    load_volume    numeric(6, 3) check (load_volume >= 0)  not null,
    latitude       LATITUDE                                not null,
    longitude      LONGITUDE                               not null,
    location_name  varchar                                 not null,
    dno_id         int                                     not null,
    is_favorite    boolean default false                   not null,
    notes          json,
    training       json,
    image          bytea,

    constraint min_max_critical_levels check (critical_level <= min_level and min_level <= max_level),

    foreign key (dno_id) references dno (id)
);

create table if not exists tank
(
    agu_cui           CUI,
    number            int check (number >= 0)                      not null,
    min_level         PERCENTAGE                                   not null,
    max_level         PERCENTAGE                                   not null,
    critical_level    PERCENTAGE                                   not null,
    load_volume       numeric(6, 3) check (load_volume >= 0)       not null, -- using 20tons as reference is a percentage of that can be higher than 100%
    correction_factor numeric(6, 3) check (correction_factor >= 0) not null,
    capacity          int check (capacity >= 0)                    not null,

    constraint min_max_critical_levels check (critical_level <= min_level and min_level <= max_level),

    foreign key (agu_cui) references agu (cui),
    primary key (agu_cui, number)
);

create table if not exists provider
(
    id            int primary key,
    agu_cui       CUI,
    provider_type varchar check (provider_type ~* '^(gas|temperature)$'),
    last_fetch    timestamp with time zone,

    foreign key (agu_cui) references agu (cui)
);

create table if not exists measure
(
    timestamp      timestamp with time zone not null,
    agu_cui        CUI,
    provider_id    int,
    tag            varchar check (tag ~* '^(level|min|max)$'),
    data           int                      not null,
    prediction_for timestamp with time zone not null,
    tank_number    int check ((tag ~* '(level)$' and tank_number IS NOT NULL) or
                              (tank_number is null and tag ~* '^(min|max)$')),

    constraint prediction_in_future check (prediction_for >= timestamp),

    foreign key (agu_cui) references agu (cui),
    foreign key (provider_id) references provider (id),
    foreign key (agu_cui, tank_number) references tank (agu_cui, number),

    primary key (timestamp, agu_cui, provider_id, tag, prediction_for)
);

create table if not exists contacts
(
    name    varchar                                          not null,
    phone   PHONE                                            not null,
    type    varchar check (type ~* '^(emergency|logistic)$') not null,
    agu_cui CUI,

    foreign key (agu_cui) references agu (cui),

    primary key (agu_cui, name, type)
);

-- Views

create or replace view temperature_measures as
select provider.id,
       provider.agu_cui,
       measure1.timestamp                             as fetch_timestamp,
       measure1.prediction_for,
       measure1.data                                  as min,
       measure2.data                                  as max,
       (measure1.timestamp - measure1.prediction_for) as days_ahead
from provider
         left join measure measure1 on measure1.provider_id = provider.id
         left join measure measure2 on measure2.provider_id = measure1.provider_id
where provider.provider_type = 'temperature';

-- view for gas readings
create or replace view gas_measures as
select measure.agu_cui,
       measure.provider_id,
       measure.timestamp                            as fetch_timestamp,
       measure.prediction_for                       as date,
       measure.data                                 as level,
       tank.number                                  as tank_number,
       (measure.timestamp - measure.prediction_for) as days_ahead
from measure
         left join provider on measure.provider_id = provider.id
         left join tank on measure.agu_cui = tank.agu_cui
where provider.provider_type = 'gas';
commit;

-- use for a rainy day
-- create table if not exists driver
-- (
--     company_name varchar,
--     name         varchar,
--
--     unique (name),
--
--     foreign key (company_name) references transport_company (name),
--
--     primary key (company_name, name)
-- );
--
-- create table if not exists car
-- (
--     company_name  varchar,
--     driver_name   varchar,
--     tank_capacity numeric(6, 3),
--     licence_plate varchar,
--
--     foreign key (company_name) references transport_company (name),
--     foreign key (driver_name) references driver (name),
--
--     primary key (company_name, licence_plate)
-- );
