import * as React from 'react'

type AuthContextType = {
    username: string | undefined;
    setUsername: (v: string | undefined) => void
}

export const AuthContext = React.createContext<AuthContextType>({
    username: undefined,
    setUsername: () => { throw Error("Not implemented!") }
})

export function AuthProvider({children} : { children: React.ReactNode}) {
    const [user, setUser] = React.useState(undefined)
    return (
        <AuthContext.Provider value={{username: user, setUsername: setUser}}>
            {children}
        </AuthContext.Provider>
    )
}