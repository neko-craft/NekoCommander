package net.fabricmc.fabric.api.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;

public interface ServerStartCallback {
    Event<ServerStartCallback> EVENT = new Event<ServerStartCallback>() {
        @Override
        public void register(ServerStartCallback var1) {
        }
    };
    void onStartServer(MinecraftServer var1);
}
