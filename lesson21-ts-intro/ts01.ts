function add(a: number | string, b: number | string) : number {
    if(typeof a === "number" && typeof b === "number") {
        return a + b    
    }
    if(typeof a === "string" && typeof b === "string") {
        return a.length + b.length
    }
}

// const arr: Array<number> = [1,2,3,4, "ola"]
const arr: Array<number> = [1,2,3,4]

// Operator + has different behavior depending
// of the operands type
console.log(add(7, 11)) // 18 
console.log(add("ola ", " isel")) // "ola isel"
console.log(add(7, "super")) // 7super 

const div = document.createElement("div") // Implicit the type of div variable
const txt: Text = document.createTextNode(add(7, 11).toString())
div.appendChild(txt)
document.body.appendChild(div)

