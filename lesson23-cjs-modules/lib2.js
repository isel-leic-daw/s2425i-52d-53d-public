const { add } = require('./lib1.js')

console.log("Running module lib2 with add = " + add)

exports.sub =  function (a, b) {
    return a - b
}