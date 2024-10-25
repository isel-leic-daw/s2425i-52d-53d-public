// the plus + operator behavior depends of
// the operands type.
// e.g. for numbers it adds and for strings it concats
function add(a: number, b: number) : number {
    return a + b
}

console.log(add(7,11))          // > 18
// 'string' is not assignable to parameter of type 'number'.
// console.log(add("ola", "isel")) // > olaisel
// console.log(add("ola",11))      // > ola11

const div = document.createElement("div")
const txt = document
    .createTextNode(add(7,11).toString())
div.appendChild(txt)
document.body.append(div)
