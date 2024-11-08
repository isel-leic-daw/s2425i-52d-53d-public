import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

/*
 * DON'T do this => DON't manage Mutable state with CLOSURES
 */
let count = 0
function Counter() {
    return (
        <div>
            <button onClick={() => {
                count-- // call setter
                myRender() // DON'T DO THIS => NEVER CALL RENDER !!!!
            }}>-</button>
            {count}
            <button onClick={() => {
                count++
                myRender() // DON'T DO THIS !!!!
            }}>+</button>
            <p>Number 17 is odd</p>
            <b>TPC: Make label odd switch between even and odd</b>
        </div>
    )
}

function myRender() {
    root.render(
        <div>
            <Counter />
            <Counter />
        </div>
    )
}

export function demo04() {
    myRender()
}