# NekoCommander [![](https://www.jitpack.io/v/neko-craft/NekoCommander.svg)](https://www.jitpack.io/#neko-craft/NekoCommander)

A simple command system of Bukkit and Fabric.

## Install (build.gradle)

### Bukkit

```groovy
repositories {
    maven { url 'https://www.jitpack.io' }
}
dependencies {
    compileOnly 'net.sf.jopt-simple:jopt-simple:6.0-alpha-3'
    compile 'com.github.neko-craft:NekoCommander:-SNAPSHOT'
}
```

### Fabric

```groovy
repositories {
    maven { url 'https://www.jitpack.io' }
    maven { url = 'https://oss.sonatype.org/content/repositories/snapshots' }
}
dependencies {
    compile 'net.sf.jopt-simple:jopt-simple:6.0-alpha-3'
    compile 'net.md-5:bungeecord-chat:1.16-R0.4-SNAPSHOT'
    compile 'com.github.neko-craft:NekoCommander:-SNAPSHOT'
}
```

## Usage

Command.java: **(Bukkit)**

```java
import cn.apisium.nekocommander.*;
import cn.apisium.nekocommander.completer.PlayersCompleter;
import joptsimple.*;

@Command("command")
public final class Command implements BaseCommand {
    @Command("subCommand")
    @Permission("command.use")
    public void command1(CommandSender sender) { }

    @MainCommand
    public boolean mainCommand() { return true; }

    @Command("test")
    public void testCommand(ProxiedCommandSender sender) { }

    @Command("subCommand2")
    public final class SubCommand implements BaseCommand {
        @Command("command3")
        @Argument(value = { "e", "extra" }, defaultValues = { "default" }, required = true, type = Boolean.class)
        @Argument(value = { "p", "player" }, completer = PlayersCompleter.class)
        public void command1(Player sender, @Argument({ "t", "time" }) int time) { }
    }
}
```

Main.java:

### Bukkit:

```java
import org.bukkit.plugin.java.JavaPlugin;
import cn.apisium.nekocommander.impl.BukkitCommander;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        new BukkitCommander(this)
            .registerCommand(new Command());
    }
}
```

### Fabric:

```java
import net.fabricmc.api.DedicatedServerModInitializer;
import cn.apisium.nekocommander.impl.FabricCommander;

public final class Main implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        new FabricCommander()
            .registerCommand(new Command());
    }
}
```

### Registered commands

- `/command`
- `/command test`
- `/command subCommand`
- `/command subCommand2 command3 -e --player PLAYER --time=1h`

## Author

Shirasawa

## License

[MIT](./LICENSE)
