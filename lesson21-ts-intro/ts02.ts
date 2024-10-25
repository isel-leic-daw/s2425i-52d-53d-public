type Person = {
    nr: number;
    name: string;
    country: "Portugal";
    // sibling: Person
}

type Student = {
    nr: number | string;
    name: string;
}

function printStudent(p: Student) {
    console.log(p.nr + " -> " + p.name)
}

// const eirck: Person = {nr: 2347, name: "Alice", country: "Australia"} // Not compatible
const alice: Person = {nr: 2347, name: "Alice", country: "Portugal"}
const john = {nr: "124861", name: "John"}

printStudent(alice) // alice is of type Person with more members than required
printStudent(john) // Not possible in Kotlin or Java


const nrs: Array<number> = [36834, 3423, 23412]
const arr:  (string | number | {
    sport: string;
})[] = [434,341234,"ola", 4234, {sport: "swim"}]