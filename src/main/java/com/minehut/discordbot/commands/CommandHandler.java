package com.minehut.discordbot.commands;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.general.HelpCommand;
import com.minehut.discordbot.commands.general.minehut.StatusCommand;
import com.minehut.discordbot.commands.management.*;
import com.minehut.discordbot.commands.master.*;
import com.minehut.discordbot.commands.music.NowPlayingCommand;
import com.minehut.discordbot.commands.music.PlayCommand;
import com.minehut.discordbot.commands.music.QueueCommand;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private MinehutBot bot = MinehutBot.get();

    private static List<Command> cmds = new ArrayList<>();

    public void execute(UserClient client, Guild guild, Message message, Member sender, TextChannel channel) {
        new BotTask("CommandTask") {
            @Override
            public void run() {
                String msg = message.getContentRaw();
                String command = msg.substring(1);
                String[] args = new String[0];

                if (msg.contains(" ")) {
                    command = command.substring(0, msg.indexOf(" ") - 1);
                    args = msg.substring(msg.indexOf(" ") + 1).split(" ");
                }

                Command cmd = getCommand(command);

                if (cmd == null) return; //Invalid command
                MinehutBot.log.info("[" + guild.getName() + "#" + channel.getName() + "|" + Chat.getFullName(sender.getUser()) + "]: " + msg);

                switch (cmd.getType()) {
                    case MASTER:
                        if (!sender.getUser().getId().equals(Bot.getCreator().getId()))
                            return;
                        break;
                    case TRUSTED:
                        if (!client.isStaff()) return;
                        break;
                    case MUSIC:
                        String musicId = bot.getConfig().getMusicCommandChannelId();

                        if (musicId != null && !musicId.isEmpty()) {
                            if (!channel.getId().equals(musicId)) {
                                Chat.removeMessage(message);
                                Chat.sendMessage(sender.getAsMention() + " Please use music commands in <#" + musicId + ">", channel, 5);
                                return;
                            }
                        } else {
                            Chat.removeMessage(message);
                            Chat.sendMessage(sender.getAsMention() + " Music commands are currently disabled. Please contact a staff member if you believe this is an error", channel, 5);
                            return;
                        }
                        break;
                    case GENERAL:
                        String cmdId = bot.getConfig().getCommandChannelId();

                        if (cmdId != null && !cmdId.isEmpty()) {
                            if (!channel.getId().equals(cmdId)) {
                                Chat.removeMessage(message);
                                Chat.sendMessage(sender.getAsMention() + " Please use bot commands in <#" + cmdId + ">", channel, 5);
                                return;
                            }
                        }
                        break;
                }

                try {
                    if (!cmd.onCommand(client, guild, channel, message, args)) {
                        Chat.sendMessage(sender.getAsMention() + " Usage: ```" + bot.getConfig().getCommandPrefix() + cmd.getUsage() + "```", channel, 20);
                    }
                } catch (CommandException e) {
                    MinehutBot.log.error(e.getMessage(), e);
                }
            }
        }.command();
    }

    /**
     * Get a command.
     *
     * @param name The name of the command
     * @return The Command if found, null otherwise.
     */
    protected Command getCommand(String name) {
        for (Command cmd : cmds) {
            if (cmd.getCommands().length > 0) {
                for (String command : cmd.getCommands()) {
                    if (command.equalsIgnoreCase(name)) {
                        return cmd;
                    }
                }
            }
        }
        return null;
    }

    public static void registerCommands() {
        //general
        cmds.add(new StatusCommand());

        cmds.add(new HelpCommand());

        //management
        cmds.add(new JoinCommand());
        cmds.add(new LeaveCommand());
        cmds.add(new MuteCommand());
        //cmds.add(new PurgeCommand());
        cmds.add(new ReconnectVoiceCommand());
        cmds.add(new ToggleMusicCommand());

        //master
        cmds.add(new IconCommand());
        cmds.add(new NameCommand());
        cmds.add(new ReloadCommand());
        cmds.add(new SayCommand());
        cmds.add(new ShutdownCommand());

        //music
        cmds.add(new NowPlayingCommand());
        cmds.add(new PlayCommand());
        cmds.add(new QueueCommand());
        //cmds.add(new RandomCommand());
        cmds.add(new SkipCommand());
    }

}
