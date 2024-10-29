////////////// index.js
function moduleIndex(require, exports) {
    const {add} = require('./add.js')
    const {sub} = require('./sub.js')
    const {write} = require("./writer.js")

    write("Add", "11 + 13 = " + add(11,13))
    write("Add", "7 + 5 = " + add(7,5))

    write("Sub", "11 - 13 = " + sub(11,13))
    write("Sub", "7 - 5 = " + sub(7,5))
}

////////////// writer
function moduleWriter(require, exports) {
    exports.write  = function (label, msg) {
        console.log(label + ": " +  msg)
    }
}
///////////// add
function moduleAdd(require, exports) {
    console.log("Running Add")
    exports.add = function add (a, b) {
        return a + b
    }
}

//////////// sub
function moduleSub(require, exports) {
    const {add} = require('./add.js')

    console.log("Running sub with: " + add)
    exports.sub = function (a, b) {
        return a - b
    }
}

(() => {
    const moduleFiles = {
        './writer.js': moduleWriter,
        './add.js': moduleAdd,
        './sub.js': moduleSub,
    }

    const modules = {}

    function importModule(path) {
        // First check if that module has already been loaded,
        // and the resulting exports object stored in modules.
        const module = modules[path]
        if(module) {
            return module
        }
        const mf = moduleFiles[path]
        const exports = {}
        mf(importModule, exports)
        // Add the exports object to the modules storage.
        modules[path] = exports
        return exports
    }
    moduleIndex(importModule, {})
})()