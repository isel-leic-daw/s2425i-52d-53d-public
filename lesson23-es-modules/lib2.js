import {add} from './lib1.js'

console.log("Running module lib2 with add = " + add)

export function sub(a, b) {
    return a - b
}