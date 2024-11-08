import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function Counter() {
    const [count, setCount] = React.useState(0)
    function decrementCount() { setCount(count - 1)}
    function incrementCount() { setCount(count + 1)}
    return (
        <div>
            <button onClick={decrementCount}>-</button>
            {count}
            <button onClick={incrementCount}>+</button>
            <p>Number 17 is odd</p>
            <b>TPC: Make label odd switch between even and odd</b>
        </div>
    )
}

export function demo05() {
    root.render(
        <div>
            <Counter />
            <Counter />
        </div>
    )
}