import { useState, useEffect } from "react"
import * as React from "react"
import * as ReactDOM from "react-dom/client"
import { useStopwatch } from "./useStopwatch"

const SECOND = 1000
const MINUTE = SECOND * 60
const HOUR = MINUTE * 60

function Stopwatch() {
    const [{init, elapsedInMillis, isRunning}, handlers] = useStopwatch()

    // calculation
    const hours = Math.floor(elapsedInMillis / HOUR)
    const minutes = Math.floor((elapsedInMillis / MINUTE) % 60)
    const seconds = Math.floor((elapsedInMillis / SECOND) % 60)
    const milliseconds = elapsedInMillis % SECOND
    return (
        <div>
            <p>
                {hours}:{minutes.toString().padStart(2, "0")}:
                {seconds.toString().padStart(2, "0")}:
                {milliseconds.toString().padStart(3, "0")}
            </p>
            <div>
                <button onClick={handlers.onResumeOrLap}>
                    {isRunning ? "Lap" : "Resume"}
                </button>
                <button onClick={handlers.onStop}>
                    Reset
                </button>
            </div>
        </div>
    )
}

const container: HTMLElement = document.getElementById('container')

export function demo11() {
    const root = ReactDOM.createRoot(container)
    root.render(
        <Stopwatch></Stopwatch>
    )
}