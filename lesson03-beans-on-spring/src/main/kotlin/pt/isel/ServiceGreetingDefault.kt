package pt.isel

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * By default, we have a singleton instance shared by
 * all dependent instances (e.g. two different Controller).
 */
@Component
@Scope("prototype")
class ServiceGreetingDefault : ServiceGreetings {
    override val greeting: String
        get() = "Hello DAW"
}

/*
// ERROR: Parameter 0 of constructor in ControllerExample
// required a single bean, but 2 were found.
@Component
@Scope("prototype")
class ServiceGreetingPt : ServiceGreetings {
    override val greeting: String
        get() = "Ola DAW"
}
 */

