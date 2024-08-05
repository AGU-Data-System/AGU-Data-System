begin transaction;

--Delete tables
DELETE FROM alerts;
DELETE FROM scheduled_load;
DELETE FROM agu_transport_company;
DELETE FROM transport_company;
DELETE FROM measure;
DELETE FROM tank;
DELETE FROM provider;
DELETE FROM contacts;
DELETE FROM agu;
DELETE FROM dno;

commit;