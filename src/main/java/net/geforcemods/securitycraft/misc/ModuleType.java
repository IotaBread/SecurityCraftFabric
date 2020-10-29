package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.item.ItemStack;

//import java.util.function.Supplier;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableTileEntity}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum ModuleType {

	REDSTONE(SCContent.REDSTONE_MODULE),
	WHITELIST(SCContent.WHITELIST_MODULE),
	BLACKLIST(SCContent.BLACKLIST_MODULE),
	HARMING(SCContent.HARMING_MODULE),
	SMART(SCContent.SMART_MODULE),
	STORAGE(SCContent.STORAGE_MODULE),
	DISGUISE(SCContent.DISGUISE_MODULE);

	private ModuleItem module;

	private ModuleType(ModuleItem moduleItem) {
		module = moduleItem;
	}

	public ModuleItem getItem() {
		return module;
	}

	public String getTranslationKey() {
		return getItem().getTranslationKey();
	}

	public static ModuleType getModuleFromStack(ItemStack stack) {
		if (stack.getItem() instanceof ModuleItem)
			return ((ModuleItem) stack.getItem()).getModuleType();
		else return null;
	}

//	private Supplier<ModuleItem> module;
//
//	private ModuleType(Supplier<ModuleItem> moduleItem){
//		module = moduleItem;
//	}
//
//	public ModuleItem getItem() {
//		return module.get();
//	}
//
//	public String getTranslationKey() {
//		return getItem().getTranslationKey();
//	}
//
//	public static ModuleType getModuleFromStack(ItemStack stack) {
//		if(stack.getItem() instanceof ModuleItem)
//			return ((ModuleItem)stack.getItem()).getModuleType();
//		else return null;
//	}

}