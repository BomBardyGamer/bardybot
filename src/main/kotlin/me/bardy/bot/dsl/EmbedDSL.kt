package me.bardy.bot.dsl

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import java.awt.Color

@DslMarker
annotation class EmbedDSL

@EmbedDSL
fun embed(builder: EmbedBuilder.() -> Unit) = EmbedBuilder().apply(builder).build()

@EmbedDSL
fun EmbedBuilder.title(value: String) = setTitle(value)

@EmbedDSL
fun EmbedBuilder.title(value: String, url: String) = setTitle(value, url)

@EmbedDSL
fun EmbedBuilder.description(value: String) = setDescription(value)

@EmbedDSL
fun EmbedBuilder.color(value: Int) = setColor(value)

@EmbedDSL
fun EmbedBuilder.color(value: Color) = setColor(value)

@EmbedDSL
fun EmbedBuilder.thumbnail(value: String) = setThumbnail(value)

@EmbedDSL
fun EmbedBuilder.author(name: String, url: String, iconUrl: String) = setAuthor(name, url, iconUrl)