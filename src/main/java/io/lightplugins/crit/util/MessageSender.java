package io.lightplugins.crit.util;

import io.lightplugins.crit.master.LightMaster;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class MessageSender {

    public static void sendTextChannelMessage(long channelID, String message) {

        TextChannel channel = LightMaster.instance.getShardManager().getTextChannelById(channelID);

        if(channel == null) {
            System.out.println(LightMaster.getPrefix() + "[ERROR] Channel with id " + channelID + " not found!");
            return;
        }
        // Send message to text channel
        channel.sendMessage(message).queue();

    }

    public static void sendNoPermissionMessage(SlashCommandInteractionEvent event) {
       // Send message to the target text channel, but only visual for the user
        event.reply(":no_entry:  You are not allowed to use this command").setEphemeral(true).queue();
    }
}
