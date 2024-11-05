import * as React from 'react'
import * as ReactDom from 'react-dom/client'

function treeDom(): HTMLDivElement {
    const div = document.createElement('div')
    const h1 = document.createElement('h1')
    h1.textContent = "First Demo in DOM"
    const p = document.createElement('p')
    p.textContent = "I am a simple paragraph from DOM"
    div.appendChild(h1)
    div.appendChild(p)
    return div
}

function reactVirtualTree(): React.DetailedReactHTMLElement<{}, HTMLElement> {
    return React.createElement('div', {}, 
        React.createElement('h1', {}, 'First Demo in React'),
        React.createElement('p', {}, 'I am a simple paragraph from React')
    )
}

/*
 * DON'T DO This. Only for demo. Function with side-effects and a Mutable state.
 */
let counter = 1
function reactVirtualTreeInJSX() {
    return (
        <div>
            <h1>First Demo in React JSX</h1>
            <p>I am a simple paragraph from React in JSX<input type='text'/></p>
            <p>counter = {counter}<input type='text' value="ola isel" readOnly/></p>
        </div>
    )
}

export function demo01() {
    // document.body.appendChild(treeDom())
    const root = ReactDom.createRoot(document.getElementById('container'))
    // root.render(reactVirtualTree())
    setInterval(() => {
        counter++
        root.render(reactVirtualTreeInJSX())
    }, 1000)
    
}
