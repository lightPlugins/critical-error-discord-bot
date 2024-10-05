package io.lightplugins.crit.modules.message.listener;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.message.LightMessage;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HexFormat;

public class WelcomeMessage extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        boolean welcomeEnabled = LightMessage.instance.getWelcomeConfig().getBoolean("welcome.enable");

        if(!welcomeEnabled) {
            return;
        }

        User user = event.getUser();
        Member member = event.getMember();

        String welcomeChannelId = LightMessage.instance.getWelcomeConfig().getString("welcome.welcome-channel-id");
        TextChannel welcomeChannel = LightMaster.instance.getShardManager().getTextChannelById(welcomeChannelId);

        String selfRoleChannel = LightMessage.instance.getWelcomeConfig().getString("welcome.selfroles-channel-id");
        TextChannel roleChannel = LightMaster.instance.getShardManager().getTextChannelById(selfRoleChannel);

        if(roleChannel == null) {
            LightPrinter.printError("Role Channel not found. Please check welcome.yml for the correct channel id.");
            return;
        }

        if(welcomeChannel == null) {
            LightPrinter.printError("Welcome Channel not found. Please check welcome.yml for the correct channel id.");
            return;
        }

        String title = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.title");
        String description = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.description");
        String color = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.color");
        String footer = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.footer");
        String thumbnailURL = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.thumbnail");
        String fieldTitle = LightMessage.instance.getWelcomeConfig()
                .getString("welcome.embed.field.title");
        boolean inline = LightMessage.instance.getWelcomeConfig()
                .getBoolean("welcome.embed.field.inline");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(thumbnailURL);
        embedBuilder.setDescription(description
                .replace("#mention#", user.getAsMention())
                .replace("#clearname#", user.getEffectiveName()));
        embedBuilder.setTitle(title);
        embedBuilder.addField(fieldTitle, "Self Role Channel: " + roleChannel.getAsMention(), inline);
        embedBuilder.setColor(HexFormat.fromHexDigits(color)); // Goldene Farbe für den linken Balken
        embedBuilder.setFooter(footer);


        MessageEmbed welcomeEmbed = embedBuilder.build();
        welcomeChannel.sendMessageEmbeds(welcomeEmbed).queue();

    }
}
