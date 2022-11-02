package org.test.nativedemo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.time.Instant
import java.time.ZonedDateTime


@ImportRuntimeHints(MyHints::class)
@SpringBootApplication
class NativeDemoMvnApplication {
    @Bean
    fun http(cr: CustomerRepository) = coRouter {
        GET("/customers") {
            ServerResponse.ok().bodyAndAwait(cr.findAll())
        }
    }

    @Bean
    fun myListener(cr: CustomerRepository) = MyListener(cr)

}

fun main(args: Array<String>) {
    runApplication<NativeDemoMvnApplication>(*args)
}

class MyListener(val customerRepository: CustomerRepository) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        runBlocking {
            val customers: Flow<Customer> = flowOf("James", "Josh").map { Customer(null, it) }
            customerRepository.saveAll(customers).collect { println (it) }//look ma, no Flow!
        }
    }
}


data class Customer(@Id val id: Int?, val name: String)

interface CustomerRepository : CoroutineCrudRepository<Customer, Int>


class MyHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        listOf(Customer::class.java, Array<Instant>::class.java, Array<ZonedDateTime>::class.java).forEach {
            hints.reflection().registerType(it, *MemberCategory.values())
        }
     }
}
