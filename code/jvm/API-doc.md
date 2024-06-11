# AGU Data System — API Documentation

## Table of Contents

- [Introduction](#introduction)
- [Pagination](#pagination)
- [API Endpoints](#api-endpoints)
    - [AGU](#agu)
        - [Get All AGUs](#get-all-agus)
        - [Get AGU by id](#get-agu-by-id)
        - [Create AGU](#create-agu)
        - [Get Temperature Measures](#get-temperature-measures)
        - [Get Daily Gas Measures](#get-daily-gas-measures)
        - [Get Hourly Gas Measures](#get-hourly-gas-measures)
        - [Get Prediction Gas Measures](#get-prediction-gas-measures)
        - [Get Favourite AGUs](#get-favourite-agus)
        - [Update Favourite State](#update-favourite-state)
        - [Add Contact](#add-contact)
        - [Delete Contact](#delete-contact)
        - [Add tank](#add-tank)
        - [Update Tank](#update-tank)
        - [Change Gas Levels](#change-gas-levels)
        - [Change Notes](#change-notes)
- [Input Models](#input-models)
    - [AGU Input Models](#agu-input-models)
        - [AGU Creation Input Model](#agu-creation-input-model)
        - [Notes Input Model](#notes-input-model)
        - [Update Favourite AGU Input Model](#update-favorite-agu-input-model)
    - [Contact Input Models](#contact-input-models)
        - [Contact Creation Input Model](#contact-creation-input-model)
    - [DNO Input Models](#dno-input-models)
        - [DNO Creation Input Model](#dno-creation-input-model)
    - [Gas Levels Input Models](#gas-levels-input-models)
        - [Gas Levels Input Model](#gas-levels-input-model)
    - [Tank Input Models](#tank-input-models)
        - [Tank Creation Input Model](#tank-creation-input-model)
        - [Update Tank Input Model](#update-tank-input-model)
- [Output Models](#output-models)
    - [AGU Output Models](#agu-output-models)
        - [AGU Basic Info Output Model](#agu-basic-info-output-model)
        - [AGU Basic Info List Output Model](#agu-basic-info-list-output-model)
        - [AGU Creation Output Model](#agu-creation-output-model)
        - [AGU Output Model](#agu-output-model)
    - [Contact Output Models](#contact-output-models)
        - [Add Contact Output Model](#add-contact-output-model)
        - [Contact List Output Model](#contact-list-output-model)
        - [Contact Output Model](#contact-output-model)
    - [DNO Output Models](#dno-output-models)
        - [DNO Output Model](#dno-output-model)
    - [Gas Levels Output Models](#gas-levels-output-models)
        - [Gas Levels Output Model](#gas-levels-output-model)
    - [Location Output Models](#location-output-models)
        - [Location Output Model](#location-output-model)
    - [Provider Output Models](#provider-output-models)
        - [Gas Measure List Output Model](#gas-measure-list-output-model)
        - [Gas Measure Output Model](#gas-measure-output-model)
        - [Provider List Output Model](#provider-list-output-model)
        - [Provider Output Model](#provider-output-model)
        - [Temperature Measure List Output Model](#temperature-measure-list-output-model)
        - [Temperature Measure Output Model](#temperature-measure-output-model)
    - [Tank Output Models](#tank-output-models)
        - [Add Tank Output Model](#add-tank-output-model)
        - [Tank List Output Model](#tank-list-output-model)
        - [Tank Output Model](#tank-output-model)
- [Error Handling](#error-handling)
    - [Problem Details](#problem-details)
    - [Bad Request](#bad-request)
    - [AGU Already Exists](#agu-already-exists)
    - [AGU Name Already Exists](#agu-name-already-exists)
    - [AGU Not Found](#agu-not-found)
    - [Contact Already Exists](#contact-already-exists)
    - [Invalid Capacity](#invalid-capacity)
    - [Invalid Contact](#invalid-contact)
    - [Invalid Contact Type](#invalid-contact-type)
    - [Invalid Coordinates](#invalid-coordinates)
    - [Invalid Critical Level](#invalid-critical-level)
    - [Invalid CUI](#invalid-cui)
    - [invalid Days](#invalid-days)
    - [Invalid DNO](#invalid-dno)
    - [Invalid EIC](#invalid-eic)
    - [Invalid Levels](#invalid-levels)
    - [Invalid Load Volume](#invalid-load-volume)
    - [Invalid Max Level](#invalid-max-level)
    - [Invalid Min Level](#invalid-min-level)
    - [Invalid Name](#invalid-name)
    - [Invalid Provider](#invalid-provider)
    - [Invalid Tank](#invalid-tank)
    - [Invalid Tank Number](#invalid-tank-number)
    - [Invalid Time](#invalid-time)
    - [Provider Not Found](#provider-not-found)
    - [Tank Already Exists](#tank-already-exists)
    - [Tank Not Found](#tank-not-found)

## Introduction

This document outlines the API endpoints and how to make requests to say API.
The API is a simple REST API that allows to interact with the AGU Data System.
The API is divided into several endpoints, each responsible for a different part of the system.

## Pagination

As of current implementation, the API does not support pagination, due to the quantity of data being small.

## API Endpoints

The API has the following endpoints:

### AGU

AGU Endpoint is responsible for managing AGUs in the system.

#### Get All AGUs

- **URL:** `/api/agus`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Basic Info List Output Model](#agu-basic-info-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus" -H "accept: application/json"
    ```

#### Get AGU by id

- **URL:** `/api/agus/{cui}`
- **Method:** `GET`
- **Path Variables:**
    - `cui` - The unique id of the AGU.
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Output Model](#agu-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/PT1234567890123456XX" -H "accept: application/json"
    ```

#### Create AGU

- **URL:** `/api/agus/create`
- **Method:** `POST`
- **Request Body:**
    - `application/json`
        - [AGU Creation Input Model](#agu-creation-input-model)
- **Success Response:**
- **Content:**
    - `application/json`
        - [AGU Creation Output Model](#agu-creation-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [Invalid CUI](#invalid-cui)
            - [Invalid Contact](#invalid-contact)
            - [Invalid Contact Type](#invalid-contact-type)
            - [Invalid Coordinates](#invalid-coordinates)
            - [Invalid DNO](#invalid-dno)
            - [Invalid Critical Level](#invalid-critical-level)
            - [Invalid Min Level](#invalid-min-level)
            - [Invalid Max Level](#invalid-max-level)
            - [Invalid Load Volume](#invalid-load-volume)
            - [Invalid Levels](#invalid-levels)
            - [Invalid Tank](#invalid-tank)
            - [Invalid Provider](#invalid-provider)
            - [Invalid Name](#invalid-name)
            - [AGU Already Exists](#agu-already-exists)
            - [AGU Name Already Exists](#agu-name-already-exists)
- **Sample Call:**
    ```shell
  curl -X POST "http://localhost:8080/api/agus/create" -H "accept: application/json" 
  ```    

[comment]: <> (TODO: add body to Requests)

#### Get Temperature Measures

- **URL:** `/api/agus/{cui}/temperature`
- **Method:** `GET`
- **Path Variables:**
    - `cui` - The unique id of the AGU.
- **Request Param:**
    - `days` - Number of days to get temperature measures.
        - **Default:** `10`
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Temperature Measure List Output Model](#temperature-measure-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Provider Not Found](#provider-not-found)
            - [Invalid Days](#invalid-days)
            - [Invalid Time](#invalid-time)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/PT1234567890123456XX/temperature" -H "accept: application/json"
    ```

#### Get Daily Gas Measures

- **URL:** `/api/agus/{cui}/gas/daily`
- **Method:** `GET`
- **Path Variables:**
    - `cui` - The unique id of the AGU.
- **Request Param:**
    - `days` - Number of days to get temperature measures.
        - **Default:** `10`
    - `time` - Time to get the measures at (Hour:minute).
        - **Default:** `09:00`
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Gas Measure List Output Model](#gas-measure-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Provider Not Found](#provider-not-found)
            - [Invalid Days](#invalid-days)
            - [Invalid Time](#invalid-time)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/PT1234567890123456XX/gas/daily" -H "accept: application/json"
    ```

#### Get Hourly Gas Measures

- **URL:** `/api/agus/{cui}/gas/hourly`
- **Method:** `GET`
- **Path Variables:**
    - `cui` - The unique id of the AGU.
- **Request Param:**
    - `day` - The day to get the measures from.
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Gas Measure List Output Model](#gas-measure-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Provider Not Found](#provider-not-found)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/PT1234567890123456XX/gas/hourly" -H "accept: application/json"
    ```

#### Get Prediction Gas Measures

- **URL:** `/api/agus/{cui}/gas/predictions`
- **Method:** `GET`
- **Path Variables:**
    - `cui` - The unique id of the AGU.
- **Request Param:**
    - `days` - Number of days to get temperature measures.
        - **Default:** `10`
    - `time` - Time to get the measures at (Hour:minute).
        - **Default:** `09:00`
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Gas Measure List Output Model](#gas-measure-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Provider Not Found](#provider-not-found)
            - [Invalid Days](#invalid-days)
            - [Invalid Time](#invalid-time)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/PT1234567890123456XX/gas/predictions" -H "accept: application/json"
    ```

#### Get Favourite AGUs

- **URL:** `/api/agus/favourite`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Basic Info List Output Model](#agu-basic-info-list-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [Bad Request](#bad-request)

          [comment]: <> (TODO Change bad request to something more meaningful)
- **Sample Call:**
    ```shell
    curl -X GET "http://localhost:8080/api/agus/favourite" -H "accept: application/json"
    ```

#### Update Favourite State

- **URL:** `/api/agus/{aguCui}/favourite`
- **Method:** `POST`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
- **Request Body:**
    - [Update Favourite AGU Input Model](#update-favorite-agu-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Output Model](#agu-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/favourite" -H "accept: application/json"
    ```

#### Add Contact

- **URL:** `/api/agus/{aguCui}/contact`
- **Method:** `POST`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
- **Request Body:**
    - [Contact Creation Input Model](#contact-creation-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Add Contact Output Model](#add-contact-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Invalid Contact](#invalid-contact)
            - [Invalid Contact Type](#invalid-contact-type)
            - [Contact Already Exists](#contact-already-exists)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/contact" -H "accept: application/json"
    ```

#### Delete Contact

- **URL:** `/api/agus/{aguCui}/contact/{contactId}`
- **Method:** `DELETE`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
    - `contactId` - The id of the Contact
- **Success Response:**
    - **Content:**
        - `nothing`
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/contact/1" -H "accept: application/json"
    ```

#### Add tank

- **URL:** `/api/agus/{aguCui}/tank`
- **Method:** `POST`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
- **Request Body:**
    - [Tank Creation Input Model](#tank-creation-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [Add Tank Output Model](#add-tank-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Invalid Levels](#invalid-levels)
            - [Tank Already Exists](#tank-already-exists)
            - [Invalid Capacity](#invalid-capacity)
            - [Invalid Load Volume](#invalid-load-volume)
            - [Invalid Tank Number](#invalid-tank-number)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/tank" -H "accept: application/json"
    ```

#### Update Tank

- **URL:** `/api/agus/{aguCui}/tank/{tankNumber}`
- **Method:** `PUT`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
    - `tankNumber` - The number of the Tank to update
- **Request Body:**
    - [Tank Update Input Model](#update-tank-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Output Model](#agu-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Invalid Levels](#invalid-levels)
            - [Tank Not Found](#tank-not-found)
            - [Invalid Capacity](#invalid-capacity)
            - [Invalid Load Volume](#invalid-load-volume)
            - [Invalid Tank Number](#invalid-tank-number)
            - [Invalid CUI](#invalid-cui)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/tank/1" -H "accept: application/json"
    ```

#### Change Gas Levels

- **URL:** `/api/agus/{aguCui}/levels`
- **Method:** `PUT`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
- **Request Body:**
    - [Gas Levels Input Model](#gas-levels-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Output Model](#agu-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
            - [Invalid Levels](#invalid-levels)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/levels" -H "accept: application/json"
    ```

#### Change Notes

- **URL:** `/api/agus/{aguCui}/notes`
- **Method:** `PUT`
- **Path Variables:**
    - `aguCui` - The unique id of the AGU.
- **Request Body:**
    - [Notes Input Model](#notes-input-model)
- **Success Response:**
    - **Content:**
        - `application/json`
            - [AGU Output Model](#agu-output-model)
- **Error Response:**
    - **Content:**
        - `application/json`
            - [AGU Not Found](#agu-not-found)
- **Sample Call:**
    ```shell
    curl -X POST "http://localhost:8080/api/agus/PT1234567890123456XX/notes" -H "accept: application/json"
    ```

## Input Models

### AGU Input Models

#### AGU Creation Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `cui`: The CUI of the AGU.
        - `name`: The name of the AGU.
        - `minLevel`: The minimum level of the AGU.
        - `maxLevel`: The maximum level of the AGU.
        - `criticalLevel`: The critical level of the AGU.
        - `loadVolume`: The load volume of the AGU.
        - `latitude`: The latitude of the AGU.
        - `longitude`: The longitude of the AGU.
        - `locationName`: The location name of the AGU.
        - `dnoCreation`: [DNO Creation Input Model](#dno-creation-input-model).
        - `gasLevelUrl`: The gas level URL of the AGU.
        - `image`: The image of the AGU.
        - `tanks`: [Tank Creation Input Model](#tank-creation-input-model) list.
        - `contacts`: [Contact Creation Input Model](#contact-creation-input-model) list.
        - `isFavorite`: The favorite status of the AGU.
    - **Optional:**
        - `notes`: The notes of the AGU.

- **Example:**

```json
{
    "cui": "string",
    "name": "string",
    "minLevel": 0,
    "maxLevel": 0,
    "criticalLevel": 0,
    "loadVolume": 0,
    "latitude": 0,
    "longitude": 0,
    "locationName": "string",
    "dnoCreation": {
        "name": "string",
        "region": "string"
    },
    "gasLevelUrl": "string",
    "image": "byte[]",
    "tanks": [
        {
            "name": "string",
            "capacity": 0,
            "level": 0,
            "isCritical": true
        }
    ],
    "contacts": [
        {
            "name": "string",
            "phone": "string",
            "email": "string"
        }
    ],
    "isFavorite": true,
    "notes": "string"
}
```

#### Notes Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `notes`: The notes of the AGU.

- **Example:**

```json
{
    "notes": "string"
}
```

#### Update Favorite AGU Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `isFavourite`: The favorite status of the AGU.

- **Example:**

```json
{
    "isFavourite": true
}
```

### Contact Input Models

#### Contact Creation Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name`: The name of the contact.
        - `phone`: The phone of the contact.
        - `email`: The email of the contact.

- **Example:**

```json
{
    "name": "string",
    "phone": "string",
    "email": "string"
}
```

### DNO Input Models

#### DNO Creation Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name`: The name of the DNO.
        - `region`: The region of the DNO.
- **Example:**

```json
{
    "name": "string",
    "region": "string"
}
```

### Gas Levels Input Models

#### Gas Levels Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `min`: The minimum level of the AGU.
        - `max`: The maximum level of the AGU.
        - `critical`: The critical level of the AGU.

- **Example:**

```json
{
    "min": 0,
    "max": 0,
    "critical": 0
}
```

### Tank Input Models

#### Tank Creation Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `number`: The number of the tank.
        - `minLevel`: The minimum level of the tank.
        - `maxLevel`: The maximum level of the tank.
        - `criticalLevel`: The critical level of the tank.
        - `loadVolume`: The load volume of the tank.
        - `capacity`: The capacity of the tank.
        - `correctionFactor`: The correction factor of the tank.

- **Example:**

```json
{
    "number": 0,
    "minLevel": 0,
    "maxLevel": 0,
    "criticalLevel": 0,
    "loadVolume": 0.0,
    "capacity": 0,
    "correctionFactor": 0.0
}
```

#### Update Tank Input Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `minLevel`: The minimum level of the tank.
        - `maxLevel`: The maximum level of the tank.
        - `criticalLevel`: The critical level of the tank.
        - `loadVolume`: The load volume of the tank.
        - `capacity`: The capacity of the tank.
        - `correctionFactor`: The correction factor of the tank.

- **Example:**

```json
{
    "minLevel": 0,
    "maxLevel": 0,
    "criticalLevel": 0,
    "loadVolume": 0.0,
    "capacity": 0,
    "correctionFactor": 0.0
}
```

## Output Models

### AGU Output Models

#### AGU Basic Info Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `cui`: The CUI of the AGU.
        - `name`: The name of the AGU.
        - `dno`: [DNO Output Model](#dno-output-model).
        - `location`: [Location Output Model](#location-output-model).

- **Example:**

```json
{
    "cui": "string",
    "name": "string",
    "dno": {
        "id": 0,
        "name": "string"
    },
    "location": {
        "name": "string",
        "latitude": 0.0,
        "longitude": 0.0
    }
}
```

#### AGU Basic Info List Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `agusBasicInfo`: [AGU Basic Info Output Model](#agu-basic-info-output-model) list.
        - `size`: The size of the list.

- **Example:**

```json
{
    "agusBasicInfo": [
        {
            "cui": "string",
            "name": "string",
            "dno": {
                "id": 0,
                "name": "string"
            },
            "location": {
                "name": "string",
                "latitude": 0.0,
                "longitude": 0.0
            }
        }
    ],
    "size": 1
}
```

#### AGU Creation Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `cui`: The CUI of the AGU.

- **Example:**

```json
{
    "cui": "string"
}
```

#### AGU Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `cui`: The CUI of the AGU.
        - `name`: The name of the AGU.
        - `levels`: [Gas Levels Output Model](#gas-levels-output-model).
        - `loadVolume`: The load volume of the AGU.
        - `location`: [Location Output Model](#location-output-model).
        - `dno`: [DNO Output Model](#dno-output-model).
        - `image`: The image of the AGU.
        - `contacts`: [Contact List Output Model](#contact-list-output-model).
        - `tanks`: [Tank List Output Model](#tank-list-output-model).
        - `providers`: [Provider List Output Model](#provider-list-output-model).
        - `isFavorite`: The favorite status of the AGU.
    - **Optional:**
        - `notes`: The notes of the AGU.
        - `training`: The training of the AGU.

- **Example:**

```json
{
    "cui": "string",
    "name": "string",
    "levels": {
        "min": 0,
        "max": 0,
        "critical": 0
    },
    "loadVolume": 0,
    "location": {
        "name": "string",
        "latitude": 0.0,
        "longitude": 0.0
    },
    "dno": {
        "id": 0,
        "name": "string"
    },
    "image": "byte[]",
    "contacts": {
        "contacts": [
            {
                "id": 0,
                "name": "string",
                "phone": "string",
                "type": "emergency"
            }
        ],
        "size": 1
    },
    "tanks": {
        "tanks": [
            {
                "number": 0,
                "levels": {
                    "min": 0,
                    "max": 0,
                    "critical": 0
                },
                "loadVolume": 0,
                "capacity": 0,
                "correctionFactor": 0.0
            }
        ],
        "size": 1
    },
    "providers": {
        "providers": [
            {
                "id": 0,
                "measures": {
                    "gasMeasures": {
                        "gasMeasures": [
                            {
                                "id": 0,
                                "date": "2024-03-18T12:28:34.971+00:00",
                                "level": 0
                            }
                        ],
                        "size": 1
                    },
                    "temperatureMeasures": {
                        "temperatureMeasures": [
                            {
                                "timestamp": "2024-03-18T12:28:34.971+00:00",
                                "predictionFor": "2024-03-18T12:28:34.971+00:00",
                                "min": 0,
                                "max": 0
                            }
                        ],
                        "size": 1
                    }
                },
                "lastFetch": "2024-03-18T12:28:34.971+00:00",
                "type": "string"
            }
        ],
        "size": 1
    },
    "isFavorite": true,
    "notes": "string"
}
```

### Contact Output Models

#### Add Contact Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id`: The id of the contact.

- **Example:**

```json
{
    "id": 0
}
```

#### Contact List Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `contacts`: [Contact Output Model](#contact-output-model) list.
        - `size`: The size of the list.

- **Example:**

```json
{
    "contacts": [
        {
            "id": 0,
            "name": "string",
            "phone": "string",
            "type": "emergency"
        }
    ],
    "size": 1
}
```

#### Contact Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id`: The id of the contact.
        - `name`: The name of the contact.
        - `phone`: The phone of the contact.
        - `type`: The type of the contact.

- **Example:**

```json
{
    "id": 0,
    "name": "xpto",
    "phone": "123456789",
    "type": "emergency"
}
```

### DNO Output Models

### DNO Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id`: The id of the DNO.
        - `name`: The name of the DNO.
        - `region`: The region of the DNO.

- **Example:**

```json
{
    "id": 0,
    "name": "string",
    "region": "string"
}
```

### Gas Levels Output Models

#### Gas Levels Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `min`: The minimum level of the AGU.
        - `max`: The maximum level of the AGU.
        - `critical`: The critical level of the AGU.

- **Example:**

```json
{
    "min": 0,
    "max": 0,
    "critical": 0
}
```

### Location Output Models

#### Location Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `name`: The name of the location.
        - `latitude`: The latitude of the location.
        - `longitude`: The longitude of the location.

- **Example:**

```json
{
    "name": "string",
    "latitude": 0.0,
    "longitude": 0.0
}
```

### Provider Output Models

#### Gas Measure List Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `gasMeasures`: [Gas Measure Output Model](#gas-measure-output-model) list.
        - `size`: The size of the list.
- **Example:**

```json
{
    "gasMeasures": [
        {
            "timestamp": "2024-03-18T12:28:34.971+00:00",
            "predictionFor": "2024-03-18T12:28:34.971+00:00",
            "level": 0,
            "tankNumber": 0
        }
    ],
    "size": 1
}
```

#### Gas Measure Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `timestamp`: The timestamp of the measure.
        - `predictionFor`: The prediction date of the measure if any.
        - `level`: The level of the gas measure.
        - `tankNumber`: The tank number of the gas measure.
- **Example:**

```json
{
    "timestamp": "2024-03-18T12:28:34.971+00:00",
    "predictionFor": "2024-03-18T12:28:34.971+00:00",
    "level": 0,
    "tankNumber": 0
}
```

#### Provider List Output Model

data class ProviderListOutputModel(
val providers: List<ProviderOutputModel>,
val size: Int
)

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `providers`: [Provider Output Model](#provider-output-model) list.
        - `size`: The size of the list.
- **Example:**

```json
{
    "providers": [
        {
            "id": 0,
            "measures": {
                "gasMeasures": {
                    "gasMeasures": [
                        {
                            "id": 0,
                            "date": "2024-03-18T12:28:34.971+00:00",
                            "level": 0
                        }
                    ],
                    "size": 1
                },
                "temperatureMeasures": {
                    "temperatureMeasures": [
                        {
                            "timestamp": "2024-03-18T12:28:34.971+00:00",
                            "predictionFor": "2024-03-18T12:28:34.971+00:00",
                            "min": 0,
                            "max": 0
                        }
                    ],
                    "size": 1
                }
            },
            "lastFetch": "2024-03-18T12:28:34.971+00:00",
            "type": "string"
        }
    ],
    "size": 1
}
```

#### Provider Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id`: The id of the provider.
        - `measures`: The measures of the provider.
        - `lastFetch`: The last fetch of the provider.
        - `type`: The type of the provider.
- **Example:**

```json
{
    "id": 0,
    "measures": {
        "gasMeasures": {
            "gasMeasures": [
                {
                    "id": 0,
                    "date": "2024-03-18T12:28:34.971+00:00",
                    "level": 0
                }
            ],
            "size": 1
        },
        "temperatureMeasures": {
            "temperatureMeasures": [
                {
                    "timestamp": "2024-03-18T12:28:34.971+00:00",
                    "predictionFor": "2024-03-18T12:28:34.971+00:00",
                    "min": 0,
                    "max": 0
                }
            ],
            "size": 1
        }
    },
    "lastFetch": "2024-03-18T12:28:34.971+00:00",
    "type": "string"
}
```

#### Temperature Measure List Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `temperatureMeasures`: [Temperature Measure Output Model](#temperature-measure-output-model) list.
        - `size`: The size of the list.
- **Example:**

```json
{
    "temperatureMeasures": [
        {
            "timestamp": "2024-03-18T12:28:34.971+00:00",
            "predictionFor": "2024-03-18T12:28:34.971+00:00",
            "min": 0,
            "max": 10
        }
    ],
    "size": 1
}
```

#### Temperature Measure Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `timestamp`: The timestamp of the measure.
        - `predictionFor`: The prediction date of the measure if any.
        - `min`: The minimum temperature of the measure.
        - `max`: The maximum temperature of the measure.
- **Example:**

```json
{
    "timestamp": "2024-03-18T12:28:34.971+00:00",
    "predictionFor": "2024-03-18T12:28:34.971+00:00",
    "min": 0,
    "max": 10
}
```

### Tank Output Models

#### Add Tank Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `number`: The number of the tank.
- **Example:**

```json
{
    "number": 0
}
```

#### Tank List Output Model

data class TankListOutputModel(
val tanks: List<TankOutputModel>,
val size: Int
)

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `tanks`: [Tank Output Model](#tank-output-model) list.
        - `size`: The size of the list.
- **Example:**

```json
{
    "tanks": [
        {
            "number": 0,
            "levels": {
                "min": 0,
                "max": 0,
                "critical": 0
            },
            "loadVolume": 0,
            "capacity": 0,
            "correctionFactor": 0.0
        }
    ],
    "size": 1
}
```

#### Tank Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `number`: The number of the tank.
        - `levels`: [Gas Levels Output Model](#gas-levels-output-model).
        - `loadVolume`: The load volume of the tank.
        - `capacity`: The capacity of the tank.
        - `correctionFactor`: The correction factor of the tank.
- **Example:**

```json
{
    "number": 0,
    "levels": {
        "min": 0,
        "max": 0,
        "critical": 0
    },
    "loadVolume": 0,
    "capacity": 0,
    "correctionFactor": 0.0
}
```

## Error Handling

The problems are returned in the following format:

### Problem Details

- **Type:** `application/problem+json`
- **Attributes:**
    - **Required:**
        - `title` - The title of the error.
        - `details` - The details of the error.
        - `type` - The type of the error.

### Bad Request

- **Type:** `application/problem+json`
- **Attributes:**
    - **Required:**
        - `timestamp` - The time the error occurred.
        - `status` - The status of the error.
        - `error` - The error message.
        - `path` - The path of the request that caused the error.
- **Example:**

```json
{
    "timestamp": "2024-03-18T12:28:34.971+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/provider/a"
}
```

### AGU Already Exists

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "AGU Already Exists.",
    "details": "The AGU already exists with the given CUI.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/agu-already-exists"
}
```

### AGU Not Found

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "AGU Not Found.",
    "details": "The AGU with the given CUI was not found.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/agu-not-found"
}     
```

### AGU Name Already Exists

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "AGU Name Already Exists.",
    "details": "The AGU already exists with the given name.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/agu-name-already-exists"
}
```

### Contact Already Exists

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Contact Already Exists.",
    "details": "The contact already exists.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/contact-already-exists"
}
```

### DNO Not Found

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "DNO Not Found.",
    "details": "The DNO with the given ID or name was not found.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/dno-not-found"
}
```

### Invalid Capacity

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Capacity.",
    "details": "The capacity must be positive.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-capacity"
}
```

### Invalid Contact

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Contact.",
    "details": "The contact must have a name and a phone number in the format '^PT[0-9]{16}[A-Z]{2}$'.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-contact"
}
```

### Invalid Contact Type

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Contact Type.",
    "details": "The contact type must be in the format '^(LOGISTIC|EMERGENCY)$'",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-contact-type"
}
```

### Invalid Coordinates

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Coordinates.",
    "details": "The coordinates must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-coordinates"
}
```

### Invalid Critical Level

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Critical Level.",
    "details": "The critical level must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-critical-level"
}
```

### Invalid CUI

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid CUI.",
    "details": "The CUI must be in the format '^PT[0-9]{16}[A-Z]{2}$'.",
    "type": "https:///github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-cui"
}
```

### Invalid Days

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Days.",
    "details": "The days must be positive.",
    "type": "https:///github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-days"
}
```

### Invalid DNO

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid DNO.",
    "details": "The DNO must have a name.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-dno"
}
```

### Invalid EIC

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid EIC.",
    "details": "The EIC must not be blank.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-eic"
}
```

### Invalid Levels

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Levels.",
    "details": "The levels must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-levels"
}
```

### Invalid Load Volume

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Load Volume.",
    "details": "The load volume must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-load-volume"
}
```

### Invalid Max Level

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Max Level.",
    "details": "The max level must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-max-level"
}
```

### Invalid Min Level

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Min Level.",
    "details": "The min level must be valid.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-min-level"
}
```

### Invalid Name

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Name.",
    "details": "The name must have a value.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-name"
}
```

### Invalid Provider

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Provider.",
    "details": "Couldn't add the provider to the periodic fetcher.",
    "type": "https:///github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-provider"
}
```

### Invalid Tank

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Tank.",
    "details": "The tank must have a name and a volume.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-tank"
}
```

### Invalid Tank number

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Tank Number.",
    "details": "The tank number must be positive.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-tank-number"
}
```

### Invalid Time

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Invalid Time.",
    "details": "The time must be between 00:00 and 23:59.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/invalid-time"
}
```

### Provider Not Found

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Provider Not Found.",
    "details": "The provider with the given ID was not found.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/provider-not-found"
}
```

### Tank Already Exists

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Tank Already Exists.",
    "details": "The tank already exists.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/tank-already-exists"
}
```

### Tank Not Found

- **Structure:**
    - [Problem Details](#problem-details)
- **Example:**

```json
{
    "title": "Tank Not Found.",
    "details": "The tank with the given ID was not found.",
    "type": "https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/tank-not-found"
}
```
