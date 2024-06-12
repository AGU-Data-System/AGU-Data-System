begin transaction;

insert into dno (name, region) values ('SNG', 'north');
insert into transport_company (name) values ('MOL'), ('MED'), ('TJB'), ('TJA');

commit;