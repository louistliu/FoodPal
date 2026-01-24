# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client without a config. Default config will be created in `client/config.json`.

Optionally config can be specified with

	mvn -pl client -am javafx:run -Dcfg=<config file path>

or by passing `-cfg <config file path>` in program arguments.

to run the client (with a valid config path). Please note that the server needs to be running, before you can start the client.

Get the template project running from the command line first to ensure you have the required tools on your system.

Once it is working, you can try importing the project into your favorite IDE. Especially the client is a bit more tricky to set up there due to the dependency on a JavaFX SDK.
To help you get started, you can find additional instructions in the corresponding README of the client project.

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
