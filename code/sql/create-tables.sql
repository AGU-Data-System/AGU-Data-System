begin transaction;

create table if not exists transport_company
(
    name varchar primary key
);

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

create table if not exists loads
(
    reference        varchar,
    company_name     varchar,
    licence_plate    varchar,
    time_of_day      varchar,
    load_timestamp   bigint,
    unload_timestamp bigint,
    isCompleted      boolean, -- will be true when unload_timestamp is not null
    amount           numeric(6, 3),
    distance         numeric(6, 3),

    constraint check_scheduled_arrival_time check (time_of_day in ('morning', 'afternoon', 'evening')),

    foreign key (company_name) references transport_company(name),

    primary key (reference)
);

create table if not exists dno
(
    id   int generated always as identity,
    name varchar,

    primary key (id)
);

create table if not exists location
(
    latitude  numeric(6, 3),
    longitude numeric(6, 3),
    name      varchar default null,

    constraint check_latitude check (latitude >= -180 and latitude <= 180),
    constraint check_longitude check (longitude >= -180 and longitude <= 180),

    primary key (latitude, longitude)
);

-- create table if not exists temperature
-- (
--     id        int generated always as identity,
--     date      varchar,
--     min       numeric(6, 3),
--     max       numeric(6, 3),
--     timestamp bigint,
--     latitude  numeric(6, 3),
--     longitude numeric(6, 3),
--
--     foreign key (latitude, longitude) references location (latitude, longitude)
--
-- );

create table if not exists agu
(
    id             int generated always as identity,
    cui            varchar,
    isFavorite     boolean,
    notes          json,
    training       json,
    min_level      integer,
    critical_level integer,
    max_level      integer,
    load_volume    numeric(6, 3),
    capacity       numeric(6, 3),
    image          bytea,
    dno_id         int,
    latitude       numeric(6, 3),
    longitude      numeric(6, 3),

    unique (cui),

    constraint critical_level_check check (critical_level >= 0 and critical_level <= 100),
    constraint min_level_check check (min_level >= 0 and min_level <= 100),
    constraint max_level_check check (max_level >= 0 and max_level <= 100),


    foreign key (latitude, longitude) references location (latitude, longitude),
    foreign key (dno_id) references dno (id),

    primary key (dno_id, id)
);

create table if not exists contacts
(
    name   varchar,
    phone  varchar,
    type   varchar,
    agu_id int,
    dno_id int,

    constraint check_type check (type in ('logistic', 'emergency')),

    foreign key (dno_id, agu_id) references agu (dno_id, id),

    primary key (dno_id, agu_id)
);

create table if not exists levels
(
    timestamp bigint,
    data     jsonb,
    type     varchar,
    provider_id int,
    agu_id int,
    dno_id int,

    constraint check_type check (type in ('temperature', 'gas')),

    foreign key (agu_id, dno_id) references agu (id, dno_id),

    primary key (timestamp, agu_id, dno_id)
);

create table if not exists readings(
    timestamp bigint,
    agu_id int,
    dno_id int,

    foreign key (timestamp, agu_id, dno_id) references levels (timestamp, agu_id, dno_id),

    primary key (timestamp, agu_id, dno_id)
);

create table if not exists predictions
(
    timestamp bigint,
    agu_id int,
    dno_id int,
    prediction_date bigint,
    model jsonb,
    days_ahead int, -- calculated from prediction_date and timestamp

    foreign key (timestamp, agu_id, dno_id) references levels (timestamp, agu_id, dno_id),

    primary key (timestamp, agu_id, dno_id, prediction_date)
);

-- view for temperature and gas
create or replace view temperature_view as
    select levels.agu_id,
           levels.provider_id,
           levels.timestamp as fetch_timestamp,
           levels.data as temperature,
           predictions.prediction_date as date,
           predictions.days_ahead
    from levels join predictions
        on predictions.agu_id = levels.agu_id and
           predictions.timestamp = levels.timestamp and
              predictions.dno_id = levels.dno_id
           where levels.type = 'temperature';

create or replace view gas_view as
    select levels.agu_id,
           levels.provider_id,
           levels.timestamp as fetch_timestamp,
           levels.data as level,
           predictions.prediction_date as date,
           predictions.days_ahead,
           predictions.model
    from levels join predictions
        on predictions.agu_id = levels.agu_id and
           predictions.timestamp = levels.timestamp and
              predictions.dno_id = levels.dno_id
           where levels.type = 'gas';

commit;