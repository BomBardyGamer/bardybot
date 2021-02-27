package me.bardy.bot.dsl

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import java.awt.Color

@DslMarker
annotation class EmbedDSL

@EmbedDSL
fun embed(builder: EmbedBuilder.() -> Unit) = EmbedBuilder().apply(builder).build()

@EmbedDSL
var EmbedBuilder.title: String
    get() = ""
    set(value) { setTitle(value) }

@EmbedDSL
fun EmbedBuilder.title(title: String, url: String) = setTitle(title, url)

@EmbedDSL
var EmbedBuilder.description: String
    get() = ""
    set(value) { setDescription(value) }

@EmbedDSL
var EmbedBuilder.intColor: Int
    get() = 0
    set(value) { setColor(value) }

@EmbedDSL
var EmbedBuilder.color: Color
    get() = Color.WHITE
    set(value) { setColor(value) }

@EmbedDSL
var EmbedBuilder.thumbnail: String
    get() = ""
    set(value) { setThumbnail(value) }

@EmbedDSL
fun EmbedBuilder.author(name: String, url: String, iconUrl: String) = setAuthor(name, url, iconUrl)