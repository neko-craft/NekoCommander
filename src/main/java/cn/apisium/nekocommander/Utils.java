package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

public final class Utils {
    public static final boolean IS_BUKKIT;
    public static final Class<?> SENDER_CLASS;
    private Utils() {}

    static {
        boolean isBukkit = false;
        try {
            Class.forName("org.bukkit.command.Command");
            isBukkit = true;
        } catch (Exception ignored) { }
        IS_BUKKIT = isBukkit;
        SENDER_CLASS = isBukkit ? org.bukkit.command.CommandSender.class
            : net.minecraft.server.command.ServerCommandSource.class;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void throwSneaky(final @NotNull Throwable exception) throws T {
        throw (T) exception;
    }
}
