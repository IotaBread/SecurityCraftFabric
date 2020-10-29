package net.geforcemods.securitycraft.compat.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FabricMisc {
    private static MinecraftServer server = null;

    /**
     * A less complicated Fabric version of ForgeHooks.newChatWithLinks()
     * @param link input link
     * @return a underlined blue text with a click event to open the link
     */
    public static Text newChatLink(String link) {
        return new LiteralText(link).formatted(Formatting.BLUE, Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(
                ClickEvent.Action.OPEN_URL, link)));
    }

    public void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> FabricMisc.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FabricMisc.server = server);
    }

    public static MinecraftServer getServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER ? (FabricMisc.server != null ? FabricMisc.server : null) : getServerFromClient();
    }

    @Environment(EnvType.CLIENT)
    public static MinecraftServer getServerFromClient() {
        return MinecraftClient.getInstance().getServer();
    }
}
