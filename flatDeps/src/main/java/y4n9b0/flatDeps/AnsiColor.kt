package y4n9b0.flatDeps

/**
 * ANSI 控制台颜色与样式工具类。
 *
 * 可用于在 Gradle 插件中打印彩色日志或高亮输出。
 * 用法：
 *   println("${AnsiColors.fg.red}${AnsiColors.styles.bold}Error!${AnsiColors.styles.reset}")
 *   println(AnsiColors.error("Build failed"))
 *   project.logger.lifecycle(AnsiColors.info("→ Start resolving dependencies..."))
 *   project.logger.warn(AnsiColors.warn("No configuration found for debug"))
 *   project.logger.error(AnsiColors.error("Build failed"))
 *   project.logger.lifecycle(AnsiColors.success("All done ✅"))
 */
object AnsiColors {

    /** 文本样式 (Text Styles) */
    object styles {
        const val reset = "\u001B[0m"          // 重置所有样式
        const val bold = "\u001B[1m"           // 加粗
        const val dim = "\u001B[2m"            // 变暗
        const val italic = "\u001B[3m"         // 斜体
        const val under = "\u001B[4m"          // 下划线
        const val blink = "\u001B[5m"          // 闪烁
        const val inverse = "\u001B[7m"        // 反显
        const val hidden = "\u001B[8m"         // 隐藏
        const val strikethrough = "\u001B[9m"  // 删除线
    }

    /** 前景色 (Foreground Colors) */
    object fg {
        const val black = "\u001B[30m"
        const val red = "\u001B[31m"
        const val green = "\u001B[32m"
        const val yellow = "\u001B[33m"
        const val blue = "\u001B[34m"
        const val magenta = "\u001B[35m"
        const val cyan = "\u001B[36m"
        const val white = "\u001B[37m"
        const val brightBlack = "\u001B[90m"
        const val brightRed = "\u001B[91m"
        const val brightGreen = "\u001B[92m"
        const val brightYellow = "\u001B[93m"
        const val brightBlue = "\u001B[94m"
        const val brightMagenta = "\u001B[95m"
        const val brightCyan = "\u001B[96m"
        const val brightWhite = "\u001B[97m"
    }

    /** 背景色 (Background Colors) */
    object bg {
        const val black = "\u001B[40m"
        const val red = "\u001B[41m"
        const val green = "\u001B[42m"
        const val yellow = "\u001B[43m"
        const val blue = "\u001B[44m"
        const val magenta = "\u001B[45m"
        const val cyan = "\u001B[46m"
        const val white = "\u001B[47m"
        const val brightBlack = "\u001B[100m"
        const val brightRed = "\u001B[101m"
        const val brightGreen = "\u001B[102m"
        const val brightYellow = "\u001B[103m"
        const val brightBlue = "\u001B[104m"
        const val brightMagenta = "\u001B[105m"
        const val brightCyan = "\u001B[106m"
        const val brightWhite = "\u001B[107m"
    }

    fun error(msg: String) = "${fg.red}${styles.bold}$msg${styles.reset}"

    fun warn(msg: String) = "${fg.yellow}${styles.bold}$msg${styles.reset}"

    fun success(msg: String) = "${fg.green}${styles.bold}$msg${styles.reset}"

    fun info(msg: String) = "${fg.cyan}$msg${styles.reset}"
}