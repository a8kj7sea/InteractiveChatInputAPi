# Interactive Chat Input API

A simple and powerful API for Bukkit/Spigot plugins to prompt players for chat-based input with validation, cancellation, and timeout support.

## ✨ Features

- **Fluent Builder**: Easily construct complex input prompts.  
- **Input Validation**: Validate player input with custom logic.  
- **Cancellation**: Allows players to cancel input with a keyword.  
- **Timeout**: Automatically cancel the prompt if the player takes too long.  
- **Named Contexts**: Prevent conflicting prompts by giving them unique names.  
- **SOLID Principles**: Designed to be clean, flexible, and easy to maintain.  

---

## ⚙️ Setup

First, you need to initialize the API in your plugin's `onEnable` method. This only needs to be done once.

**ExamplePlugin.java**
```java
import dev.a8kj7sea.chatinput.InteractiveInputAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize the API
        InteractiveInputAPI.initialize(this);
    }
}
```

---

## 🚀 How to Use

To ask a player for input, you need to create an `InputContext` using the `InputContextBuilder`.  
The builder provides a fluent interface to configure the prompt.

### Full Example

Here is a full example of how to ask a player for their age within a command.

**ExampleCommand.java**
```java
import dev.a8kj7sea.chatinput.InputValidationResult;
import dev.a8kj7sea.chatinput.InteractiveInputAPI;
import dev.a8kj7sea.chatinput.context.InputContext;
import dev.a8kj7sea.chatinput.context.InputContextBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExampleCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ExampleCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        InteractiveInputAPI api = InteractiveInputAPI.getInstance();

        // 1. Check if the player is already in this specific input process
        if (api.hasActiveContext(player, "age_prompt")) {
            player.sendMessage(ChatColor.RED + "You are already being asked for your age!");
            return true;
        }
        
        // 2. Build the Input Context
        InputContext context = new InputContextBuilder(player, (p, message) -> {
            // Success callback (InputReceiver) - runs when input is valid
            p.sendMessage(ChatColor.GREEN + "Thank you! Your age is set to: " + ChatColor.WHITE + message);
        })
        .withName("age_prompt") // A unique name to identify this prompt
        .withPrompt("&ePlease enter your age in the chat. &7(Type 'exit' to cancel)") // The message sent to the player
        .withValidator((p, message) -> { // Logic to validate the player's input
            try {
                int age = Integer.parseInt(message);
                if (age > 0 && age < 130) {
                     return InputValidationResult.ok(); // Input is valid
                } else {
                     return InputValidationResult.fail(ChatColor.RED + "Please provide a realistic age."); // Invalid
                }
            } catch (NumberFormatException e) {
                return InputValidationResult.fail(ChatColor.RED + "That's not a valid number!"); // Invalid
            }
        })
        .withCanceller(p -> p.sendMessage(ChatColor.YELLOW + "You have cancelled the input process.")) // Runs on cancellation
        .withTimeout(plugin, 30, ChatColor.RED + "You ran out of time to enter your age.") // Timeout after 30 seconds
        .build();

        // 3. Register the context to start the prompt
        api.registerContext(context);

        return true;
    }
}
```

---

## 📖 Explanation

- **Check for Active Context**: Use `api.hasActiveContext(player, "your_context_name")` to avoid multiple overlapping prompts.  
- **Build the Context**:  
  - `InputContextBuilder(player, receiver)`: Takes the Player and an InputReceiver lambda.  
  - `.withName(...)`: Unique string to identify this prompt.  
  - `.withPrompt(...)`: The message sent to the player (supports `&` color codes).  
  - `.withValidator(...)`: Validate input and return `InputValidationResult.ok()` or `.fail("reason")`.  
  - `.withCanceller(...)`: Runs when input is cancelled by the player, timeout, or another plugin.  
  - `.withCancelKeyword(...)`: Custom cancel keyword (default = `exit`).  
  - `.withTimeout(...)`: Sets timeout in seconds.  
  - `.build()`: Finalizes the context.  
- **Register the Context**: Use `api.registerContext(context)` to start the process.  

---

## ✅ Summary

This API makes it easy to prompt players for validated, cancellable, and timeout-based chat input in Bukkit/Spigot plugins.

<img width="754" height="245" alt="image" src="https://github.com/user-attachments/assets/65086b3c-e8f4-4cec-b87a-3e43d6ef6e6e" />

