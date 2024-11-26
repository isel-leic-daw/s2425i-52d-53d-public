import * as React from "react"

import { ThemeContext } from "./ThemeProvider"

export function ThemeSwitcher() {
    const {theme, setTheme} = React.useContext(ThemeContext)
    return (
        <div>
            <p>Current theme: {theme}</p>
            <button className="button" onClick={() => setTheme("light")}>Light Mode</button>
            <button className="button" onClick={() => setTheme("dark")}>Dark Mode</button>
            <button className="button" onClick={() => setTheme("colorful")}>Colorful Mode</button>
        </div>
    )
}