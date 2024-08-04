package io.lightplugins.crit.commands;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.PermissionNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;


public class PollCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if(!PermissionNode.isAdmin(Objects.requireNonNull(event.getMember()))) {
            event.reply(":no_entry:  You are not allowed to use this command").setEphemeral(true).queue();
            return;
        }

        String triggerCommand = event.getName();
        if(!triggerCommand.equalsIgnoreCase("umfrage"))  {
            return;
        }

        OptionMapping question = event.getOption("frage");
        if(question == null) {
            event.reply(":no_entry:  Missing requirements - Ja/Nein Frage").setEphemeral(true).queue();
            return;
        }

        String questionAsString = question.getAsString();

        createPoll(1087099618396999802L, questionAsString);

        event.reply(":no_entry:  Unfrage erfolgreich erstellt").setEphemeral(true).queue();

    }

    public void createPoll(long channelId, String question) {

        TextChannel channel = LightMaster.instance.getShardManager().getTextChannelById(channelId);
        if (channel == null) {
            System.out.println(LightMaster.getPrefix() + "[ERROR] Channel with id " + channelId + " not found!");
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("**CRIT-E Umfrage** - Ihr seid gefragt!");
        embedBuilder.addField("Stimme jetzt ab :rocket:", question, false);
        embedBuilder.setColor(0xFFDC73); // Goldene Farbe für den linken Balken
        embedBuilder.setFooter("Reagiere mit den Emojis um abzustimmen");

        String pollOptions = "";
        Emoji[] emojis = {
                Emoji.fromUnicode("U+1F44D"), // 👍
                Emoji.fromUnicode("U+1F44E"), // 👎
        };

        embedBuilder.setDescription(pollOptions);

        MessageEmbed pollEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(pollEmbed).queue(message -> {
            try {
                for (Emoji emoji : emojis) {
                    message.addReaction(emoji).queue();
                }
            } catch (PermissionException e) {
                System.out.println(LightMaster.getPrefix() + "[ERROR] Missing permission to add reactions!");
            }
        });
    }
}
