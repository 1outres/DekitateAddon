package jp.cron.dsa.event;

import me.zero.alpine.event.Cancellable;
import net.minecraft.text.Text;

public class ChatDisplayEvent extends Event implements Cancellable {
    private final Text message;

    private boolean isCancelled = false;

    public ChatDisplayEvent(Text message) {
        this.message = message;
    }

    public Text getMessage() {
        return message;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
}
