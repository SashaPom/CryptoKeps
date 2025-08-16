import React from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App";
import "./styles.css";

import Bots from "./pages/Bots";
import Accounts from "./pages/Accounts";
import Monitor from "./pages/Monitor";

const router = createBrowserRouter([
    {
        path: "/",
        element: <App />,
        children: [
            { index: true, element: <Bots /> },
            { path: "bots", element: <Bots /> },
            { path: "accounts", element: <Accounts /> },
            { path: "monitor", element: <Monitor /> }
        ]
    }
]);

createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>
);
