package net.minecraft.server.command;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ServerCommandSource {
    public void sendFeedback(Text message, boolean broadcastToOps) {}
    @Nullable
    public Entity getEntity() {
        return null;
    }
    public net.minecraft.util.math.Vec3d getPosition() {
        return null;
    }
    public ServerWorld getWorld() {
        return null;
    }
    public String getName() {
        return null;
    }
    public boolean hasPermissionLevel(int level) {
        return true;
    }
}
