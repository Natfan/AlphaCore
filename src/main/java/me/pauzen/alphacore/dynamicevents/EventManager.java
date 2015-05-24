/*
 *  Created by Filip P. on 2/11/15 11:37 PM.
 */

package me.pauzen.alphacore.dynamicevents;

import me.pauzen.alphacore.Core;
import me.pauzen.alphacore.core.managers.Manager;
import me.pauzen.alphacore.dynamicevents.events.CallEventEvent;
import me.pauzen.alphacore.dynamicevents.events.EventListener;
import me.pauzen.alphacore.utils.loading.LoadPriority;
import me.pauzen.alphacore.utils.loading.Priority;
import me.pauzen.alphacore.utils.reflection.Nullify;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Priority(LoadPriority.FIRST)
public class EventManager implements Listener, Manager {

    @Nullify
    private static EventManager manager;

    public static void register() {
        manager = new EventManager();
    }

    public static EventManager getManager() {
        return manager;
    }
    
    public static <E extends Event> void registerEvent(Class<E> eventClass) {
        manager.registerEventClass(eventClass);
    }
    
    public static <E extends Event> void registerEvent(Class<E> eventClass, EventListener<E> listener) {
        manager.registerEventClass(eventClass, listener);
    }

    public <E extends Event> void registerEventClass(Class<E> eventClass) {
        Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.HIGHEST, (listener, event) -> this.onEvent(event), Core.getCore());
    }
    
    public <E extends Event> void registerEventClass(Class<E> eventClass, EventListener<E> listener) {
        Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.HIGHEST, (listener1, event) -> listener.onCall((E) event), Core.getCallerPlugin());
    }

    private void onEvent(Event event) {
        if (event instanceof CallEventEvent) {
            return;
        }

        new CallEventEvent<>(event).call();
    }

    @Override
    public String getName() {
        return "dynamic_events";
    }
}