import * as React from 'react'
import * as ReactDom from 'react-dom/client'

/*
 * DON'T DO THIS:
 * - Don't use MUTABLE state!!!!!
 * - Don't capture MUTABLE state from a Component Funcion with CLOSURE
 */
let count = 0

function Counter() {
    function incrementHandler() {
        count++
        myRender() // !!!!! DON'T DO THIS !!!! DO NOT CALL RENDER - NEVER
    }
    function decrementHandler() {
        count-- 
        myRender() // !!!!! DON'T DO THIS !!!! DO NOT CALL RENDER - NEVER
    }
    return (
        <div>
            <button onClick={decrementHandler}>-</button>
            <b>{count}</b>
            <button onClick={incrementHandler}>+</button>
        </div>
    )
}

const root = ReactDom.createRoot(document.getElementById('container'))

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