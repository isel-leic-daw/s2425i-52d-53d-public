const {add} = require('./add.js')
const {sub} = require('./sub.js')
const {write} = require("./writer.js")

write("Add", "11 + 13 = " + add(11,13))
write("Add", "7 + 5 = " + add(7,5))

write("Sub", "11 - 13 = " + sub(11,13))
write("Sub", "7 - 5 = " + sub(7,5))
