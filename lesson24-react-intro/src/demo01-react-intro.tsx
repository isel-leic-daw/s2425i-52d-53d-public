import * as React from 'react'
import * as ReactDom from 'react-dom/client'

function domTree(): Element {
    const div: Element = document.createElement("div")
    const h1 = document.createElement("h1")
    h1.textContent = "First Demo via DOM"
    const p = document.createElement("p")
    p.textContent = "Simple HTML document with paragraph within a div!"
    div.append(h1)
    div.append(p)
    return div
}

/**
 * React Virtual tree created with React API
 */
function virtualTree() {
    return React.createElement('div', {}, 
        React.createElement('h1', {}, 'First Demo via React and DOM'),
        React.createElement('p', {}, 'Simple HTML document with paragraph within a div!')
    )
}

/**
 * React Virtual tree created via JSX
 */
let counter = 1 /// ALERT Mutable => DON'T DO this !!!!
function virtualTreeInJSX() {
    counter++ // Only for DEMO!!!! DON'T DO this in production
    return (
        <div>
            <h1>Second Demo via React and DOM with JSX!</h1>
            {`Counter = ${counter}`}
            <p>Simple HTML document with paragraph within a div!</p>
            <hr></hr>
            <input type='text'></input>
            <hr></hr>
            <input type='text'></input>
        </div>
    )
}

export function demo01() {
    // document.body.appendChild(domTree())
    // const tree = virtualTree()
    const root = ReactDom.createRoot(document.getElementById('container'))
    setInterval(() => {
        root.render(virtualTreeInJSX())
    }, 1000)
}