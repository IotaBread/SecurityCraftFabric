package net.geforcemods.securitycraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.geforcemods.securitycraft.util.ClientUtils;

import java.util.List;

public class BaseKeycardItem extends Item{

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
	private final int level;

	public BaseKeycardItem(Settings properties, int level) {
		super(properties);
		this.level = level;
	}

	public int getKeycardLvl(){
		if(level == 0)
			return 1;
		else if(level == 1)
			return 2;
		else if(level == 2)
			return 3;
		else if(level == 3)
			return 6;
		else if(level == 4)
			return 4;
		else if(level == 5)
			return 5;
		else
			return 0;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext flag) {
		if(level == 3){
			if(stack.getTag() == null){
				stack.setTag(new CompoundTag());
				stack.getTag().putInt("Uses", 5);
			}

			list.add(ClientUtils.localize("tooltip.securitycraft:keycard.uses").append(new LiteralText(" " + stack.getTag().getInt("Uses"))).setStyle(GRAY_STYLE));

		}
	}

}
