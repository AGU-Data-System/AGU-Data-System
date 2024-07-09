# UAG Data System - Documentation

## Description

This project is a managing system for gas consumption data. 
It is divided into two main parts: the backend and the frontend. 
The backend system is implemented using the Kotlin programming language and the Spring Boot framework. 
The frontend is responsible for providing a user-friendly interface for the user to interact with the system. 
The frontend system is implemented using the React framework.

- The Back-end documentation can be found [here](code/jvm/README.md).
  - The Web API documentation can be found [here](code/jvm/API-doc.md).
- The Front-end documentation can be found [here](code/ts/README.md).

## Installation

To run the application, you need to have the following installed:

- [Docker](https://www.docker.com/)

### Requirements

For running the AGU-Data-System, two microservices are required:

- Dynamic-Scheduling-Fetcher, it's possible to deploy it following the depicted steps [here](https://github.com/AGU-Data-System/Dynamic-Fetching-Scheduler/blob/master/code/jvm/README.md#running-the-application);
- AGU-Prediction-System, it's possible to deploy it following the depicted steps [here](https://github.com/AGU-Data-System/AGU-prediction-system/blob/main/code/jvm/README.md).

To deploy the application using Docker, with the following steps:

1. Deploy the Dynamic-Scheduling-Fetcher microservice;
2. Deploy the AGU-Prediction-System microservice;
3. Run the command:
    ```shell
      docker compose up -d --build --force-recreate
    ```
4. Run the following commands withing 60 seconds of the AGU-Data-System is up:   
    ```shell 
      docker network create agu-fetcher-prediction
      docker network connect agu-fetcher-prediction agu-data-system
      docker network connect agu-fetcehr-prediction fetcher
      docker network connect agu-fetcher-prediction agu-prediction-system
    ```
