/*
 *  Created by Filip P. on 5/5/15 10:23 PM.
 */

package me.pauzen.alphacore.tools;

import me.pauzen.alphacore.core.modules.ManagerModule;
import me.pauzen.alphacore.inventory.items.ItemBuilder;
import me.pauzen.alphacore.inventory.misc.ClickType;
import me.pauzen.alphacore.utils.Interactable;
import me.pauzen.alphacore.utils.InvisibleEncoder;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Tool implements ManagerModule {

    public static final Tool EMPTY_TOOL = new Tool("");
    private final String                            type;
    private       Interactable<PlayerInteractEvent> listener;
    private long coolDown = 0;
    private long lastInteract;

    public Tool(String type) {
        this.type = type;
    }

    public void makeTool(ItemStack itemStack) {

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() == Material.AIR) {
            return;
        }

        if (ToolManager.getManager().isTool(itemStack)) {
            return;
        }

        ItemBuilder itemBuilder = ItemBuilder.from(itemStack);
        itemBuilder.name(itemBuilder.getName() + InvisibleEncoder.encode("-tool-" + "type:" + type));
        itemBuilder.build();
    }

    public void register() {
        ToolManager.getManager().registerModule(this);
    }

    public long getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(long coolDown) {
        this.coolDown = coolDown;
    }

    public String getType() {
        return type;
    }

    public void onInteract(PlayerInteractEvent event, ClickType clickType) {

        if (getListener() != null) {

            if (System.currentTimeMillis() / 50 - lastInteract < coolDown) {
                lastInteract = System.currentTimeMillis() / 50;
            }

            getListener().onInteract(event, clickType);
        }
    }

    public Interactable<PlayerInteractEvent> getListener() {
        return listener;
    }

    public void setListener(Interactable<PlayerInteractEvent> listener) {
        this.listener = listener;
    }

    @Override
    public void unload() {
        ToolManager.getManager().unregisterModule(this);
    }
}
