package com.minehut.discordbot.commands;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.general.HelpCommand;
import com.minehut.discordbot.commands.general.minehut.ServerCommand;
import com.minehut.discordbot.commands.general.minehut.StatusCommand;
import com.minehut.discordbot.commands.general.minehut.UserCommand;
import com.minehut.discordbot.commands.management.*;
import com.minehut.discordbot.commands.master.*;
import com.minehut.discordbot.commands.music.*;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends ListenerAdapter {

    private List<Command> cmds = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Guild guild = event.getGuild();
        Member sender = event.getMember();
        User user = message.getAuthor();
        TextChannel channel = event.getChannel();

        if (message.getRawContent() != null && message.getContent().startsWith(GuildSettings.getPrefix(guild))) {
            if (Core.getConfig().getBlockedUsers().contains(sender.getUser().getId()) && !GuildSettings.isTrusted(sender)) {
                Chat.removeMessage(message);
                sender.getUser().openPrivateChannel().queue(c ->
                    c.sendMessage("You are blacklisted from using bot commands. " +
                            "If you believe this is an error, please contact MatrixTunnel.").queue(m -> m.getPrivateChannel().close()));
                return;
            }

            String msg = event.getMessage().getRawContent();
            String command = msg.substring(1);
            String[] args = new String[0];
            if (msg.contains(" ")) {
                command = command.substring(0, msg.indexOf(" ") - 1);
                args = msg.substring(msg.indexOf(" ") + 1).split(" ");
            }

            Command cmd = getCommand(command);

            if (cmd == null) return; //Invalid command

            if (cmd.getType() == Command.CommandType.MASTER && !sender.getUser().equals(Bot.getCreator())) {
                return;
            }
            if (cmd.getType() == Command.CommandType.TRUSTED && !GuildSettings.isTrusted(sender)) {
                return;
            }
            if (cmd.getType() == Command.CommandType.MUSIC && !GuildSettings.getMusicCommandChannels().contains(channel.getId())) {
                return;
            }

            if (!cmd.isEnabled()) {
                //Command is not enabled, send user a message informing them
                sender.getUser().openPrivateChannel().queue(c ->
                        c.sendMessage("The command " + cmd.getName() + " is currently disabled.").queue(m -> m.getPrivateChannel().close()));
                return;
            }

            try {
                if (!cmd.onCommand(guild, channel, sender, message, args)) {
                    Chat.sendMessage(sender.getAsMention() + " Usage: ```" + GuildSettings.getPrefix(guild) + cmd.getUsage() + "```", channel, 20);
                }
            } catch (CommandException e) {
                Core.log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get a command.
     *
     * @param name The name of the command
     * @return The Command if found, null otherwise.
     */
    public Command getCommand(String name) {
        for (Command cmd : cmds) {
            if (cmd.getAliases().length > 0) {
                for (String alias : cmd.getAliases()) {
                    if (cmd.getName().equalsIgnoreCase(name) || alias.equalsIgnoreCase(name)) {
                        return cmd;
                    }
                }
            } else {
                if (cmd.getName().equalsIgnoreCase(name)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    public void registerCommands() {
        //general
        cmds.add(new ServerCommand());
        cmds.add(new StatusCommand());
        cmds.add(new UserCommand());

        cmds.add(new HelpCommand());

        //management
        cmds.add(new JoinCommand());
        cmds.add(new LeaveCommand());
        cmds.add(new MuteCommand());
        //cmds.add(new PurgeCommand());
        cmds.add(new ReconnectVoiceCommand());
        cmds.add(new ToggleMusicCommand());

        //master
        cmds.add(new ReloadCommand());
        cmds.add(new SayCommand());
        cmds.add(new ShutdownCommand());
        cmds.add(new EnableCommand(this));
        cmds.add(new DisableCommand(this));

        //music
        cmds.add(new NowPlayingCommand());
        cmds.add(new PlayCommand());
        cmds.add(new QueueCommand());
        cmds.add(new RandomCommand());
        cmds.add(new SkipCommand());
        cmds.add(new VolumeCommand());
    }

}
