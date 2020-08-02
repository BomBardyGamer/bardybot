package dev.bombardy.bardybot.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/status")
@ConditionalOnProperty("services.api")
class StatusController {

    @GetMapping(produces = ["application/json"])
    fun overallStatus() = ResponseEntity.ok()
                .body(JSON.stringify(BardyBotStatus.serializer(), BardyBotStatus("running")))

    @Serializable
    private data class BardyBotStatus(val status: String)

    companion object {
        private val JSON = Json(JsonConfiguration.Stable)
    }
}
