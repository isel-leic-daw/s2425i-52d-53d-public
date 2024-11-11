import { useState, useEffect } from "react"
import * as React from "react"
import * as ReactDOM from "react-dom/client"

const SECOND = 1000
const MINUTE = SECOND * 60
const HOUR = MINUTE * 60


function Stopwatch() {
    // state to store time
    const [init, setInit] = useState(0)
    const [current, setCurrent] = useState(0)

    // state to check stopwatch running or not
    const [isRunning, setIsRunning] = useState(false)

    useEffect(() => {
        let timeoutId: NodeJS.Timeout
        if (isRunning) {
            timeoutId = setTimeout(() => setCurrent(Date.now() - init), 10)            
        }
        return () => { clearTimeout(timeoutId) }
    }, [isRunning, current])

    // calculation
    const hours = Math.floor(current / HOUR)
    const minutes = Math.floor((current / MINUTE) % 60)
    const seconds = Math.floor((current / SECOND) % 60)
    const milliseconds = current % SECOND

    // Method to start and stop timer
    const startAndStop = () => {
        if(init === 0) {
            setInit(Date.now())
        }
        setIsRunning(!isRunning)
    }

    // Method to reset timer back to 0
    const reset = () => {
        setInit(0)
        setCurrent(0)
        setIsRunning(false)
    }
    return (
        <div>
            <p>
                {hours}:{minutes.toString().padStart(2, "0")}:
                {seconds.toString().padStart(2, "0")}:
                {milliseconds.toString().padStart(2, "0")}
            </p>
            <div>
                <button onClick={startAndStop}>
                    {isRunning ? "Lap" : "Resume"}
                </button>
                <button onClick={reset}>
                    Reset
                </button>
            </div>
        </div>
    )
}

const container: HTMLElement = document.getElementById('container')

export function demo09() {
    const root = ReactDOM.createRoot(container)
    root.render(
        <Stopwatch></Stopwatch>
    )
}