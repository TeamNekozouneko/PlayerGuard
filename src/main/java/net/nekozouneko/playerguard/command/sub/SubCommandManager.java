package net.nekozouneko.playerguard.command.sub;

import java.util.*;

public class SubCommandManager {

    Map<String, SubCommand> commands = new HashMap<>();
    Map<String, String> aliases = new HashMap<>();

    public void register(String name, SubCommand command, String... aliases) {
        commands.put(name, command);
        for (String alias : aliases)
            this.aliases.put(name, alias);
    }

    public SubCommand getCommand(String name) {
        String al = aliases.get(name);
        return al == null ? commands.get(name) : commands.get(al);
    }

    public Set<String> getCommandNames() {
        return Collections.unmodifiableSet(commands.keySet());
    }

    public Set<String> getCommandNamesAndAliases() {
        Set<String > set = new HashSet<>(commands.keySet());
        set.addAll(aliases.keySet());

        return Collections.unmodifiableSet(set);
    }
}
