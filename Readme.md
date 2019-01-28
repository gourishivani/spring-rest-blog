Authentication:
Not using session based authentication with the intent of being stateless.

All the entities are loaded when the application starts via LoadDatabase.java. 
The following user credentials should work from the get go:
johndoe@example.com/test123
tester@example.com/tester

The UI pre-populates first user's credentials in the login form. This is only meant for ease of testing. This shouldn't be the case in production.