begin transaction;

-- DELETE tables
DELETE
FROM contacts;
DELETE
FROM measure;
DELETE
FROM provider;
DELETE
FROM tank;
DELETE
FROM agu_transport_company;
DELETE
FROM agu;
DELETE
FROM dno;
DELETE
FROM scheduled_load;
DELETE
FROM transport_company;

commit;
