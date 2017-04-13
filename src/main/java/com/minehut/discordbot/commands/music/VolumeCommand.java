package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;


/**
 * Created by MatrixTunnel on 1/27/2017.
 */
public class VolumeCommand implements Command {

    @Override
    public String getCommand() {
        return "volume";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"vol", "sound"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message, 5);

        Chat.sendMessage(sender.getAsMention() + "\nSince that command was overloading the CPU, is has been removed.\n" +
                "If you want to change the volume, do this instead: https://gfycat.com/UnrulyBountifulAfricancivet\n**(10-15% recommended)**", channel, 30);
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
