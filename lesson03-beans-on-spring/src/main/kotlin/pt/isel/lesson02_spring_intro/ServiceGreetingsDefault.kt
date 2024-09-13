package pt.isel.lesson02_spring_intro

import org.springframework.stereotype.Component

@Component
class ServiceGreetingsDefault : ServiceGreetings {
    override val greeting: String
        get() = "Hello DAW"
}

/*
// ERROR: Parameter 0 of constructor in ControllerExample required
// a single bean, but 2 were found:
// * serviceGreetingsDefault
// * serviceGreetingsPt
//
@Component
class ServiceGreetingsPt : ServiceGreetings {
    override val greeting: String
        get() = "Ola DAW"
}
*/