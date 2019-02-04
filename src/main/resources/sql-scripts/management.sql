# You need to be logged in as a root

# We will be using the following to run the app
create database blog_db; -- Create the new database
create user 'blogadmin'@'%' identified by 'blog'; -- Creates the user
grant all on blog_db.* to 'blogadmin'@'%'; -- Gives all the privileges to the new user on the newly created database


# The following is in order to run integration tests.To keep it simple, am using the same user account for both integration test db and main db.
create database blog_test_db;
grant all on blog_test_db.* to 'blogadmin'@'%'; -- Gives all the privileges to the new user on the newly created database