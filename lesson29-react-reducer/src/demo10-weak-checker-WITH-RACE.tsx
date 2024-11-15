import { useState, useEffect } from "react"
import * as React from "react"
import * as ReactDOM from "react-dom/client"
import { worstPasswords } from "./demo10-worst-passwords"

const container: HTMLElement = document.getElementById('container')

export function demo10() {
    const root = ReactDOM.createRoot(container)
    root.render(
        <WeakChecker></WeakChecker>
    )
}
function WeakChecker() {
    const [pass, setPass] = useState("")
    const [error, setError] = useState("")

    useEffect(() => {
        const tid = setTimeout(async () => {
            // makes IO and is an async operations
            console.log("Validating ... " + pass)
            const res: string | true = await validatePassword(pass)
            if( typeof res === "string" ) { // !!!!! && validatedPass === actualPass !!! cannot make this
                setError(res)
            }
            console.log("Validation COMPLETE for " + pass)
        }, 500)
        return () => { 
            console.log("Cancelling schedule of a Validation effect!!!")    
            clearTimeout(tid)
        }
    }, [pass])

    async function inputHandler(event: React.ChangeEvent<HTMLInputElement>) {
        const input = event.target.value
        setPass(input)
        setError("")
    }
    return (
        <div>
            Password: 
            <input value={pass} onChange={inputHandler} type="text"></input>
            <p>{error}</p>
        </div>
    )
}
/**
 * Simulates an external service (IO) that checks if the
 * password is weak or nor.
 * @param password 
 */
async function validatePassword(password: string): Promise<true | string> {
    await delay(2000)
    return validatePasswordSync(password)
}

function delay(ms: number) : Promise<undefined> {
    return new Promise((resolve, reject) => {
        setTimeout(() => resolve(undefined), ms)
    })
}

function validatePasswordSync(password: string): true | string {
    const minLength = 8;
    const commonWords = worstPasswords.filter(pass => password.toLocaleLowerCase().includes(pass))
    if (commonWords.length > 0) {
        return `Don't use ${commonWords[0].toUpperCase()} on your password!`;
    }
    if (password.length < minLength) {
        return `Password must be at least ${minLength} characters long.`;
    }
    if (!/[A-Z]/.test(password)) {
        return "Password must contain at least one uppercase letter.";
    }
    if (!/[a-z]/.test(password)) {
        return "Password must contain at least one lowercase letter.";
    }  
    if (!/[0-9]/.test(password)) {
        return "Password must contain at least one number.";
    }
    // if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    //     return "Password must contain at least one special character.";
    // }
    // If all checks pass
    return true;
}
