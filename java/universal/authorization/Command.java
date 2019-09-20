package de.idkwhoami.utils.command;

import com.google.gson.annotations.SerializedName;
import de.idkwhoami.utils.permission.Permitable;

import java.util.LinkedList;
import java.util.Set;

public abstract class Command extends Permitable {

    @SerializedName("prefix")
    private Character prefix;

    @SerializedName("base")
    private String baseCommand;

    @SerializedName("args")
    private LinkedList<Set<String>> arguments;

    public Command(String permission, Character prefix, String command) {
        super(permission);
        String[] args = command.split("\\s");
        this.prefix = prefix;
        this.baseCommand = command.toLowerCase();
        this.arguments = new LinkedList<>();
    }

    public boolean exists(String... args) {
        if (args == null)
            return false;
        boolean exists = false;
        for (int j = 0; j < args.length; j++) {
            exists = arguments.get(j).contains(args[j].toLowerCase());
            if (!exists)
                return false;
        }
        return exists;
    }

    public void registerCommand(String... args) {
        if (args == null)
            return;
        if(exists(args))
            return;
        for (int i = 0; i < args.length; i++) {
            if (!arguments.get(i).contains(args[i].toLowerCase()))
                arguments.get(i).add(args[i].toLowerCase());
        }
    }

    private boolean validate(String input) {
        input = input.toLowerCase();
        if (!input.trim().matches("^" + prefix + "[^\\s]+(\\s([^\\s]+|$))+"))
            return false;
        String[] array = input.substring(this.baseCommand.length() + 1).trim().split("\\s");
        return exists(array);
    }

    public abstract boolean execute(Object... optional);

}
