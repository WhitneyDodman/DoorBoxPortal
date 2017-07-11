create database doorbox;

create user 'dbadmin'@'localhost' identified by 'Lertindu01!';
create user 'dbadmin'@'%' identified by 'Lertindu01!';

grant all privileges on doorbox.* to 'dbadmin'@'localhost' with grant privilege;
grant all privileges on doorbox.* to 'dbadmin'@'%' with grant privilege;
