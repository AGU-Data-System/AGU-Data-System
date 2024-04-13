import * as React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NavBar from './Components/Layouts/NavBar';
import NotFoundPage from './Components/NotFoundPage';
import FavBar from "./Components/FavBar/FavBar";

const router = createBrowserRouter([
    {
      'path': '/',
      'element': <NavBar />,
      'children': [
        {
          'path': '/',
          'element': <FavBar />,
        },
      ]
    },
    {
      'path': '*',
      'element': <NotFoundPage />,
    },
  ]
)

export function Router() {
  return (
    <RouterProvider router={router}/>
  )
}