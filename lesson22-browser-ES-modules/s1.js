import { print } from './printer.js'

console.log("Running  from s1.js")

const label = "S1 script"

function add(a, b) {
    return a + b
}

// > S1 script: 11 + 7 = 18
print(label, "11 + 7 = " + add(11, 7))
