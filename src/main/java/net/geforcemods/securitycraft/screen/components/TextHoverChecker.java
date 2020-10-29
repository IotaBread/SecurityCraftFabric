package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class TextHoverChecker extends HoverChecker
{
	private List<Text> lines;

	public TextHoverChecker(int top, int bottom, int left, int right, Text line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<Text> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
	}

	public TextHoverChecker(ButtonWidget button, Text line)
	{
		this(button, Arrays.asList(line));
	}

	public TextHoverChecker(ButtonWidget button, List<Text> lines)
	{
		super(button);
		this.lines = lines;
	}

	public Text getName()
	{
		return lines.get(0);
	}

	public List<Text> getLines()
	{
		return lines;
	}
}
