package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.Voice;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MissingPermissionsException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by MatrixTunnel on 12/6/2016.
 */
public class Commands {

    private String prefix = "!"; //TODO Config

    @EventSubscriber
    public void handle(MessageReceivedEvent event) throws MissingPermissionsException, IOException, UnsupportedAudioFileException {
        IMessage message = event.getMessage();
        IUser sender = event.getMessage().getAuthor();
        IGuild guild = event.getClient().getGuildByID(event.getMessage().getGuild().getID());

        if (message.getContent() == null || !message.getContent().startsWith("!")) return;

        if (!sender.getID().equals("118088732753526784")) {
            return;
        }
        //if (!message.getAuthor().get.getGuild().getID().equals("255542332290498572")) { //TODO Config a list of role ids
        //    return;
        //}

        String command = message.getContent().replaceFirst(prefix, "");
        String[] args = command.split(" ");

        Chat.removeMessage(event.getMessage());
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
            default:
                //Core.log.info("Not a valid command");
                break;
        }

    }

}
