package jp.cron.dsa.event;

import me.zero.alpine.event.Cancellable;
import net.minecraft.network.Packet;

public class C2SPacketEvent extends Event implements Cancellable {
    Packet<?> p;
    boolean isCancelled;

    public C2SPacketEvent(Packet<?> packet) {
        p = packet;
    }

    public Packet<?> getPacket() {
        return p;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
