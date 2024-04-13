import * as React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NavBar from './Components/Layouts/NavBar';
import NotFoundPage from './Components/NotFoundPage';
import Home from './Components/Home';

const router = createBrowserRouter([
    {
      'path': '/',
      'element': <NavBar />,
      'children': [
        {
          'path': '/',
          'element': <Home />,
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