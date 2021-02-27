package me.bardy.bot

import org.springframework.boot.Banner
import org.springframework.core.env.Environment
import java.io.PrintStream

object BardyBotBanner : Banner {

    override fun printBanner(environment: Environment, sourceClass: Class<*>, out: PrintStream) = out.println(BANNER)

    private const val BANNER = """
______                   _        ______         _   
| ___ \                 | |       | ___ \       | |  
| |_/ /  __ _  _ __   __| | _   _ | |_/ /  ___  | |_ 
| ___ \ / _` || '__| / _` || | | || ___ \ / _ \ | __|
| |_/ /| (_| || |   | (_| || |_| || |_/ /| (_) || |_ 
\____/  \__,_||_|    \__,_| \__, |\____/  \___/  \__|
                             __/ |                   
                            |___/
"""
}