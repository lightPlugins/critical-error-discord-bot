package io.lightplugins.crit.master;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import io.lightplugins.crit.modules.poll.LightPoll;
import io.lightplugins.crit.modules.reaction.LightReaction;
import io.lightplugins.crit.modules.roles.LightRoles;
import io.lightplugins.crit.modules.verify.LightVerify;
import io.lightplugins.crit.util.DatabaseConnection;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.interfaces.LightModule;
import io.lightplugins.crit.util.models.DiscordShardBuilder;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LightMaster {

    public static LightMaster instance;

    // Modules listed here

    public LightPoll lightPoll;
    public LightReaction lightReaction;
    public LightVerify lightVerify;
    public LightRoles lightRoles;

    private final ShardManager shardManager;
    private final Dotenv config;
    private final DefaultShardManagerBuilder defaultShardManagerBuilder;

    public HikariDataSource ds;

    private Map<String, LightModule> modules;

    private static final String prefix = "[CRIT-E] ";

    public static HikariDataSource dataSource;

    public LightMaster() throws LoginException {

        long start = System.currentTimeMillis();

        LightPrinter.print("Bot is starting ...");

        LightPrinter.print("[1/5] Loading env file ...");
        this.config = Dotenv.configure().load();
        LightPrinter.print("[1/5] Loading env file successful.");

        LightPrinter.print("[2/5] Loading default shard manager builder ...");
        this.defaultShardManagerBuilder = new DiscordShardBuilder().initShardManager(config);
        LightPrinter.print("[2/5] Loading default shard manager builder successful.");

        LightPrinter.print("[3/5] Building shard manager ...");
        shardManager = defaultShardManagerBuilder.build();
        LightPrinter.print("[3/5] Building shard manager successful.");

        LightPrinter.print("[4/5] Connect to Database ...");
        // DATABASE
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.connectToDatabaseViaSQLite();
        this.ds = dbConnection.getDataSource();
        LightPrinter.print("[4/5] Connect to Database successful.");


        LightPrinter.print("[5/5] Loading Modules ...");
        LightPrinter.print("[5/5] This may take a while!");
        // Set the instance before loading modules
        LightMaster.instance = this;
        initModules();
        loadModules();
        LightPrinter.print("[5/5] Loading Modules successful.");

        /*  template for register events

        shardManager.addEventListener(
                new LoggerEvent(),
                new PollCommand(),
                new ReactionRoles(),
                new RegisterCommands());
         */

        long end = System.currentTimeMillis();

        LightPrinter.print(" ");
        LightPrinter.print(" ");
        LightPrinter.print("     Bot is ready!");
        LightPrinter.print(" ");
        LightPrinter.print("     Bot started in " + (end - start) + "ms. ( " + (end - start) / 1000 + "s )");
        LightPrinter.print(" ");
        LightPrinter.print("     Author: lightPlugins");
        LightPrinter.print("     Java Version: JDK 21");
        LightPrinter.print("     Discord JDA Version: 4.3.0_339");
        LightPrinter.print(" ");
        LightPrinter.print(" ");

        // Start console listener in a new thread
        new Thread(this::listenForConsoleInput).start();
    }

    private void loadModules() {

        this.loadModule(lightPoll, true);
        this.loadModule(lightReaction, true);
        this.loadModule(lightVerify, true);
        this.loadModule(lightRoles, true);
    }

    private void loadModule(LightModule lightModule, boolean enable) {

        if (enable) {
            lightModule.enable();
            LightPrinter.print("Module " + lightModule.getName() + " enabled.");
        }

    }

    private void unloadModule(LightModule lightModule) {

        lightModule.disable();
        LightPrinter.print("Module " + lightModule.getName() + " disabled.");

    }

    private void initModules() {

        modules = new HashMap<>();

        this.lightPoll = new LightPoll();
        this.lightReaction = new LightReaction();
        this.lightVerify = new LightVerify();
        this.lightRoles = new LightRoles();

        this.modules.put(this.lightPoll.getName(), lightPoll);
        this.modules.put(this.lightReaction.getName(), lightReaction);
        this.modules.put(this.lightVerify.getName(), lightVerify);
        this.modules.put(this.lightRoles.getName(), lightRoles);

    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public DefaultShardManagerBuilder getDefaultShardManagerBuilder() {
        return defaultShardManagerBuilder;
    }

    public Dotenv getConfig() {
        return config;
    }

    public Map<String, LightModule> getModules() {
        return modules;
    }

    public static String getPrefix() {
        return prefix;
    }

    public HikariDataSource getDataSource() {
        return ds;
    }

    private void listenForConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if ("stop".equalsIgnoreCase(input)) {
                shutdown();
                break;
            }
        }
    }

    private void shutdown() {
        // Perform any necessary cleanup here
        if (shardManager != null) {

            // unload modules on Shutdown init
            for(LightModule module : modules.values()) {
                unloadModule(module);
            }

            LightPrinter.print("Init Bot shut down ...");
            // final, shutdown the shard manager and exit
            shardManager.shutdown();
            LightPrinter.print("Shut down successful.");

        }
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            instance = new LightMaster();
        } catch (LoginException e) {
            throw new RuntimeException("Failed to login to Discord. Please check your token.");
        }
    }
}