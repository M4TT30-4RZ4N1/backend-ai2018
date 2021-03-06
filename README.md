You can found me here:
https://backendpolito.eu-de.mybluemix.net/
Travis:
[![Build Status](https://travis-ci.com/raffysommy/backend-ai2018.svg?token=uXjDgNqngmeREVzts9Qv&branch=master)](https://travis-ci.com/raffysommy/backend-ai2018)

1) Modify ./src/main/resources/application.properties with:
    spring.data.mongodb.host=db
    spring.data.mongodb.port=27017
    spring.data.mongodb.database=db

2) In order to start the application run a command shell inside the root directory of the project, using:
    > mvn package -Dskiptest
    > docker build -t r4ffy/project .
    > docker-compose up

3) Inside ./src/main/resources there is a file called postman_collection.json.
   Import this file from the UI of Postman Application in order to have access to all the http request useful
   to test the entire application.

4) Inside the package it.polito.ai.project.security it's possible to find all the java classes that allow the
   Configuration of the Authentication mechanism (oauth2 based on JWT) for the three different profiles
   (admin, customer and user).

5) Inside the package it.polito.ai.project.rest it's possible to find the three main Rest Controllers.

6) Inside the package it.polito.ai.project.repositories there are some Interfaces that extends MongoRepository,
   in order to implement complex queries or that require personalization there exist a file
   called PositionRepositoryImpl.java.

7) The main functionalities of the service developed (differentiated by roles and permissions) are summarized
   inside the file PositionService.java, while the transaction service is described in the file CustomerTransactionService.java.
   Both files are stored inside the package it.polito.ai.project.



