# NekoCommander [![](https://www.jitpack.io/v/neko-craft/NekoCommander.svg)](https://www.jitpack.io/#neko-craft/NekoCommander)

A simple bukkit command system.

## Install

build.gradle:

```groovy
repositories {
    maven { url 'https://www.jitpack.io' }
}
dependencies {
    compileOnly 'net.sf.jopt-simple:jopt-simple:6.0-alpha-3'
    compile 'com.github.neko-craft:NekoCommander:-SNAPSHOT'
}
```

## Usage

Command.java:

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

```java
import org.bukkit.plugin.java.JavaPlugin;
import cn.apisium.nekocommander.*;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        new Commander(this)
            .registerCommand(new Command());
    }
}
```

### Registered commands

- `/command`
- `/command subCommand`
- `/command subCommand2 command3 -e --player PLAYER --time=1h`

## Author

Shirasawa

## License

[MIT](./LICENSE)
