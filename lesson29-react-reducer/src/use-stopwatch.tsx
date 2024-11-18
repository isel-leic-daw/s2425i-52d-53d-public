import * as React from "react"

type State = {
    init: number,
    elapsedInMillis: number,
    isRunning: boolean
}

type Action =  { type: 'stop' }
    | { type: 'start' }
    | { type: 'running'}
    | { type: 'lap'}

function reduce(state: State, action: Action): State {
    switch(action.type) {
        case "stop": return { init: 0, elapsedInMillis: 0, isRunning: false}
        case "start": return { init: Date.now(), elapsedInMillis: 0, isRunning: true }
        case "lap": return { init: state.init, elapsedInMillis: Date.now() - state.init, isRunning: false}
        case "running": return { init: state.init, elapsedInMillis: Date.now() - state.init, isRunning: true}
    }
}

export function useStopwatch() : [State, { 
    onStart: () => void,
    onResumeOrLap: () => void,
    onStop: () => void
}] {
    const [state, dispatch ] = React.useReducer(reduce, {
        init: 0,
        elapsedInMillis: 0, 
        isRunning: false
    })

    function onStart() { dispatch({type: "start"}) }
    function onResumeOrLap() {
        if(state.isRunning) { dispatch({type: "lap"})}
        else {dispatch({type: "running"})}
    }
    function onStop() { dispatch({type: "stop"}) }

    React.useEffect(() => {
        let timeoutId: NodeJS.Timeout
        if (state.isRunning) {
            timeoutId = setTimeout(() => dispatch({ type: "running"}), 10)            
        }
        return () => { clearTimeout(timeoutId) }
    }, [state.isRunning, state.elapsedInMillis])

    return [state, {onStart: onStart, onStop: onStop, onResumeOrLap: onResumeOrLap}]
}