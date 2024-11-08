import * as React from 'react'
import * as ReactDom from 'react-dom/client'

function Counter() {
    const [count, setCount] = React.useState(0)
    const [label, setLabel] = React.useState("even")
    function incrementHandler() {
        setCount(count + 1)
    }
    function decrementHandler() {
        setCount(count - 1)
    }
    return (
        <div>
            <button onClick={decrementHandler}>-</button>
            <b>{count}</b>
            <button onClick={incrementHandler}>+</button>
            {/* Show the value of State label with the even or odd according to the count */}
        </div>
    )
}

const root = ReactDom.createRoot(document.getElementById('container'))

function myRender() {
    root.render(
        <div>
            <Counter />
            <Counter />
            <Counter />
        </div>
    )
}

export function demo05() {
  myRender()  
}