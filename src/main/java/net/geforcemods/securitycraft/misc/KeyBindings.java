package net.geforcemods.securitycraft.misc;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyBinding cameraZoomIn;
    public static KeyBinding cameraZoomOut;
    public static KeyBinding cameraEmitRedstone;
    public static KeyBinding cameraActivateNightVision;

    public static void init() {
        cameraZoomIn = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.securitycraft.cameraZoomIn", GLFW.GLFW_KEY_EQUAL, "key.categories.securitycraft"));
        cameraZoomOut = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.securitycraft.cameraZoomOut", GLFW.GLFW_KEY_MINUS, "key.categories.securitycraft"));
        cameraEmitRedstone = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.securitycraft.cameraEmitRedstone", GLFW.GLFW_KEY_R, "key.categories.securitycraft"));
        cameraActivateNightVision = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.securitycraft.cameraActivateNightVision", GLFW.GLFW_KEY_N, "key.categories.securitycraft"));
    }
}
