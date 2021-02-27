package me.bardy.bot.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnProperty("services.api")
class StatusController {

    @GetMapping("/status", produces = ["application/json"])
    fun status() = ResponseEntity.ok().body(JSON.encodeToString(BardyBotStatus("running")))

    @Serializable
    private data class BardyBotStatus(val status: String)

    companion object {

        private val JSON = Json {}
    }
}