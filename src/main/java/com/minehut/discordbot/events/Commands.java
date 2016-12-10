package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.Voice;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MissingPermissionsException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by MatrixTunnel on 12/6/2016.
 */
public class Commands {

    private String prefix = "_"; //TODO Config

    @EventSubscriber
    public void handle(MessageReceivedEvent event) throws MissingPermissionsException, IOException, UnsupportedAudioFileException {
        if (event.getMessage().getChannel() instanceof IPrivateChannel) return;

        IMessage message = event.getMessage();
        IUser sender = event.getMessage().getAuthor();
        IGuild guild = event.getClient().getGuildByID(event.getMessage().getGuild().getID());

        if (message.getContent() == null || !message.getContent().startsWith(prefix)) return;

        if (!sender.getID().equals("118088732753526784")) { //TODO Let roles do commands too
            return;
        }

        String command = message.getContent().replaceFirst(prefix, "");
        String[] args = command.split(" ");

        //Chat.logRemove = false;
        //Chat.removeMessage(event.getMessage());
        Core.log.info(sender.getName() + " did the command \"" + message.getContent() + "\"");

        switch (args[0]) { //TODO Add clip/purge & status command
            case "shutdown":
                if (sender.getID().equals("118088732753526784")) {
                    Core.shutdown();
                }
                break;
            case "summon":
                message.getAuthor().getConnectedVoiceChannels().get(0).join();
                break;
            case "leave":
                message.getClient().getConnectedVoiceChannels().get(0).leave();
                break;
            /*
            case "status":
                if (args[1].equals("game")) {
                    Core.getDiscord().changeStatus(Status.game(args[2]));
                } else if (args[1].equals("stream") && args[2] != null && args[3] != null) {
                    Core.getDiscord().changeStatus(Status.stream(args[2], args[3]));
                } else {
                    Core.getDiscord().changeStatus(Status.empty());
                }
                break;
            */
            case "play":
                Voice.playAudioFile(args[1], guild);
                break;
            case "skip":
                Voice.skip(guild);
                break;
            case "pause":
                Voice.pause(guild);
                break;
            case "resume":
                Voice.resume(guild);
                break;
            case "vol":
                Float level = Float.valueOf(args[1]) / 100;
                //if (level > 1.0) level = (float) 1.0;
                Voice.setVolume(level, guild);
                Core.log.info("Volume set: " + level.toString());
                break;
            case "intillijisbetter":
                /*
                *  Command message from Baristron bot
                *  https://github.com/chrislo27/Baristron
                */
                String mess =
                        "__Reasons why IntelliJ Idea is better than Eclipse (by dec)__\n\n" +
                        "```- the git vcs integration is far superior to eclipses clunky mess, it abstracts away " + "all" + " " +
                                "the stuff you don't really need to know like staging files and just displays " + "the" + " " +
                                "git status of a file with easy to remember colours, doesn't natively support " +
                                "gitignore but a simple plugin you can download in under five mins sorts that\n" +
                                "- eclipses dark themes are laughably bad, intellij has many theme options but " + "the" +
                                " main two are regular white which is okay if you like being blinded and darcula" + " " +
                                "which is a light grey theme that's easy on the eyes and doesn't give you " +
                                "headaches if you're coding for ages\n" +
                                "- it has native maven and gradle and other build config tool integrations (even" + " " +
                                "supports sbt for scala), even the pom files have full proper linting and code " +
                                "suggestions\n" +
                                "- intellij uses \"inspections\" which are totally user configurable to suggest " +
                                "how you could improve your code by collapsing SAMs into lambdas, suggesting " +
                                "change of scope for variables etc\n" +
                                "- the program itself is far more lightweight in terms of both memory usage and " +
                                "hdd usage and it's very easy to manage multiple entirely different projects at " +
                                "the same time.\n" +
                                "- it has a full integrated plugin ecosystem which is kept well updated and can " + "be" +
                                " accessed via file - settings - plugins where you can find anything from making" + " " +
                                ".gitignore show ignored files as gray to plugins that support entire languages " +
                                "for linting and inspections.\n" +
                                "- also it's updated frequently and jetbrains are pretty k00l guys```";
                Chat.sendDiscordMessage(mess, event.getMessage().getChannel());
                break;
            //TODO Temp private channels
            /*
            case "channel":
                try {
                    IVoiceChannel v = event.getMessage().getGuild().createVoiceChannel(args[1]);
                    v.changeUserLimit(Integer.valueOf(args[2]));
                } catch (DiscordException | RateLimitException e) {
                    e.printStackTrace();
                }
                break;
                */
            default:
                //Core.log.info("Not a valid command");
                break;
        }

    }

}
