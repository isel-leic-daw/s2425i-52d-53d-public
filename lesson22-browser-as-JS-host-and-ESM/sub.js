import {write} from "./writer.js"

function sub(a, b) {
    return a - b
}

const label = "File sub"

write(label, "11 - 13 = " + sub(11,13))
write(label, "7 - 5 = " + sub(7,5))
