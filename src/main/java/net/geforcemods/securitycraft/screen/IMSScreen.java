package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity.EnumIMSTargetingMode;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IMSScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private IMSTileEntity tileEntity;
	private ClickButton targetButton;
	private int targetingOptionIndex = 0;

	public IMSScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = (IMSTileEntity)container.te;
		targetingOptionIndex = tileEntity.getTargetingOption().modeIndex;
	}

	@Override
	public void init(){
		super.init();

		addButton(targetButton = new ClickButton(0, width / 2 - 38, height / 2 - 58, 120, 20, tileEntity.getTargetingOption() == EnumIMSTargetingMode.PLAYERS_AND_MOBS ? ClientUtils.localize("gui.securitycraft:ims.hostileAndPlayers") : ClientUtils.localize("tooltip.securitycraft:module.players"), this::actionPerformed));
		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		font.drawString(ClientUtils.localize(SCContent.ims.getTranslationKey()), xSize / 2 - font.getStringWidth(ClientUtils.localize(SCContent.ims.getTranslationKey())) / 2, 6, 4210752);
		font.drawString(ClientUtils.localize("gui.securitycraft:ims.target"), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(ClickButton button){
		switch(button.id){
			case 0:
				targetingOptionIndex++;

				if(targetingOptionIndex > (EnumIMSTargetingMode.values().length - 1))
					targetingOptionIndex = 0;

				tileEntity.setTargetingOption(EnumIMSTargetingMode.values()[targetingOptionIndex]);

				ClientUtils.syncTileEntity(tileEntity);

				updateButtonText();
		}
	}

	private void updateButtonText() {
		if(EnumIMSTargetingMode.values()[targetingOptionIndex] == EnumIMSTargetingMode.PLAYERS)
			targetButton.setMessage(ClientUtils.localize("tooltip.securitycraft:module.playerCustomization.players"));
		else if(EnumIMSTargetingMode.values()[targetingOptionIndex] == EnumIMSTargetingMode.PLAYERS_AND_MOBS)
			targetButton.setMessage(ClientUtils.localize("gui.securitycraft:ims.hostileAndPlayers"));
		else if(EnumIMSTargetingMode.values()[targetingOptionIndex] == EnumIMSTargetingMode.MOBS)
			targetButton.setMessage(ClientUtils.localize("gui.securitycraft:ims.hostile"));
	}

}
