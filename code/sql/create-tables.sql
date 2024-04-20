begin transaction;

-- Domains
DROP DOMAIN IF EXISTS CUI;
CREATE DOMAIN CUI as varchar check (value ~ '^PT[0-9]{16}[A-Z]{2}$');

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
    amount           numeric(6, 3) not null,
    distance         numeric(6, 3) not null,
    load_timestamp   timestamp with time zone,
    unload_timestamp timestamp with time zone,

    foreign key (company_name) references transport_company(name)
);

create table if not exists dno
(
    id   int generated always as identity,
    name varchar unique not null,

    primary key (id)
);

create table if not exists agu
(
    id             int generated always as identity primary key,
    name           varchar unique not null,
    cui            CUI unique not null,
    isFavorite     boolean default false not null,
    min_level      integer check (min_level >= 0 and min_level <= 100) not null,
    critical_level integer check (critical_level >= 0 and critical_level <= 100) not null,
    max_level      integer check (max_level >= 0 and max_level <= 100) not null,
    load_volume    numeric(6, 3) check (load_volume >= 0) not null,
    capacity       numeric(6, 3) check (capacity >= 0) not null,
    latitude       numeric(6, 3) check (latitude >= -90 and latitude <= 90) not null,
    longitude      numeric(6, 3) check (longitude >= -180 and longitude <= 180) not null,
    location_name  varchar not null,
    dno_id         int not null,
    notes          json,
    training       json,
    image          bytea,

    foreign key (dno_id) references dno (id)
);

create table if not exists provider
(
    id            int generated always as identity primary key,
    agu_id        int,
    provider_type varchar check (provider_type in ('gas', 'temperature')),

    foreign key (agu_id) references agu (id)
);

create table if not exists readings(
    timestamp       timestamp with time zone,
    agu_id          int,
    dno_id          int,
    provider_id     int,
    data            jsonb not null,
    prediction_for  timestamp with time zone, -- NULL if not a prediction

    foreign key (agu_id) references agu (id),
    foreign key (provider_id) references provider (id),

    primary key (timestamp, agu_id, dno_id, provider_id)
);

create table if not exists contacts
(
    name   varchar not null,
    phone  varchar not null,
    type   varchar check (type in ('emergency', 'logistic')) not null,
    agu_id int,

    foreign key (agu_id) references agu (id),

    primary key (agu_id, name, type)
);

-- Views

create or replace view temperature_readings as
    select
        readings.agu_id,
        readings.provider_id,
        readings.timestamp as fetch_timestamp,
        readings.prediction_for as date,
        readings.data ->> 'min' as min,
        readings.data ->> 'max' as max,
        (readings.timestamp - readings.prediction_for) as days_ahead
    from readings
         join provider on readings.provider_id = provider.id
    where provider.provider_type = 'temperature';

-- view for gas readings
create or replace view gas_readings as
    select
        readings.agu_id,
        readings.provider_id,
        readings.timestamp as fetch_timestamp,
        readings.prediction_for as date,
        readings.data ->> 'level' as level,
        -- readings.model as prediction_model,
        (readings.timestamp - readings.prediction_for) as days_ahead
    from readings
         join provider on readings.provider_id = provider.id
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
