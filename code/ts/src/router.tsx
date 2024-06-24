import * as React from 'react';
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NavBar from './Components/Layouts/NavBar';
import NotFoundPage from './Components/NotFoundPage';
import Home from './Components/Home';
import Agu from './Components/Agu/Agu';
import AguCreate from './Components/Agu/AguCreate';
import AguEdit from './Components/Agu/AguEdit';
import WeeklyPlan from "./Components/WeeklyPlan/WeeklyPlan";

interface RouterProps {
    isDarkMode: boolean;
    toggleTheme: () => void;
}

const createRouter = (isDarkMode: boolean, toggleTheme: () => void) =>
    createBrowserRouter([
        {
            path: '/',
            element: <NavBar isDarkMode={isDarkMode} toggleTheme={toggleTheme} />,
            children: [
                {
                    path: '/',
                    element: <Home darkMode={isDarkMode} />,
                },
                {
                    path: '/uag/create',
                    element: <AguCreate />,
                },
                {
                    path: '/uag/:aguId',
                    element: <Agu />,
                },
                {
                    path: '/uag/:aguId/edit',
                    element: <AguEdit />,
                },
                {
                    path: '/weekly-plan',
                    element: <WeeklyPlan />,
                },
            ],
        },
        {
            path: '*',
            element: <NotFoundPage />,
        },
    ]);

export function Router({ isDarkMode, toggleTheme }: RouterProps) {
    const router = createRouter(isDarkMode, toggleTheme);
    return <RouterProvider router={router} />;
}