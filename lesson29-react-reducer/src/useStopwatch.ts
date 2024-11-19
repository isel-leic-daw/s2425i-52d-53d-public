import * as React from "react"
import { useEffect } from "react"

type State = {
    init: number,            // Date Time when stropwatch has started
    elapsedInMillis: number, // Duration in Millis from the start
    isRunning: boolean       // If is running or not
}

type Action = { type: "stop", }
    | { type: "running", currentTime: number }
    | { type: "resumeOrLap", currentTime: number }

function reduce(state: State, action: Action): State {
    switch(action.type) {
        case "stop": return { init: 0, elapsedInMillis: 0, isRunning: false}
        case "resumeOrLap": return {
            init: state.init == 0 ? Date.now() : state.init,
            elapsedInMillis: action.currentTime - state.init,
            isRunning: !state.isRunning
        }
        case "running": return {
            init: state.init, 
            elapsedInMillis: action.currentTime - state.init,
            isRunning: true
        }
    }
}

export function useStopwatch() : [State, {
    onStop: () => void,
    onResumeOrLap: () => void,
}]{
    const [state, dispatch] = React.useReducer(reduce, {
        init: 0,
        elapsedInMillis: 0,
        isRunning: false}
    )
    useEffect(() => {
        let timeoutId: NodeJS.Timeout
        if (state.isRunning) {
            timeoutId = setTimeout(() => runningHandler(), 10)            
        }
        return () => { clearTimeout(timeoutId) }
    }, [state.isRunning, state.elapsedInMillis])

    function stopHandler() { dispatch({type: "stop" })}
    function resumeOrLapHandler() { dispatch({type: "resumeOrLap", currentTime: Date.now()})}
    function runningHandler() { dispatch({type: "running", currentTime: Date.now()})}

    return [ state, {
        onResumeOrLap: resumeOrLapHandler,
        onStop: stopHandler,
    } ]
}