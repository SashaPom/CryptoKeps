import React from "react";
import { useTheme } from "./ThemeContext";

export default function ThemeToggle() {
    const { theme, toggleTheme } = useTheme();

    return (
        <button
            onClick={toggleTheme}
            className="px-3 py-1 rounded border border-gray-400 dark:border-gray-600 hover:bg-gray-200 dark:hover:bg-gray-800 transition"
        >
            {theme === "light" ? "🌙 Dark" : "☀️ Light"}
        </button>
    );
}
