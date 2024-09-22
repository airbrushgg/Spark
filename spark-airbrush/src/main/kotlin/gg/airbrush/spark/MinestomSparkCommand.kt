package gg.airbrush.spark

import me.lucko.spark.common.SparkPlatform
import me.lucko.spark.minestom.MinestomCommandSender
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.Suggestion
import net.minestom.server.command.builder.suggestion.SuggestionCallback
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import java.util.*

class MinestomSparkCommand(private val platform: SparkPlatform) : Command("spark"), CommandExecutor, SuggestionCallback {
    private val arrayArgument = ArgumentType.StringArray("args")

    init {
        defaultExecutor = this
        addSyntax(this, arrayArgument)
    }

    override fun apply(sender: CommandSender, context: CommandContext) {
        val args = processArgs(context, false) ?: return
        platform.executeCommand(MinestomCommandSender(sender), args)
    }

    override fun apply(sender: CommandSender, context: CommandContext, suggestion: Suggestion) {
        val args = processArgs(context, true) ?: return
        platform.tabCompleteCommand(MinestomCommandSender(sender), args).forEach { entry ->
            suggestion.addEntry(SuggestionEntry(entry))
        }
    }

    private fun processArgs(context: CommandContext, tabComplete: Boolean): Array<String>? {
        val split = context.input.split(" ", limit = if (tabComplete) -1 else 0)
        if (split.isEmpty() || split.first() != "/spark" && split.first() != "spark") {
            return null
        }
        return Arrays.copyOfRange(split.toTypedArray(), 1, split.size)
    }
}