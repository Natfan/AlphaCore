/*
 *  Created by Filip P. on 2/2/15 11:24 PM.
 */

package me.pauzen.alphacore.commands;

import me.pauzen.alphacore.Core;
import me.pauzen.alphacore.listeners.ListenerImplementation;
import me.pauzen.alphacore.messages.ErrorMessage;
import me.pauzen.alphacore.core.modules.ManagerModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class Command extends ListenerImplementation implements ManagerModule {

    private List<CommandListener> commandListeners = new ArrayList<>();

    private String   name;
    private String[] aliases;
    private String   description;

    private Command parent;

    public Command() {
        addListener(defaultListener());
        CommandMeta commandMeta = getClass().getAnnotation(CommandMeta.class);
        if (commandMeta == null) {
            throw new IllegalStateException("Class does not have a CommandMeta annotation.");
        }
        name = commandMeta.value();
        description = commandMeta.description();
        aliases = commandMeta.aliases();
    }

    public Command(String name, String[] aliases, String description) {
        addListener(defaultListener());
        this.name = name;
        this.aliases = aliases;
        this.description = description;
    }

    public Command(String name, String[] aliases) {
        this(name, aliases, "%default%");
    }

    public Command(String name) {
        this(name, new String[]{});
    }

    /**
     * Executes the command.
     *
     * @param commandSender Runner of the command.
     * @param args          Command arguments.
     * @param modifiers     Command modifiers (-key value)
     */
    public void execute(CommandSender commandSender, String[] args, Map<String, String> modifiers) {
        for (CommandListener commandListener : this.commandListeners) {
            if (!commandListener.canConsoleSend()) {
                if (!(commandSender instanceof Player)) {
                    ErrorMessage.CONSOLESENDER.send(commandSender);
                    continue;
                }
            }
            commandListener.preRun(this, commandSender, args, modifiers);
        }
    }

    /**
     * Adds a command listener.
     *
     * @param commandListener A command listener to be ran when the command is ran.
     */
    public void addListener(CommandListener commandListener) {
        this.commandListeners.add(commandListener);
    }

    /**
     * The command name aliases.
     *
     * @return Command aliases.
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Returns command name.
     *
     * @return Command name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the command names with aliases and name.
     *
     * @return Command names.
     */
    public Set<String> getNames() {
        Set<String> names = new HashSet<>();

        names.addAll(Arrays.asList(getAliases()));
        names.add(getName());

        return names;
    }

    /**
     * The default listener which is called when the command is ran.
     *
     * @return The default CommandListener.
     */
    public abstract CommandListener defaultListener();

    /**
     * Returns command description.
     *
     * @return Command description.
     */
    public String getDescription() {
        return description;
    }

    public boolean shouldSuggestPlayerNames() {
        return false;
    }

    /**
     * Gets the list of registered CommandListeners.
     *
     * @return CommandListeners.
     */
    public List<CommandListener> getListeners() {
        return commandListeners;
    }

    /**
     * Returns whether a string is an alias of this command.
     *
     * @param name The string to check for.
     * @return If the string is an alias.
     */
    public boolean isAlias(String name) {
        for (String s : getAliases()) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    @Deprecated
    public void register() {
        CommandManager.getManager().registerCommand(this);
    }

    public Command getParent() {
        return parent;
    }

    public void setParent(Command parent) {
        this.parent = parent;
    }

    @Override
    public void unload() {
        RegisteredCommand.unregisterCommand(this);
    }

    public JavaPlugin getOwner() {
        return Core.getCore();
    }
}
