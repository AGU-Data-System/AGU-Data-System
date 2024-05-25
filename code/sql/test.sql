SELECT DISTINCT ON (date_trunc('day', measure.timestamp))
    measure.timestamp, measure.prediction_for, measure.data, measure.tank_number
FROM measure
WHERE measure.provider_id = 1 AND
    measure.timestamp::date >= now()::date - :days AND
    measure.prediction_for = measure.timestamp
ORDER BY date_trunc('day', measure.timestamp),
         abs(extract(epoch from measure.timestamp::time) - extract(epoch from :timestamp::time))