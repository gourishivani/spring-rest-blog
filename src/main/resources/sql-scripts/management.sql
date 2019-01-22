# You need to be logged in as a root

select * from mysql.user;

create database db_example_blog; -- Create the new database
create user 'blogadmin'@'%' identified by 'blog'; -- Creates the user
grant all on db_example_blog.* to 'blogadmin'@'%'; -- Gives all the privileges to the new user on the newly created database