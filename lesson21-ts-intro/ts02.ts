type Student = {
    nr: number,
    name: string,
}

// ERROR: nbr does not exist in type Student
// const alice: Student = { nbr: 7, name: "Alice" }

const alice: Student = { nr: 7, name: "Alice" }

class Person {
    nr: number
    name: string
    constructor(n: number, name: string) {
        this.nr = n
        this.name = name
    }
}

let p = new Person(8374, "John")
p = alice // Structural Type System

console.log(typeof p)
