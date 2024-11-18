import { useState, useEffect } from "react"
import * as React from "react"
import * as ReactDOM from "react-dom/client"
import { useStopwatch } from "./use-stopwatch"

const SECOND = 1000
const MINUTE = SECOND * 60
const HOUR = MINUTE * 60


function Stopwatch() {
    const [{ init, elapsedInMillis: current, isRunning}, handlers ] = useStopwatch()

    // calculation
    const hours = Math.floor(current / HOUR)
    const minutes = Math.floor((current / MINUTE) % 60)
    const seconds = Math.floor((current / SECOND) % 60)
    const milliseconds = current % SECOND

    // Method to start and stop timer
    const startAndStop = () => {
        if(init === 0) { handlers.onStart() }
        else { handlers.onResumeOrLap() }
    }

    return (
        <div>
            <p>
                {hours}:{minutes.toString().padStart(2, "0")}:
                {seconds.toString().padStart(2, "0")}:
                {milliseconds.toString().padStart(3, "0")}
            </p>
            <div>
                <button onClick={startAndStop}>
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