package me.bardy.bot.util

import java.awt.Color
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

@DslMarker
private annotation class EmbedDSL

@EmbedDSL
fun embed(builder: EmbedBuilder.() -> Unit): MessageEmbed = EmbedBuilder().apply(builder).build()

@EmbedDSL
fun buildEmbed(builder: EmbedBuilder.() -> Unit): EmbedBuilder = EmbedBuilder().apply(builder)

@EmbedDSL
fun EmbedBuilder.title(value: String): EmbedBuilder = setTitle(value)

@EmbedDSL
fun EmbedBuilder.title(value: String, url: String): EmbedBuilder = setTitle(value, url)

@EmbedDSL
fun EmbedBuilder.description(value: String): EmbedBuilder = setDescription(value)

@EmbedDSL
fun EmbedBuilder.color(value: Int): EmbedBuilder = setColor(value)

@EmbedDSL
fun EmbedBuilder.color(value: Color): EmbedBuilder = setColor(value)

@EmbedDSL
fun EmbedBuilder.thumbnail(value: String): EmbedBuilder = setThumbnail(value)

@EmbedDSL
fun EmbedBuilder.author(name: String, url: String, iconUrl: String): EmbedBuilder = setAuthor(name, url, iconUrl)
