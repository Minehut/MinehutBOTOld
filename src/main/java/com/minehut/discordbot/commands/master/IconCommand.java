package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.*;
import okhttp3.Request;

/**
 * Created by MatrixTunnel on 8/10/2017.
 */
public class IconCommand extends Command {

    public IconCommand() {
        super(CommandType.MASTER, null, "icon");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (args.length == 0) {
            if (!message.getAttachments().isEmpty()) {
                Message.Attachment attachment = message.getAttachments().get(0);
                try {
                    MinehutBot.get().getDiscordClient().getSelfUser().getManager().setAvatar(Icon.from(
                            MinehutBot.get().getHttpClient().newCall(new Request.Builder()
                                    .url(attachment.getUrl())
                                    .header("User-Agent", "Mozilla/5.0 MinehutBot").build()).execute()
                                    .body().byteStream()
                    )).complete();
                } catch (Exception e) {
                    channel.sendMessage("Failed to update avatar!! " + e).queue(msg -> Chat.removeMessage(msg, 5));
                    return true;
                }
                channel.sendMessage(sender.getUser().getAsMention() + " Success!").queue(msg -> Chat.removeMessage(msg, 5));
            } else {
                channel.sendMessage(sender.getUser().getAsMention() + " You must either attach an image or link to one!").queue(msg -> Chat.removeMessage(msg, 5));
            }
        } else {
            try {
                MinehutBot.get().getDiscordClient().getSelfUser().getManager().setAvatar(Icon.from(
                        MinehutBot.get().getHttpClient().newCall(new Request.Builder()
                                .url(args[0])
                                .header("User-Agent", "Mozilla/5.0 MinehutBot").build()).execute()
                                .body().byteStream()
                )).complete();
            } catch (Exception e) {
                channel.sendMessage(sender.getUser().getAsMention() + " Failed to update avatar!! " + e).queue(msg -> Chat.removeMessage(msg, 5));
                return true;
            }
            channel.sendMessage(sender.getUser().getAsMention() + " Success!").queue(msg -> Chat.removeMessage(msg, 5));
        }

        return true;
    }

}
