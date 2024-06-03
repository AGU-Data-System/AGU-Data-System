# AGU Data System - API Documentation

## Table of Contents

- [Introduction](#introduction)
- [Pagination](#pagination)
- [API Endpoints](#api-endpoints)
  - [AGU](#agu)
    - [Get All AGUs](#get-all-agus)
- [Input Models](#input-models)
  - [Update Tank Input Model](#update-tank-input-model)
  - [Update Notes Input Model](#update-notes-input-model)
  - [Update Favorite AGU Input Model](#update-favorite-agu-input-model)
  - [Gas Levels Input model](#gas-levels-input-model)
  - [Tank Creation Input Model](#tank-creation-input-model)
  - [Contact Creation Input Model](#contact-creation-input-model)
  - [AGU Creation Input Model](#agu-creation-input-model)
- [Output Models](#output-models)
  - [Location Output Model](#location-output-model)
  - [DNO Output Model](#dno-output-model)
  - [AGU Basic Info Output Model](#agu-basic-info-output-model)
  - [AGU Basic Info List Output Model](#agu-basic-info-list-output-model)
- [Error Handling](#error-handling)
  - [Bad Request](#bad-request)

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
            - [Bad Request](#bad-request)
- **Sample Call:**
    ```shell
    curl 
    ```

## Input Models

### Update Tank Input Model

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

### Update Notes Input Model

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

### Update Favorite AGU Input Model

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

### Gas Levels Input model

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

### Tank Creation Input Model

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

### Contact Creation Input Model

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

### AGU Creation Input Model

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
        - `dnoName`: The DNO name of the AGU.
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
    "dnoName": "string",
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

## Output Models

### Location Output Model

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

### DNO Output Model

- **Type:** `application/json`
- **Attributes:**
    - **Required:**
        - `id`: The id of the DNO.
        - `name`: The name of the DNO.

- **Example:** 
```json
{
    "id": 0,
    "name": "string"
}
```

### AGU Basic Info Output Model

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

### AGU Basic Info List Output Model

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

## Error Handling

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
