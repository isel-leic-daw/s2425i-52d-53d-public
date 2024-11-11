import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function TextFieldValidator({limit, label}: { limit: number, label: string }) {
    // State variables
    // 
    const [inputText, setInputText] = React.useState("")
    const [isValid, setIsValid] = React.useState(true)
    // Event Handlers
    //
    function onInputTextChange(ev:  React.ChangeEvent<HTMLInputElement>) {
        const txt = ev.target.value
        if(txt.length > limit) {
            setIsValid(false)
        } else {
            setInputText(txt)
            setIsValid(true)
        }
    }
    // UI
    return (
        <div>
            <p>
                {label}:
                <input type="text" onChange={onInputTextChange} value={inputText}>
                </input></p>
            <p>{ isValid ? "" : `Text cannot exceed ${limit} characters` }</p>
        </div>
    )
}

export function demo06() {
    root.render(
        <div>
            <TextFieldValidator limit={10} label="Your text"></TextFieldValidator>
            <hr></hr>
            <TextFieldValidator limit={7} label="Input text"></TextFieldValidator>
        </div>
    )
}