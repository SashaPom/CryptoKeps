import React from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App";
import "./styles.css";

import Bots from "./pages/Bots";
import Accounts from "./pages/Accounts";
import Monitor from "./pages/Monitor";
import RiskManager from "./pages/RiskManager";

import {loadBasicAuthFromStorage, setBasicAuth} from "./api/client";

loadBasicAuthFromStorage();

// // только в dev:
// if (import.meta.env.DEV) {
//     // @ts-ignore
//     const u = import.meta.env.VITE_GATEWAY_USER;
//     // @ts-ignore
//     const p = import.meta.env.VITE_GATEWAY_PASS;
//     if (u && p) setBasicAuth(u, p);
// }

setBasicAuth("ck_admin", "supersecret");

const router = createBrowserRouter([
    {
        path: "/",
        element: <App />,
        children: [
            { index: true, element: <Bots /> },
            { path: "bots", element: <Bots /> },
            { path: "accounts", element: <Accounts /> },
            { path: "monitor", element: <Monitor /> },
            { path: "risk", element: <RiskManager /> },
        ]
    }
]);

createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>
);

