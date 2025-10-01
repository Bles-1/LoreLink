package net.ultrastudios.lorelink.modsconfig.shr;

import net.minecraft.world.InteractionResult;
import net.ultrastudios.lorelink.utils.actioncontext.blockusecontext.ClientBlockUseContext;
import net.ultrastudios.lorelink.utils.actioncontext.blockusecontext.ServerBlockUseContext;
import org.jetbrains.annotations.NotNull;

/**
 * Action handler for the ReSpawner block in SimpleHardcoreRespawn (SHR).
 */
public interface IActionHandler {
    // Conditions

    /**
     * <p>Primary condition method that is called to check if charging action can be attempted.</p>
     * <p>Should check things such as ReSpawner state, ReSpawnerCharging game rule, etc.</p>
     * <p>If false returned, action won't be attempted and failure effect won't be played.</p>
     * @param context {@link ServerBlockUseContext}
     * @return True if charging action can be attempted, false if not.
     */
    boolean canCharge(@NotNull ServerBlockUseContext context);

    /**
     * <p>Primary condition method that is called to check if respawn action can be attempted.</p>
     * <p>Should check things such as ReSpawner state, etc.</p>
     * <p>If false returned, action won't be attempted and failure effect won't be played.</p>
     * @param context {@link ServerBlockUseContext}
     * @return True if respawn action can be attempted, false if not.
     */
    boolean canRespawn(@NotNull ServerBlockUseContext context);

    /**
     * <p>Method to check if ReSpawner is charged to the minimum required to respawn a player.</p>
     * @param context {@link ServerBlockUseContext}
     * @return True if charge is enough to respawn player.
     */
    boolean isCharged(@NotNull ServerBlockUseContext context);

    /**
     * <p>Method to check if Easter Egg should be triggered.</p>
     * This method is executed after {@link net.ultrastudios.lorelink.modsconfig.shr.IActionHandler#canRespawn(ServerBlockUseContext)} returns true, and {@link net.ultrastudios.lorelink.modsconfig.shr.IActionHandler#doRespawn(ServerBlockUseContext)} returns {@link InteractionResult#FAIL}.
     * @param context {@link ServerBlockUseContext}
     * @return True if Easter Egg should be triggered.
     */
    boolean shouldTriggerEasterEgg(@NotNull ServerBlockUseContext context);

    /**
     * <p>Method that returns amount of power to charge.</p>
     * @param context {@link ServerBlockUseContext}
     * @return Amount of power to charge
     */
    int getPowerToCharge(@NotNull ServerBlockUseContext context);

    /**
     * <p>Optional method that can predict action results when useItemOn method was run client-side.</p>
     * The {@link InteractionResult} will be returned in useItemOn method on client-side, so it conditions the behavior:
     * <ul>
     *     <li>{@link InteractionResult#FAIL} - Skips hand sway animation and buffered actions.</li>
     *     <li>{@link InteractionResult#SUCCESS} - Shows hand sway animation regardless of the action performed server-side.</li>
     *     <li>{@link InteractionResult#PASS} - Skips hand sway animation and can buffer block placement or other item action, so can result in ghost block when SUCCESS is returned server-side.</li>
     * </ul>
     * @param context {@link ClientBlockUseContext}
     * @return {@link InteractionResult} that will be returned in Block#useItemOn method on client-side.
     */
    @NotNull InteractionResult getClientResults(@NotNull ClientBlockUseContext context);

    // Actions

    /**
     * <p>Action method that respawns player.</p>
     * <p>This method contains all respawning logic, including mechanical effects. {@link IActionHandler#respawnEffect(ServerBlockUseContext)} or {@link IActionHandler#respawnFailedEffect(ServerBlockUseContext)} is executed after this method.</p>
     * @param context {@link ServerBlockUseContext}
     * @return {@link InteractionResult} indicating success.
     * <ul>
     *     <li>{@link InteractionResult#SUCCESS} will run {@link IActionHandler#respawnEffect(ServerBlockUseContext)} method.</li>
     *     <li>{@link InteractionResult#FAIL} will run {@link IActionHandler#respawnFailedEffect(ServerBlockUseContext)} method or Easter egg.</li>
     *     <li>Anything else will skip any further action and return this value in Block#useItemOn method. (server-side)</li>
     * </ul>
     */
    @NotNull InteractionResult doRespawn(@NotNull ServerBlockUseContext context);

    /**
     * <p>Action method that charges ReSpawner.</p>
     * <p>This method contains all charging logic, including mechanical effects. {@link IActionHandler#chargeEffect(int, ServerBlockUseContext)} is executed after this method.</p>
     * @param pPower Amount of power that player is trying to charge.
     * @param context {@link ServerBlockUseContext}
     * @return {@link InteractionResult} indicating success.
     * {@link InteractionResult#SUCCESS} will run {@link IActionHandler#chargeEffect(int, ServerBlockUseContext)} method.
     */
    @NotNull InteractionResult doCharge(int pPower, @NotNull ServerBlockUseContext context);

    /**
     * <p>Action method that unbans player. works on the same principle as {@link IActionHandler#doRespawn(ServerBlockUseContext)} but is executed when ReSpawnerUnbanning option is enabled.</p>
     * <p>This method contains all unbanning logic, including mechanical effects. {@link IActionHandler#respawnEffect(ServerBlockUseContext)} or {@link IActionHandler#respawnFailedEffect(ServerBlockUseContext)} is executed after this method.</p>
     * @param context {@link ServerBlockUseContext}
     * @return {@link InteractionResult} indicating success.
     * <ul>
     *     <li>{@link InteractionResult#SUCCESS} will run {@link IActionHandler#respawnEffect(ServerBlockUseContext)} method.</li>
     *     <li>{@link InteractionResult#FAIL} will run {@link IActionHandler#respawnFailedEffect(ServerBlockUseContext)} method or Easter egg.</li>
     *     <li>Anything else will skip any further action and return this value in Block#useItemOn method. (server-side)</li>
     * </ul>
     */
    @NotNull InteractionResult doUnban(@NotNull ServerBlockUseContext context);

    // Effects

    /**
     * Effect method that should run additional cosmetic effects, such as sounds, when respawn succeeds.
     * @param context {@link ServerBlockUseContext}
     */
    void respawnEffect(@NotNull ServerBlockUseContext context);

    /**
     * Effect method that should run additional cosmetic effects, such as sounds, when respawn fails.
     * @param context {@link ServerBlockUseContext}
     */
    void respawnFailedEffect(@NotNull ServerBlockUseContext context);

    /**
     * Effect method that should run additional cosmetic effects, such as sounds, when charge succeeds.
     * @param pPower Amount of power charged in latest action.
     * @param context {@link ServerBlockUseContext}
     */
    void chargeEffect(int pPower, @NotNull ServerBlockUseContext context);

    /**
     * Effect method that should run additional cosmetic effects, such as sounds, when charge fails. Not used in default behaviour.
     * @param pPower Amount of power tried to charge in latest action.
     * @param context {@link ServerBlockUseContext}
     */
    void chargeFailedEffect(int pPower, @NotNull ServerBlockUseContext context);

    /**
     * Effect method that should run all effects for Easter egg.
     * @param context {@link ServerBlockUseContext}
     */
    void easterEggEffect(@NotNull ServerBlockUseContext context);

    /**
     * Particles method. Will be executed by ReSpawner after {@link IActionHandler#respawnEffect(ServerBlockUseContext)}
     * @param context {@link ServerBlockUseContext}
     */
    void respawnParticlesEffect(@NotNull ServerBlockUseContext context);
}
