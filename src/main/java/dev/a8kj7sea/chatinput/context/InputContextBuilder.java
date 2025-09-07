package dev.a8kj7sea.chatinput.context;

import dev.a8kj7sea.chatinput.InteractiveInputAPI;
import dev.a8kj7sea.chatinput.handlers.InputCanceller;
import dev.a8kj7sea.chatinput.handlers.InputReceiver;
import dev.a8kj7sea.chatinput.handlers.InputValidator;
import dev.a8kj7sea.chatinput.session.PlayerSession;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A builder class for creating {@link InputContext} instances with a fluent
 * API. This allows for easy and readable construction of input contexts.
 */
@Setter
public class InputContextBuilder {

	private final Player player;
	private final InputReceiver receiver;
	private String name;
	private InputValidator validator;
	private InputCanceller canceller;
	private String prompt;
	private String cancelKeyword = "exit";
	private BukkitTask timeoutTask;

	/**
	 * Constructs a new InputContextBuilder.
	 *
	 * @param player   The player for whom the context is being built.
	 * @param receiver The callback to handle the player's successful input.
	 */
	public InputContextBuilder(Player player, InputReceiver receiver) {
		this.player = player;
		this.receiver = receiver;
	}

	/**
	 * Sets a unique name for this context to identify it.
	 *
	 * @param name The unique name for the context.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Sets the message to be sent to the player when the input session starts.
	 *
	 * @param prompt The prompt message.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withPrompt(String prompt) {
		this.prompt = ChatColor.translateAlternateColorCodes('&', prompt);
		return this;
	}

	/**
	 * Sets the validator to check the player's input.
	 *
	 * @param validator The input validator.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withValidator(InputValidator validator) {
		this.validator = validator;
		return this;
	}

	/**
	 * Sets the handler to be executed if the player cancels the input.
	 *
	 * @param canceller The cancellation handler.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withCanceller(InputCanceller canceller) {
		this.canceller = canceller;
		return this;
	}

	/**
	 * Sets the keyword that the player must type to cancel the input process.
	 *
	 * @param keyword The cancellation keyword.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withCancelKeyword(String keyword) {
		this.cancelKeyword = keyword;
		return this;
	}

	/**
	 * Sets a timeout for the input context, after which it will be automatically
	 * cancelled.
	 *
	 * @param plugin         The plugin instance scheduling the task.
	 * @param timeoutSeconds The duration in seconds before the input times out.
	 * @param timeoutMessage The message to send to the player upon timeout.
	 * @return This builder instance for chaining.
	 */
	public InputContextBuilder withTimeout(Plugin plugin, long timeoutSeconds, String timeoutMessage) {
		this.timeoutTask = org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
			PlayerSession session = InteractiveInputAPI.getInstance().getSession(player.getUniqueId());
			if (session != null && session.hasActiveContext()) {
				InputContext context = session.getActiveContext();
				if (context.getCanceller() != null) {
					context.getCanceller().onCancel(player);
				}
				// Send the timeout message directly
				if (timeoutMessage != null && !timeoutMessage.isEmpty()) {
					player.sendMessage(timeoutMessage);
				}
				session.clearContext();
			}
		}, timeoutSeconds * 20L);
		return this;
	}

	/**
	 * Builds and returns the final {@link InputContext} instance.
	 *
	 * @return The constructed InputContext.
	 */
	public InputContext build() {
		return new InputContext(name, player, prompt, receiver, validator, canceller, timeoutTask, cancelKeyword);
	}
}
