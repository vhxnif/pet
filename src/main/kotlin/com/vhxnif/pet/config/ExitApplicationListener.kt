package com.vhxnif.pet.config

import com.vhxnif.pet.util.WaitTaskList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Component
import java.time.Duration

/**
 *
 * @author vhxnif
 * @since 2024-06-21
 */
@Component
class ExitApplicationListener : ApplicationListener<ContextClosedEvent>{

    override fun onApplicationEvent(event: ContextClosedEvent) {
        runBlocking {
            withTimeout(Duration.ofSeconds(5)) {
                while(!WaitTaskList.allTaskDown()) {}
            }
        }
    }
}