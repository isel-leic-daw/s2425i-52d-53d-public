import {sub} from './lib2.js'

console.log("Running module lib1 with sub = " + sub)

export function add(a, b) {
    return a + b
}

export const sub2 = sub