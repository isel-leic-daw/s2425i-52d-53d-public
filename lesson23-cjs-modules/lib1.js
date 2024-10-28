const {sub} = require('./lib2.js')

console.log("Running module lib1 with sub = " + sub)

exports.add = function (a, b) {
    return a + b
}

exports.sub2 = sub