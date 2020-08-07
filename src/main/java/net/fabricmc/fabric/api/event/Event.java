package net.fabricmc.fabric.api.event;

public abstract class Event<T> {
    protected T invoker;

    public Event() {
    }

    public final T invoker() {
        return this.invoker;
    }

    public abstract void register(T var1);
}
