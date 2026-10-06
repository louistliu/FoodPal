# FoodPal - A recipe management web application

A full-stack recipe management application built with Spring Boot and JavaFX. It features real-time automated change synchronization via STOMP websockets, recipe search function with history tracking, and multi-language support.

# Prerequisites

- Java 23+
- Maven (or use the provided maven wrapper 'mvnw')

# How to run
To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client without a config. Default config will be created in `client/config.json`.
Optionally config can be specified with

	mvn -pl client -am javafx:run -Dcfg=<config file path>

or, otherwise, by passing `-cfg <config file path>` in program arguments when running in Intellij or as an exectuable
to run the client (with a valid config path). Please note that the server needs to be running, before you can start the client.

## Keep these things in mind
- You can edit or delete an ingredient/instruction by right-clicking it.
- The app uses STOMP websockets.

## Feature overview
- All basic features
  - Extra feature: Integration tests for server (db and endpoints) and client sockets
- Automated Change Synchronization
  - Extra feature: auto saves recipe on edit
- Live Language Switch
  - Extra feature: a total of five languages: Dutch, English, French, Turkish and Arabic (RTL)
- Searching for Recipes
  - Extra feature: Search history
    - History is saved once you search and click the search icon.
    - Click a search query in the history window to search it again. 
    - Only saves after unfocusing the searchbar (to only save searches where the user actually opened a recipe).
    - Only saves the last 10 results.
