# AGU Data System Frontend Documentation

> This is the documentation for the frontend of the AGU Data System project.

## Table of Contents

- [Introduction](#introduction)
- [Code Structure](#code-structure)
- [API Connection](#api-connection)
- [Conclusion](#conclusion)

## Introduction

The frontend of the AGU Data System project is written in TypeScript, using the React framework, it is also a single page application.
The frontend is responsible for the user interface of the application.
It is also responsible for the communication with the backend.

This application is a client for the AGU Data System API, which is documented [here](../jvm/API-doc.md).
For more information about the backend, please refer to the [backend documentation](../jvm/README.md).

## Code Structure

The code is structured in the following way:

- `js`
    - `public/` - The folder with the index HTML file and website icon.
    - `src/`
        - `assets/` - Contains the images and other assets used in the application;
        - `components/` - Contains the React components and pages used in the application;
        - `service/` - Contains the services used in the application; this layer is responsible for the communication
          with the API;
        - `utils/` - The utils files of the application
        - `index.css` - The CSS file of the application
        - `index.tsx` - The entry point of the application
        - `router.tsx` - The router for the application

In the `js` folder, there are other files used for the development of the application, like the `package.json` file,
the `tsconfig.json` file and the `webpack.config.js` file.

## API Connection

The API connectivity is done by the service layer.

The media types used in the communication with the API are the following:

* `application/json` - Used in the request bodies;
* `application/problem+json` - Used in the response bodies when an error occurs;

To make the requests, the `fetch` API is used. A `fetchFuntion` function was implemented to make the requests to the API
and to parse the response body to a `Json` object or to a `Problem` object, depending on the media type of the response.

For each request method (GET, POST, PUT, DELETE), a function that calls the `fechFuntion` function was implemented, to
simplify the code.

## Conclusion

The AGU Data System frontend is a robust and well-structured application built with TypeScript and React. 
Its modular design enhances maintainability and scalability, making it easier for developers to manage and extend functionality. 
By leveraging a dedicated service layer for API communication, the frontend ensures efficient and consistent interaction with the backend, adhering to best practices in web development.

This documentation provides a comprehensive overview of the project's architecture and components, facilitating easier onboarding for new developers and offering a clear reference for existing team members.
For any further assistance or detailed inquiries, please refer to the API and backend documentation linked above.