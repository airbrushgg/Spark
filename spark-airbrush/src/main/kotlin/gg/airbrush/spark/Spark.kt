package gg.airbrush.spark

import gg.airbrush.server.pluginManager
import gg.airbrush.server.plugins.Plugin
import me.lucko.spark.common.SparkPlatform
import me.lucko.spark.common.SparkPlugin
import me.lucko.spark.common.command.sender.CommandSender
import me.lucko.spark.common.monitor.ping.PlayerPingProvider
import me.lucko.spark.common.monitor.tick.TickStatistics
import me.lucko.spark.common.platform.PlatformInfo
import me.lucko.spark.common.sampler.source.ClassSourceLookup
import me.lucko.spark.common.sampler.source.SourceMetadata
import me.lucko.spark.common.tick.TickHook
import me.lucko.spark.common.tick.TickReporter
import me.lucko.spark.minestom.MinestomClassSourceLookup
import me.lucko.spark.minestom.MinestomCommandSender
import me.lucko.spark.minestom.MinestomPlatformInfo
import me.lucko.spark.minestom.MinestomPlayerPingProvider
import me.lucko.spark.minestom.MinestomTickHook
import me.lucko.spark.minestom.MinestomTickReporter
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.ExecutionType
import java.nio.file.Path
import java.util.logging.Level
import java.util.stream.Stream
import kotlin.io.path.Path

class Spark : Plugin(), SparkPlugin {
    private lateinit var platform: SparkPlatform
    private lateinit var command: MinestomSparkCommand

    override fun setup() {
        platform = SparkPlatform(this)
        command = MinestomSparkCommand(platform)

        val commandManager = MinecraftServer.getCommandManager()
        commandManager.register(command)
    }

    override fun teardown() {
        val commandManager = MinecraftServer.getCommandManager()
        commandManager.unregister(command)
    }

    override fun getVersion(): String = info.version

    override fun getPluginDirectory(): Path = Path("plugins/${info.id}")

    override fun getCommandName(): String = "spark"

    override fun getCommandSenders(): Stream<out CommandSender> {
        return Stream.concat(
            MinecraftServer.getConnectionManager().onlinePlayers.stream(),
            Stream.of(MinecraftServer.getCommandManager().consoleSender)
        ).map { s -> MinestomCommandSender(s) }
    }

    override fun executeAsync(task: Runnable) {
        MinecraftServer.getSchedulerManager().scheduleNextTick(task, ExecutionType.TICK_END)
    }

    override fun log(level: Level, msg: String) {
        println("[Spark] [${level.name}] - $msg")
    }

    override fun getPlatformInfo(): PlatformInfo = MinestomPlatformInfo()

    override fun createTickHook(): TickHook = MinestomTickHook()

    override fun createTickReporter(): TickReporter = MinestomTickReporter()

    override fun createPlayerPingProvider(): PlayerPingProvider = MinestomPlayerPingProvider()

    override fun createClassSourceLookup(): ClassSourceLookup = MinestomClassSourceLookup(pluginManager)

    override fun getKnownSources(): MutableCollection<SourceMetadata> {
        return SourceMetadata.gather(
            pluginManager.plugins.values,
            { plugin -> plugin.info.name },
            { plugin -> plugin.info.version },
            { "Airbrush Team" },
            { plugin -> plugin.info.description }
        )
    }
}