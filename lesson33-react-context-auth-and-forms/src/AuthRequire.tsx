import * as React from "react"
import { AuthContext } from "./AuthProvider"
import { Navigate, useLocation } from "react-router-dom"

export function AuthRequire({ children }: { children: React.ReactNode }) {
    const { username } = React.useContext(AuthContext)
    const location = useLocation()
    if (username) { return <>{children}</> }
    else {
        return <Navigate to={"/login"} state={{source: location.pathname}}></Navigate>
    }
}
