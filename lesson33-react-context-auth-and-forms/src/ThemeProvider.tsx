import * as React from 'react'
import { createContext, useState } from 'react'

type Theme = "light" | "dark" | "colorful"

//Define the Context Type
type ThemeContextType = {
    theme: Theme,
    setTheme: (newTheme: Theme) => void
}

// Create the context
export const ThemeContext = createContext<ThemeContextType>({
    theme: "light",
    setTheme: () => { throw Error("Not implemented!") }
})

// Define a Provider Component
export function ThemeProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const [theme, setTheme] = useState<Theme>("light")
    return (
        <ThemeContext.Provider value={{ theme, setTheme }}>
            {children}
        </ThemeContext.Provider>
    )
}
