begin transaction;

-- DELETE tables
DELETE
FROM contacts
WHERE true;
DELETE
FROM measure
WHERE true;
DELETE
FROM provider
WHERE true;
DELETE
FROM tank
WHERE true;
DELETE
FROM agu_transport_company
WHERE true;
DELETE
FROM scheduled_load
WHERE true;
DELETE
FROM alerts
WHERE true;
DELETE
FROM agu
WHERE true;
DELETE
FROM dno
WHERE true;
DELETE
FROM transport_company
WHERE true;

commit;
