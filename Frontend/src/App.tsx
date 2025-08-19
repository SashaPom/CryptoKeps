import React, {useEffect, useState} from "react";
import {Link, Outlet, useLocation} from "react-router-dom";
import logoUrl from "./assets/logo.png";

function useTheme() {
    const [theme, setTheme] = useState<"light" | "dark">(() => {
        const saved = localStorage.getItem("ck-theme");
        return (saved as any) || (window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");
    });
    useEffect(() => {
        const root = document.documentElement;
        if (theme === "dark") root.classList.add("dark"); else root.classList.remove("dark");
        localStorage.setItem("ck-theme", theme);
    }, [theme]);
    return {theme, setTheme} as const;
}

export default function App() {
    const {pathname} = useLocation();
    const {theme, setTheme} = useTheme();
    const NavLink = ({to, label}: { to: string; label: string }) => (
        <Link
            to={to}
            className={`text-sm px-2 py-1 rounded-lg ${
                pathname === to || (to === "/bots" && pathname === "/")
                    ? "text-neon"
                    : "text-gray-300 hover:text-neon"
            }`}
        >
            {label}
        </Link>
    );

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-black">
            <header className="sticky top-0 z-10 bg-black/90 backdrop-blur border-b border-white/10">
                <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
                    <Link to="/" className="flex items-center gap-2">
                        <img src={logoUrl} alt="CryptoKeps" className="h-6 w-6"/>
                        <span className="font-semibold text-white">CryptoKeps</span>
                    </Link>
                    <nav className="flex items-center gap-2">
                        <NavLink to="/bots" label="Bots"/>
                        <NavLink to="/accounts" label="Accounts"/>
                        <NavLink to="/monitor" label="Monitor"/>
                        <NavLink to="/risk" label="Risk Manager" />
                        <button
                            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
                            className="ml-3 text-xs px-2 py-1 rounded-lg border border-white/20 text-white hover:bg-white/10"
                            aria-label="Toggle theme"
                            title="Toggle theme"
                        >
                            {theme === "dark" ? "Light" : "Dark"}
                        </button>
                    </nav>
                </div>
            </header>
            <Outlet/>
        </div>
    );
}