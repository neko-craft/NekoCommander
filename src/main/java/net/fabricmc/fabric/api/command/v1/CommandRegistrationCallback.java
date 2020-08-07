//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.fabricmc.fabric.api.command.v1;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandRegistrationCallback {
    Event<CommandRegistrationCallback> EVENT = new Event<CommandRegistrationCallback>() {
        @Override
        public void register(CommandRegistrationCallback var1) {
        }
    };

    void register(CommandDispatcher<ServerCommandSource> var1, boolean var2);
}
