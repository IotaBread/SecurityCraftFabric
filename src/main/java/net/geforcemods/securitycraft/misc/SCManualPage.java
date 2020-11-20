package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class SCManualPage {

	private Item item;
	private TranslatableText helpInfo;
	private BooleanSupplier configValue = () -> true;
	private String designedBy = "";
	private static final List<Item> EXPLOSIVES = Arrays.asList(SCContent.BOUNCING_BETTY,
			SCContent.CLAYMORE,
			SCContent.COBBLESTONE_MINE,
			SCContent.DIAMOND_ORE_MINE,
			SCContent.DIRT_MINE,
			SCContent.FURNACE_MINE,
			SCContent.GRAVEL_MINE,
			SCContent.IMS,
			SCContent.SAND_MINE,
			SCContent.STONE_MINE,
			SCContent.TRACK_MINE,
			SCContent.MINE,
			SCContent.EMERALD_ORE_MINE,
			SCContent.QUARTZ_ORE_MINE,
			SCContent.REDSTONE_ORE_MINE,
			SCContent.IRON_ORE_MINE,
			SCContent.COAL_ORE_MINE,
			SCContent.NETHER_GOLD_ORE_MINE,
			SCContent.GILDED_BLACKSTONE_MINE,
			SCContent.ANCIENT_DEBRIS_MINE,
			SCContent.LAPIS_ORE_MINE,
			SCContent.GOLD_ORE_MINE).stream().map(Block::asItem).collect(Collectors.toList());

	public SCManualPage(Item item, TranslatableText helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;

		if(item == SCContent.KEYCARD_LVL_1) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard1;}
		else if(item == SCContent.KEYCARD_LVL_2) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard2;}
		else if(item == SCContent.KEYCARD_LVL_3) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard3;}
		else if(item == SCContent.KEYCARD_LVL_4) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard4;}
		else if(item == SCContent.KEYCARD_LVL_5) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard5;}
		else if(item == SCContent.LIMITED_USE_KEYCARD) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftLUKeycard;}
		else if(EXPLOSIVES.contains(item))
			configValue = () -> ConfigHandler.CONFIG.ableToCraftMines;
	}

	public Item getItem() {
		return item;
	}

	public TranslatableText getHelpInfo() {
		return helpInfo;
	}

	public boolean isRecipeDisabled()
	{
		return !configValue.getAsBoolean();
	}

	public void setDesignedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String getDesignedBy()
	{
		return designedBy;
	}
}
