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

type State = {
    pass: string, 
    error: string,   
}

type Action = { type: "new-password", pass: string }
    | { type: "validation-result", pass: string, res: true | string }

function reduce(state: State, action: Action): State {
    switch(action.type) {
        case "new-password": return { pass: action.pass, error: undefined }
        case "validation-result": return state.pass != action.pass // the former impl with reduce has no access to current state
            ? { pass: state.pass, error: undefined }
            : { pass: state.pass, error: action.res === true ? undefined : action.res }
    }
}

function WeakChecker() {
    const [state, dispatch] = React.useReducer(reduce, {
        pass: "", error: ""
    })

    useEffect(() => {
        const tid = setTimeout(async () => {
            // makes IO and is an async operations
            console.log("Validating ... " + state.pass)
            const res: string | true = await validatePassword(state.pass)
            dispatch({ type: "validation-result", pass: state.pass, res: res })
            console.log("Validation COMPLETE for " + state.pass)
        }, 500)
        return () => { 
            console.log("Cancelling schedule of a Validation effect!!!")    
            clearTimeout(tid)
        }
    }, [state.pass])

    async function inputHandler(event: React.ChangeEvent<HTMLInputElement>) {
        const input = event.target.value
        dispatch( { type: "new-password", pass: input })
    }
    return (
        <div>
            Password: 
            <input value={state.pass} onChange={inputHandler} type="text"></input>
            <p>{state.error}</p>
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
