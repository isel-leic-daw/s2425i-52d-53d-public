import * as React from 'react'
import * as ReactDom from 'react-dom/client'

const root = ReactDom.createRoot(document.getElementById('container'))

function TextFieldValidator({label, limit}: {label: string, limit: number}) {
    const [inputText, setInputText] = React.useState("")
    const [isValid, setIsValid] = React.useState(true)
    function inputTextOnChangeHandler(ev: React.ChangeEvent<HTMLInputElement>) {
        const txt = ev.target.value
        setIsValid(txt.length < limit)
        if(txt.length < limit) {
            setInputText(txt)
        }
    }
    return (
        <div>
            {label}
            <input type="text" value={inputText} onChange={inputTextOnChangeHandler}>
            </input>
            <br></br>
            <p>
                { isValid ? "" : `Input text cannot exceed ${limit} characters!!`}
            </p>
        </div>
    )
}

export function demo06() {
    root.render(
        <div>
            <TextFieldValidator label="Enter your text: " limit={10}></TextFieldValidator>
            <hr></hr>
            <TextFieldValidator label="Input: " limit={20}></TextFieldValidator>
        </div>
    )
}