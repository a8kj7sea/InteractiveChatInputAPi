package dev.a8kj7sea.chatinput;

import dev.a8kj7sea.chatinput.context.InputContext;
import dev.a8kj7sea.chatinput.session.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton API to manage player input contexts with session support. This
 * class is responsible for handling the lifecycle of input contexts, including
 * registration, cancellation, and processing player input.
 */
public class InteractiveInputAPI implements Listener {

	private static InteractiveInputAPI instance;
	private final Plugin plugin;
	private final Map<UUID, PlayerSession> sessions = new ConcurrentHashMap<>();

	private InteractiveInputAPI(Plugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Initializes the singleton instance of the API.
	 *
	 * @param plugin The plugin instance.
	 * @return The singleton instance of the API.
	 */
	public static synchronized InteractiveInputAPI initialize(Plugin plugin) {
		if (instance == null) {
			instance = new InteractiveInputAPI(plugin);
		}
		return instance;
	}

	/**
	 * Gets the singleton instance of the API.
	 *
	 * @return The API instance.
	 * @throws IllegalStateException if the API has not been initialized.
	 */
	public static InteractiveInputAPI getInstance() {
		if (instance == null) {
			throw new IllegalStateException("InteractiveInputAPI not initialized!");
		}
		return instance;
	}

	/**
	 * Checks if a player has an active input context with a specific name. This is
	 * useful for preventing conflicts if you need to ensure a specific input flow
	 * is not interrupted.
	 *
	 * @param player The player to check.
	 * @param name   The name of the context to check for (case-insensitive).
	 * @return {@code true} if the player's active context has the specified name,
	 *         {@code false} otherwise.
	 */
	public boolean hasActiveContext(Player player, String name) {
		PlayerSession session = sessions.get(player.getUniqueId());
		if (session != null && session.hasActiveContext()) {
			InputContext context = session.getActiveContext();
			return context.getName() != null && context.getName().equalsIgnoreCase(name);
		}
		return false;
	}

	/**
	 * Gets or creates a player session.
	 *
	 * @param uuid The UUID of the player.
	 * @return The player's session.
	 */
	public PlayerSession getSession(UUID uuid) {
		return sessions.computeIfAbsent(uuid, id -> new PlayerSession(Bukkit.getPlayer(id)));
	}

	/**
	 * Registers a new input context for a player. If an old context exists, it will
	 * be cancelled before the new one is registered.
	 *
	 * @param newContext The new input context to register.
	 */
	public void registerContext(InputContext newContext) {
		PlayerSession session = getSession(newContext.getPlayer().getUniqueId());

		// Set the new context and retrieve the old one.
		InputContext oldContext = session.setActiveContext(newContext);

		// If an old context existed, cancel its tasks to prevent conflicts.
		if (oldContext != null) {
			if (oldContext.getTimeoutTask() != null) {
				oldContext.getTimeoutTask().cancel();
			}
			if (oldContext.getCanceller() != null) {
				// Silently notify the old canceller without a message to the player.
				oldContext.getCanceller().onCancel(newContext.getPlayer());
			}
		}

		// Send the prompt for the new context.
		if (newContext.getPrompt() != null) {
			newContext.getPlayer().sendMessage(newContext.getPrompt());
		}
	}

	/**
	 * Cancels the active input context for a player.
	 *
	 * @param player The player whose context should be cancelled.
	 */
	private void cancel(Player player) {
		PlayerSession session = sessions.get(player.getUniqueId());
		if (session != null && session.hasActiveContext()) {
			InputContext context = session.clearContext(); // Clears and returns the context.
			if (context != null) {
				if (context.getTimeoutTask() != null) {
					context.getTimeoutTask().cancel();
				}
				if (context.getCanceller() != null) {
					context.getCanceller().onCancel(player);
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		PlayerSession session = sessions.get(player.getUniqueId());
		if (session == null || !session.hasActiveContext()) {
			return;
		}

		e.setCancelled(true);
		Bukkit.getScheduler().runTask(plugin, () -> {
			InputContext context = session.getActiveContext();
			String msg = e.getMessage();

			if (msg.equalsIgnoreCase(context.getCancelKeyword())) {
				cancel(player);
				return;
			}

			if (context.getValidator() != null) {
				InputValidationResult result = context.getValidator().validate(player, msg);
				if (!result.isValid()) {
					player.sendMessage(result.getReason());
					return;
				}
			}

			// Clear the context from the session *before* processing the input.
			session.clearContext();
			if (context.getTimeoutTask() != null) {
				context.getTimeoutTask().cancel();
			}
			context.getReceiver().onReceive(player, msg);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		cancel(e.getPlayer());
		sessions.remove(e.getPlayer().getUniqueId());
	}
}
