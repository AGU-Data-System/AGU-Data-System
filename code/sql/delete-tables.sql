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
FROM agu;
DELETE
FROM dno;
DELETE
FROM loads;
DELETE
FROM transport_company;

commit;
