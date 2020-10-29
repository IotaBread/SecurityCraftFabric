package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class IngredientDisplay
{
	private static final int DISPLAY_LENGTH = 20;
	private final int x;
	private final int y;
	private ItemStack[] stacks;
	private int currentRenderingStack = 0;
	private float ticksToChange = DISPLAY_LENGTH;

	public IngredientDisplay(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void render(MinecraftClient mc, float partialTicks)
	{
		if(stacks == null || stacks.length == 0)
			return;

		mc.getItemRenderer().renderInGuiWithOverrides(stacks[currentRenderingStack], x, y);
		ticksToChange -= partialTicks;

		if(ticksToChange <= 0)
		{
			if(++currentRenderingStack >= stacks.length)
				currentRenderingStack = 0;

			ticksToChange = DISPLAY_LENGTH;
		}
	}

	public void setIngredient(Ingredient ingredient)
	{
		stacks = ingredient.getMatchingStacksClient();
		currentRenderingStack = 0;
		ticksToChange = DISPLAY_LENGTH;
	}

	public ItemStack getCurrentStack()
	{
		return currentRenderingStack >= 0 && currentRenderingStack < stacks.length && stacks.length != 0 ? stacks[currentRenderingStack] : ItemStack.EMPTY;
	}
}
