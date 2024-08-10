package io.lightplugins.crit.modules.profiles.commands;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.util.DateParser;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddBirthdayCommand extends ListenerAdapter {

    private static final long COOLDOWN_TIME = 180 * 1000; // 30 seconds in milliseconds
    private final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String executedCommand = event.getName();
        String triggerCommand = "addbirthday";

        // check if command is the trigger command
        if (!executedCommand.equalsIgnoreCase(triggerCommand)) {
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply(":no_entry: User not found. Bitte kontaktiere einen Admin")
                    .setEphemeral(true).queue();
            return;
        }

        String uniqueId = member.getId();

        // Check cooldown
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(uniqueId)) {
            long lastExecutionTime = cooldowns.get(uniqueId);
            if (currentTime - lastExecutionTime < COOLDOWN_TIME) {
                long remainingTime = COOLDOWN_TIME - (currentTime - lastExecutionTime);
                long remainingSeconds = remainingTime / 1000;
                event.reply(":no_entry: Bitte warte " + remainingSeconds + " Sekunden, bevor du den Befehl erneut ausführst.")
                        .setEphemeral(true).queue();
                return;
            }
        }

        OptionMapping subcommand = event.getOption("birthday");
        if (subcommand == null) {
            event.reply(":no_entry: Missing requirements - Bitte füge nach dem Befehl dein Geburtsdatum ein.")
                    .setEphemeral(true).queue();
            return;
        }

        // print command execution log to console
        LightPrinter.print("[COMMAND] /addBirthday " + subcommand.getAsString() + " by " + member.getNickname() + " / " + member.getUser().getName());

        Date date = DateParser.parseDate(subcommand.getAsString());
        if (date == null) {
            event.reply(":no_entry: Invalid date format. Bitte verwende das richtige Format: `dd.MM.yyyy`")
                    .setEphemeral(true).queue();
            return;
        }

        // Calculate age
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(date);
        int birthYear = birthCalendar.get(Calendar.YEAR);

        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int age = currentYear - birthYear;

        // Check if age is between 15 and 50
        if (age < 15 || age > 50) {
            event.reply(":no_entry:  Dein Alter muss zwischen 15 und 50 Jahren liegen.")
                    .setEphemeral(true).queue();
            return;
        }

        if (LightProfile.getLightProfileAPI().updateBirthdayDate(date, uniqueId)) {
            event.reply(":white_check_mark: Dein Geburtstag " + subcommand.getAsString() + " wurde erfolgreich hinzugefügt oder geändert.")
                    .setEphemeral(true).queue();
            // Update cooldown
            cooldowns.put(uniqueId, currentTime);
        } else {
            event.reply(":no_entry: Geburtsdatum konnte nicht hinzugefügt werden. Kontaktiere einen Admin")
                    .setEphemeral(true).queue();
        }
    }
}