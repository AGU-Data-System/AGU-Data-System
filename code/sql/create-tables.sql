begin transaction;

-- Drop tables
DROP TABLE IF EXISTS alerts CASCADE;
DROP TABLE IF EXISTS scheduled_load CASCADE;
DROP TABLE IF EXISTS agu_transport_company CASCADE;
DROP TABLE IF EXISTS transport_company CASCADE;
DROP TABLE IF EXISTS measure CASCADE;
DROP TABLE IF EXISTS tank CASCADE;
DROP TABLE IF EXISTS provider CASCADE;
DROP TABLE IF EXISTS contacts CASCADE;
DROP TABLE IF EXISTS agu CASCADE;
DROP TABLE IF EXISTS dno CASCADE;

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
    id   int generated always as identity,
    name varchar check (length(name) > 0) unique not null,

    primary key (id)
);


create table if not exists dno
(
    id     int generated always as identity,
    name   varchar check (length(name) > 0) unique not null,
    region varchar check (length(region) > 0)      not null,

    primary key (id)
);

create table if not exists agu
(
    cui               CUI primary key,
    eic               varchar check (length(eic) > 0) unique    not null,
    name              varchar check (length(name) > 0) unique   not null,
    min_level         PERCENTAGE                                not null,
    max_level         PERCENTAGE                                not null,
    critical_level    PERCENTAGE                                not null,
    correction_factor numeric(6, 3)                             not null,
    load_volume       int check (load_volume >= 0)              not null,
    latitude          LATITUDE                                  not null,
    longitude         LONGITUDE                                 not null,
    location_name     varchar check (length(location_name) > 0) not null,
    dno_id            int                                       not null,
    is_favorite       boolean default false                     not null,
    is_active         boolean default true                      not null,
    notes             varchar,
    training          json,
    image             bytea,

    constraint min_max_critical_levels check (critical_level <= min_level and min_level <= max_level),

    foreign key (dno_id) references dno (id) on delete cascade
);

--
-- create table if not exists loads
-- (
--     reference        varchar primary key,
--     company_name     varchar,
--     time_of_day      varchar check (time_of_day in ('morning', 'afternoon')) not null,
--     amount           numeric(6, 3)                                           not null,
--     distance         numeric(6, 3)                                           not null,
--     load_timestamp   timestamp with time zone,
--     unload_timestamp timestamp with time zone,
--
--     foreign key (company_name) references transport_company (name)
-- );

create table if not exists alerts
(
    id          int generated always as identity primary key,
    agu_cui     CUI,
    timestamp   timestamp with time zone,
    title       varchar check (length(title) > 0)   not null,
    message     varchar check (length(message) > 0) not null,
    is_resolved boolean default false               not null,

    foreign key (agu_cui) references agu (cui) on delete cascade
);

-- TODO: Create delivered_loads table
create table if not exists scheduled_load
(
    id           int generated always as identity,
    agu_cui      CUI,
    local_date   date,
    time_of_day  varchar check (time_of_day in ('morning', 'afternoon')) not null,
    amount       numeric(6, 3) default 1.0                               not null,
    is_manual    boolean       default false                             not null,
    is_confirmed boolean       default false                             not null,

    foreign key (agu_cui) references agu (cui),
    primary key (id)
);

create table if not exists agu_transport_company
(
    agu_cui    CUI,
    company_id int,

    foreign key (agu_cui) references agu (cui) on delete cascade,
    foreign key (company_id) references transport_company (id) on delete cascade,

    primary key (agu_cui, company_id)
);

create table if not exists tank
(
    agu_cui           CUI,
    number            int check (number >= 0)   not null,
    min_level         PERCENTAGE                not null,
    max_level         PERCENTAGE                not null,
    critical_level    PERCENTAGE                not null,
    correction_factor numeric(6, 3)             not null,
    capacity          int check (capacity >= 0) not null,

    constraint min_max_critical_levels check (critical_level <= min_level and min_level <= max_level),

    foreign key (agu_cui) references agu (cui) on delete cascade,
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
    tank_number    int                      not null,

    constraint prediction_in_future check (prediction_for::date >= timestamp::date),

    foreign key (agu_cui) references agu (cui),
    foreign key (provider_id) references provider (id) on delete cascade,
    foreign key (agu_cui, tank_number) references tank (agu_cui, number) on delete cascade,

    primary key (timestamp, agu_cui, provider_id, tag, prediction_for, tank_number)
);

create table if not exists contacts
(
    id      int generated always as identity,
    name    varchar check (length(name) > 0)                 not null,
    phone   PHONE                                            not null,
    type    varchar check (type ~* '^(emergency|logistic)$') not null,
    agu_cui CUI,

    foreign key (agu_cui) references agu (cui) on delete cascade,
    primary key (agu_cui, id)

);

commit;

