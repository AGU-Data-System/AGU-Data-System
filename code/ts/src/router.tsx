import * as React from 'react';
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NavBar from './Components/Layouts/NavBar';
import NotFoundPage from './Components/NotFoundPage';
import Home from './Components/Home';
import Agu from './Components/Agu/Agu'

const router = createBrowserRouter([
    {
      'path': '/',
      'element': <NavBar />,
      'children': [
          {
              'path': '/',
              'element': <Home />,
          },
          {
              'path': '/uag/:aguId',
              'element': <Agu />,
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