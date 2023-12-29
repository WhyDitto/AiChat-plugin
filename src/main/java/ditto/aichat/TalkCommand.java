package ditto.aichat;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class TalkCommand implements CommandExecutor, Listener {

    private Main main;

    public TalkCommand(Main main) {
        this.main = main;
    }
    private OpenAiService service = new OpenAiService("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", 0);
    private HashMap<UUID, StringBuilder> conversations = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (!conversations.containsKey(player.getUniqueId())) {
                conversations.put(player.getUniqueId(), new StringBuilder("The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n" +
                        "\n" +
                        "Human: Hello\n" +
                        "AI:\n"));
                player.sendMessage(ChatColor.GREEN + "You have started an conversation with an AI! Use /talk to toggle off.");
            } else {
                conversations.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are not talking with an AI anymore");
            }

        } else {

        }
        return true;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (conversations.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(Util.colorize("&a&lYou:&r&l " + e.getMessage()));

            Bukkit.getScheduler().runTaskAsynchronously(main, () -> player.sendMessage(Util.colorize("&b&lAI:&f&l" + getResponse(player.getUniqueId(), e.getMessage()))));
        }


    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        conversations.remove(player.getUniqueId());
    }

    private String getResponse(UUID uuid, String message) {
        conversations.get(uuid).append("\nHuman:").append(message).append("\nAI:");
        CompletionRequest request = CompletionRequest.builder()
                .prompt(conversations.get(uuid).toString())
                .model("text-davinci-003")
                .temperature(0.9D)
                .maxTokens(170)
                .topP(1.0D)
                .frequencyPenalty(0D)
                .presencePenalty(0.6D)
                .stop(Arrays.asList("Human:", "AI:"))
                .build();
        return service.createCompletion(request).getChoices().get(0).getText();
    }
}
