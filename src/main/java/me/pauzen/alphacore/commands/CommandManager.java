/*
 *  Created by Filip P. on 2/2/15 11:24 PM.
 */

package me.pauzen.alphacore.commands;

import me.pauzen.alphacore.utils.misc.Tuple;
import me.pauzen.alphacore.utils.reflection.Nullify;
import me.pauzen.alphacore.utils.reflection.Registrable;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CommandManager implements Registrable {

    @Nullify
    private static CommandManager manager;

    public static void register() {
        manager = new CommandManager();
        new CommandRunner();
        RegisteredCommand.values();
    }
    
    public static CommandManager getManager() {
        return manager;
    }

    public Command getCommand(String commandName) {
        String[] names = commandName.split(" ");
        Command command = RegisteredCommand.getCommand(names[0].toLowerCase());
        if (names.length > 1) {
            for (int i = 1; i < names.length; i++) {
                String name = names[i];

                command = command.defaultListener().getSubCommands().get(name);
            }
        }
        return command;
    }

    public void executeCommand(Command command, CommandSender commandSender, String[] arguments) {
        Tuple<Map<String, String>, String[]> argModifierTuple = getModifiers(arguments);
        command.execute(commandSender, argModifierTuple.getSecond(), argModifierTuple.getFirst());
    }

    private Tuple<Map<String, String>, String[]> getModifiers(String[] args) {
        Map<String, String> modifiers = new HashMap<>();
        List<String> newArgs = new ArrayList<>();
        Collections.addAll(newArgs, args);
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                String key = arg.substring(1);
                String value = args[i + 1];
                modifiers.put(key, value);
                newArgs.remove(arg);
                newArgs.remove(value);
            }
        }

        return new Tuple<>(modifiers, newArgs.toArray(new String[newArgs.size()]));
    }

    public void registerCommand(Command command, Plugin plugin) {
        RegisteredCommand.registerCommand(command, plugin);
    }

    public Map<String, Command> getCommands() {
        return RegisteredCommand.getCommands();
    }
}
