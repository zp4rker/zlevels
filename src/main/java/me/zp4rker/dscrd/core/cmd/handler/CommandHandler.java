package me.zp4rker.dscrd.core.cmd.handler;

import me.zp4rker.dscrd.core.cmd.CommandExecutor;
import me.zp4rker.dscrd.core.cmd.RegisterCommand;
import me.zp4rker.dscrd.zlevels.ZLevels;
import me.zp4rker.dscrd.zlevels.core.config.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author ZP4RKER
 */
public class CommandHandler {

    private final String prefix;
    private final HashMap<String, Command> commands = new HashMap<>();

    public CommandHandler(String prefix) {
        this.prefix = prefix;
    }

    @SubscribeEvent
    public void handle(MessageReceivedEvent event) {
        // Check if in server
        if (event.getGuild() == null) return;
        // Check server
        if (!event.getGuild().getId().equals(Config.SERVER)) return;
        // Check prefix
        if (!event.getMessage().getContent().startsWith(prefix)) return;
        // Split the content
        String[] splitContent = event.getMessage().getContent().split(" ");
        // Check if has command registered
        if (!commands.containsKey(splitContent[0])) return;
        // Get the command
        Command command = commands.get(splitContent[0]);
        // Get the annotation
        RegisterCommand annotation = command.getCommandAnnotation();
        // Check direct messages
        if (event.getChannelType().equals(ChannelType.PRIVATE) && !annotation.directMessages()) return;
        // Check channel messages
        if (event.getChannelType().equals(ChannelType.TEXT) && !annotation.channelMessages()) return;
        // Check for self
        if (event.getAuthor().equals(event.getJDA().getSelfUser()) && !annotation.allowSelf()) return;
        // Check for others
        if (!event.getAuthor().equals(event.getJDA().getSelfUser()) && !annotation.allowOthers()) return;
        // Invoke the method (async)
        ZLevels.async.submit(() -> invokeMethod(command, event.getMessage(),
                getParameters(splitContent, command, event.getMessage(), event.getJDA())));
    }

    public void registerCommand(CommandExecutor commandExecutor) {
        // Loop through all methods
        for (Method method : commandExecutor.getClass().getMethods()) {
            // Get the annotation
            RegisterCommand annotation = method.getAnnotation(RegisterCommand.class);
            // Check if it exists
            if (annotation == null) continue;
            // Check if aliases is defined
            if (annotation.aliases().length == 0) {
                throw new IllegalArgumentException("No aliases have been defined!");
            }
            // Create the command
            Command simpleCommand = new Command(annotation, method, commandExecutor);
            // Loop through aliases
            for (String alias : annotation.aliases()) {
                // Add it to the map
                commands.put(prefix + alias.toLowerCase(), simpleCommand);
            }
        }
    }

    private Object[] getParameters(String[] splitMessage, Command command, Message message, JDA jda) {
        String[] args = Arrays.copyOfRange(splitMessage, 1, splitMessage.length);
        Class<?>[] parameterTypes = command.getMethod().getParameterTypes();
        final Object[] parameters = new Object[parameterTypes.length];
        int stringCounter = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type == String.class) {
                if (stringCounter++ == 0) {
                } else {
                    if (args.length + 2 > stringCounter) {
                        parameters[i] = args[stringCounter - 2];
                    }
                }
            } else if (type == String[].class) {
                parameters[i] = args;
            } else if (type == Message.class) {
                parameters[i] = message;
            } else if (type == JDA.class) {
                parameters[i] = jda;
            } else if (type == TextChannel.class) {
                parameters[i] = message.getTextChannel();
            } else if (type == User.class) {
                parameters[i] = message.getAuthor();
            } else if (type == MessageChannel.class) {
                parameters[i] = message.getChannel();
            } else if (type == Guild.class) {
                if (!message.getChannelType().equals(ChannelType.TEXT)) {
                    parameters[i] = message.getGuild();
                }
            } else if (type == Object[].class) {
                parameters[i] = getObjectsFromString(jda, args);
            } else {
                parameters[i] = null;
            }
        }
        return parameters;
    }

    private Object[] getObjectsFromString(JDA jda, String[] args) {
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            objects[i] = getObjectFromString(jda, args[i]);
        }
        return objects;
    }

    private Object getObjectFromString(JDA jda, String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
        }
        if (arg.matches("<@([0-9]*)>")) {
            String id = arg.substring(2, arg.length() - 1);
            User user = jda.getUserById(id);
            if (user != null) {
                return user;
            }
        }
        if (arg.matches("<#([0-9]*)>")) {
            String id = arg.substring(2, arg.length() - 1);
            Channel channel = jda.getTextChannelById(id);
            if (channel != null) {
                return channel;
            }
        }
        return arg;
    }

    private void invokeMethod(Command command, Message message, Object[] paramaters) {
        // Get the method
        Method m = command.getMethod();
        Object reply = null;
        try {
            // Get the reply
            reply = m.invoke(command.getExecutor(), paramaters);
        } catch (Exception e) {
            // Print trace
            e.printStackTrace();
        }
        // Check if reply is null
        if (reply != null) {
            // Send the reply
            message.getChannel().sendMessage(String.valueOf(reply)).queue();
        }
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public class Command {

        private final RegisterCommand annotation;
        private final Method method;
        private final CommandExecutor executor;

        Command(RegisterCommand annotation, Method method, CommandExecutor executor) {
            this.annotation = annotation;
            this.method = method;
            this.executor = executor;
        }

        public RegisterCommand getCommandAnnotation() {
            return annotation;
        }

        Method getMethod() {
            return method;
        }

        public CommandExecutor getExecutor() {
            return executor;
        }

    }

}
