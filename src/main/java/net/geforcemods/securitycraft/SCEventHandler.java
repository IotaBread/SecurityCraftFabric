package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
//import net.minecraft.entity.boss.WitherEntity;
//import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
//import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Box;
//import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.HashMap;
//import java.util.List;
import java.util.Random;
//
import net.geforcemods.securitycraft.compat.fabric.event.*;
import net.geforcemods.securitycraft.compat.fabric.FabricMisc;
//import net.geforcemods.securitycraft.api.CustomizableTileEntity;
//import net.geforcemods.securitycraft.api.IModuleInventory;
//import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
//import net.geforcemods.securitycraft.api.IPasswordProtected;
//import net.geforcemods.securitycraft.api.LinkedAction;
//import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
//import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
//import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
//import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
//import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
//import net.geforcemods.securitycraft.entity.SentryEntity;
//import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
//import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
//import net.geforcemods.securitycraft.misc.SCSounds;
//import net.geforcemods.securitycraft.SecurityCraft;
//import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
//import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
//import net.minecraftforge.common.ForgeHooks;
//import net.minecraftforge.event.entity.EntityMountEvent;
//import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
//import net.minecraftforge.event.entity.living.LivingHurtEvent;
//import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
//import net.minecraftforge.event.entity.player.FillBucketEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
//import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
//import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//import net.minecraftforge.event.world.BlockEvent;
//import net.minecraftforge.event.world.BlockEvent.BreakEvent;
//import net.minecraftforge.eventbus.api.Event.Result;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
//import net.minecraftforge.fml.network.PacketDistributor;
//
//@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {
    public static HashMap<String, String> tipsWithLink = new HashMap<>();

    static {
        tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
        tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
        tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
    }

    public static void registerEventListeners() {
        onPlayerLoggedIn();
        onPlayerLoggedOut();
        onDamageTaken();
        onBucketUsed();
        onRightClickBlock();
        onBreakBlock();
        onOwnership();
        onBlockBroken();
        onLivingSetAttackTarget();
        onBreakSpeed();
        onLivingDestroyEvent();
        onEntityMount();
        onRightClickItem();
        onFurnaceFuelBurnTime();
    }

    public static void onPlayerLoggedIn() {
        PlayerLoggedInCallback.EVENT.register(player -> {
            if (!ConfigHandler.CONFIG.sayThanksMessage)
                return ActionResult.PASS;

            String tipKey = getRandomTip();
            MutableText message = new LiteralText("[")
                    .append(new LiteralText("SecurityCraft").formatted(Formatting.GOLD))
                    .append(new LiteralText("] "))
                    .append(ClientUtils.localize("messages.securitycraft:thanks",
                        SecurityCraft.VERSION,
                        ClientUtils.localize("messages.securitycraft:tip"),
                        ClientUtils.localize(tipKey)));

            if (tipsWithLink.containsKey(tipKey.split("\\.")[2]))
                message = message.append(FabricMisc.newChatLink(tipsWithLink.get(tipKey.split("\\.")[2]))); //appendSibling

            player.sendSystemMessage(message, Util.NIL_UUID);

            return ActionResult.PASS;
        });
    }

    public static void onPlayerLoggedOut() {
        PlayerLoggedOutCallback.EVENT.register(player -> {
            // TODO: Dismount camera

            return ActionResult.PASS;
        });
    }
//
//    @SubscribeEvent
//    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
//    {
//        if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getPlayer().getVehicle() instanceof SecurityCameraEntity)
//            event.getPlayer().getVehicle().remove();
//    }

    public static void onDamageTaken() {
        LivingHurtCallback.EVENT.register((entity, source, damage) -> {
            // TODO: Cancel if player is mounted on camera

            if (source == CustomDamageSources.ELECTRICITY) {
                //TODO: Sound event electrified
            }

            return ActionResult.PASS;
        });
    }

//    @SubscribeEvent
//    public static void onDamageTaken(LivingHurtEvent event)
//    {
//        if(event.getEntity() != null && PlayerUtils.isPlayerMountedOnCamera(event.getEntityLiving())){
//            event.setCanceled(true);
//            return;
//        }
//
//        if(event.getSource() == CustomDamageSources.ELECTRICITY)
//            SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), SCSounds.ELECTRIFIED.path, 0.25F, "blocks"));
//    }

    public static void onBucketUsed() {
        FillBucketCallback.EVENT.register((player, itemStack, world, hitResult) -> {
            if (hitResult == null || hitResult.getType() == HitResult.Type.BLOCK)
                return ActionResult.PASS;

            // TODO:

            return ActionResult.PASS;
        });
    }

//    @SubscribeEvent
//    public static void onBucketUsed(FillBucketEvent event){
//        if(event.getTarget() == null || event.getTarget().getType() == HitResult.Type.field_1332)
//            return;
//
//        ItemStack result = fillBucket(event.getWorld(), ((BlockHitResult)event.getTarget()).getBlockPos());
//        if(result.isEmpty())
//            return;
//        event.setFilledBucket(result);
//        event.setResult(Result.ALLOW);
//    }

    public static void onRightClickBlock() {
        RightClickBlockCallback.EVENT.register((player, hand, pos, face) -> {
            // TODO: Cancel if player is mounted on camera

            if (hand == Hand.MAIN_HAND) {
                World world = player.getEntityWorld();

                if (!world.isClient) {
                    BlockEntity tileEntity = world.getBlockEntity(pos);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();

                    // TODO
                }

                //TODO
            }

            return ActionResult.PASS;
        });
    }

//    @SubscribeEvent
//    public static void onRightClickBlock(RightClickBlock event){
//        if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()))
//        {
//            event.setCanceled(true);
//            return;
//        }
//
//        if(event.getHand() == Hand.field_5808)
//        {
//            World world = event.getWorld();
//
//            if(!world.isClient){
//                BlockEntity tileEntity = world.getBlockEntity(event.getPos());
//                BlockState state  = world.getBlockState(event.getPos());
//                Block block = state.getBlock();
//
//                if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.KEY_PANEL))
//                {
//                    for(Block pc : IPasswordConvertible.BLOCKS)
//                    {
//                        if(((IPasswordConvertible)pc).getOriginalBlock() == block)
//                        {
//                            event.setUseBlock(Result.DENY);
//                            event.setUseItem(Result.ALLOW);
//                        }
//                    }
//
//                    return;
//                }
//
//                if(PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.CODEBREAKER) && handleCodebreaking(event)) {
//                    event.setCanceled(true);
//                    return;
//                }
//
//                if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getPlayer(), Items.field_8448) && event.getPlayer().inventory.getMainHandStack().hasCustomName()){
//                    event.setCanceled(true);
//
//                    for(String character : new String[]{"(", ")"})
//                        if(event.getPlayer().inventory.getMainHandStack().getName().getString().contains(character)) {
//                            PlayerUtils.sendMessageToPlayer(event.getPlayer(), new LiteralText("Naming"), ClientUtils.localize("messages.securitycraft:naming.error", event.getPlayer().inventory.getMainHandStack().getName(), character), Formatting.field_1061);
//                            return;
//                        }
//
//                    if(((INameable) tileEntity).getCustomSCName().equals(event.getPlayer().inventory.getMainHandStack().getName())) {
//                        PlayerUtils.sendMessageToPlayer(event.getPlayer(), new LiteralText("Naming"), ClientUtils.localize("messages.securitycraft:naming.alreadyMatches", ((INameable) tileEntity).getCustomSCName()), Formatting.field_1061);
//                        return;
//                    }
//
//                    if(!event.getPlayer().isCreative())
//                        event.getPlayer().inventory.getMainHandStack().decrement(1);
//
//                    ((INameable) tileEntity).setCustomSCName(event.getPlayer().inventory.getMainHandStack().getName());
//                    return;
//                }
//            }
//
//            //outside !world.isRemote for properly checking the interaction
//            //all the sentry functionality for when the sentry is diguised
//            List<SentryEntity> sentries = world.getNonSpectatingEntities(SentryEntity.class, new Box(event.getPos()));
//
//            if(!sentries.isEmpty())
//                event.setCanceled(sentries.get(0).interactMob(event.getPlayer(), event.getHand()) == ActionResult.SUCCESS); //cancel if an action was taken
//        }
//    }

    public static void onBreakBlock() {
        BreakBlockCallback.EVENT.register((world, pos, state, player) -> {
            if (!(world instanceof World))
                return ActionResult.PASS;

            // TODO

            return ActionResult.PASS;
        });
    }

//    @SubscribeEvent
//    public static void onBlockEventBreak(BlockEvent.BreakEvent event)
//    {
//        if(!(event.getWorld() instanceof World))
//            return;
//
//        List<SentryEntity> sentries = ((World)event.getWorld()).getNonSpectatingEntities(SentryEntity.class, new Box(event.getPos()));
//
//        //don't let people break the disguise block
//        if(!sentries.isEmpty())
//        {
//            BlockPos pos = event.getPos();
//
//            if (!sentries.get(0).getDisguiseModule().isEmpty())
//            {
//                ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
//                List<Block> blocks = ((ModuleItem)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());
//
//                if(blocks.size() > 0)
//                {
//                    if(blocks.get(0) == event.getWorld().getBlockState(pos).getBlock())
//                        event.setCanceled(true);
//                }
//            }
//
//            return;
//        }
//
//        sentries = ((World)event.getWorld()).getNonSpectatingEntities(SentryEntity.class, new Box(event.getPos().up()));
//
//        //remove sentry if block below is broken
//        if(!sentries.isEmpty())
//            sentries.get(0).remove();
//    }

    public static void onOwnership() {
        OwnershipEvent.EVENT.register((world, pos, player) -> {
            handleOwnableTEs(world, pos, player);

            return ActionResult.PASS;
        });
    }

    public static void onBlockBroken() {
        BreakBlockCallback.EVENT.register((world, pos, state, player) -> {
            if (world instanceof World && !world.isClient()) {
                if (world.getBlockEntity(pos) instanceof IModuleInventory) {
                    IModuleInventory te = (IModuleInventory) world.getBlockEntity(pos);

                    for (int i = 0; i < te.getMaxNumberOfModules(); i++) {
                        if (!te.getInventory().get(i).isEmpty()) {
                            ItemStack stack = te.getInventory().get(i);
                            ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                            WorldUtils.addScheduledTask(world, () -> world.spawnEntity(item));

                            te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());

                            if (te instanceof CustomizableTileEntity)
                                ((CustomizableTileEntity) te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{stack, ((ModuleItem) stack.getItem()).getModuleType() }, ((CustomizableTileEntity) te));

                            // TODO: cam
                        }
                    }
                }

                //TODO
            }

            return ActionResult.PASS;
        });
    }

//    @SubscribeEvent
//    public static void onBlockBroken(BreakEvent event){
//        if(event.getWorld() instanceof World && !event.getWorld().isClient()) {
//            if(event.getWorld().getBlockEntity(event.getPos()) instanceof IModuleInventory){
//                IModuleInventory te = (IModuleInventory) event.getWorld().getBlockEntity(event.getPos());
//
//                for(int i = 0; i < te.getMaxNumberOfModules(); i++)
//                    if(!te.getInventory().get(i).isEmpty()){
//                        ItemStack stack = te.getInventory().get(i);
//                        ItemEntity item = new ItemEntity((World)event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
//                        WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().spawnEntity(item));
//
//                        te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());
//
//                        if(te instanceof CustomizableTileEntity)
//                            ((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModuleType() }, (CustomizableTileEntity)te);
//
//                        if(te instanceof SecurityCameraTileEntity)
//                        {
//                            SecurityCameraTileEntity cam = (SecurityCameraTileEntity)te;
//
//                            cam.getWorld().updateNeighborsAlways(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).get(SecurityCameraBlock.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock());
//                        }
//                    }
//            }
//
//            List<SentryEntity> sentries = ((World)event.getWorld()).getNonSpectatingEntities(SentryEntity.class, new Box(event.getPos()));
//
//            if(!sentries.isEmpty())
//            {
//                BlockPos pos = event.getPos();
//
//                if (!sentries.get(0).getDisguiseModule().isEmpty())
//                {
//                    ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
//                    List<Block> blocks = ((ModuleItem)disguiseModule.getItem()).getBlockAddons(disguiseModule.getTag());
//
//                    if(blocks.size() > 0)
//                    {
//                        BlockState state = blocks.get(0).getDefaultState();
//
//                        ((World)event.getWorld()).setBlockState(pos, state.getOutlineShape(event.getWorld(), pos) == VoxelShapes.fullCube() ? state : Blocks.field_10124.getDefaultState());
//                    }
//                }
//            }
//        }
//    }

    public static void onLivingSetAttackTarget() {
        //TODO
    }

//    @SubscribeEvent
//    public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
//    {
//        if((event.getTarget() instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(event.getTarget())) || event.getTarget() instanceof SentryEntity)
//            ((MobEntity)event.getEntity()).setTarget(null);
//    }

    public static void onBreakSpeed() {
        //TODO
    }

//    @SubscribeEvent
//    public static void onBreakSpeed(BreakSpeed event)
//    {
//        if(event.getPlayer() != null)
//        {
//            Item held = event.getPlayer().getMainHandStack().getItem();
//
//            if(held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
//            {
//                Block block = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(event.getState().getBlock());
//
//                if(block != null)
//                    event.setNewSpeed(10000.0F);
//            }
//        }
//    }

    public static void onLivingDestroyEvent() {
        //TODO
    }

//    @SubscribeEvent
//    public static void onLivingDestroyEvent(LivingDestroyBlockEvent event)
//    {
//        event.setCanceled(event.getEntity() instanceof WitherEntity && event.getState().getBlock() instanceof IReinforcedBlock);
//    }

    public static void onEntityMount() {
        //TODO
    }

//    @SubscribeEvent
//    public void onEntityMount(EntityMountEvent event)
//    {
//        if(event.isDismounting() && event.getEntityBeingMounted() instanceof SecurityCameraEntity && event.getEntityMounting() instanceof PlayerEntity)
//        {
//            PlayerEntity player = (PlayerEntity)event.getEntityMounting();
//            BlockEntity te = event.getWorldObj().getBlockEntity(event.getEntityBeingMounted().getBlockPos());
//
//            if(PlayerUtils.isPlayerMountedOnCamera(player) && te instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART))
//            {
//                ((SecurityCameraTileEntity)te).lastPitch = player.pitch;
//                ((SecurityCameraTileEntity)te).lastYaw = player.yaw;
//            }
//        }
//    }

    public static void onRightClickItem() {
        //TODO
    }

//    @SubscribeEvent
//    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
//    {
//        if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
//            event.setCanceled(true);
//    }

    public static void onFurnaceFuelBurnTime() {
        //TODO
    }

//    @SubscribeEvent
//    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event)
//    {
//        Item item = event.getItemStack().getItem();
//
//        if(item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ReinforcedCarpetBlock)
//            event.setBurnTime(0);
//    }
//
//    private static ItemStack fillBucket(World world, BlockPos pos){
//        Block block = world.getBlockState(pos).getBlock();
//
//        if(block == SCContent.FAKE_WATER_BLOCK.get()){
//            world.setBlockState(pos, Blocks.AIR.getDefaultState());
//            return new ItemStack(SCContent.FAKE_WATER_BUCKET.get(), 1);
//        }else if(block == SCContent.FAKE_LAVA_BLOCK.get()){
//            world.setBlockState(pos, Blocks.AIR.getDefaultState());
//            return new ItemStack(SCContent.FAKE_LAVA_BUCKET.get(), 1);
//        }
//        else
//            return ItemStack.EMPTY;
//    }

    private static void handleOwnableTEs(World world, BlockPos pos, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof IOwnable) {
            String name = player.getName().getString();
            String uuid = player.getGameProfile().getId().toString();

            ((IOwnable) world.getBlockEntity(pos)).getOwner().set(uuid, name);
        }
    }

//    private static boolean handleCodebreaking(PlayerEntity player, Hand hand, BlockPos pos) {
//        if(ConfigHandler.CONFIG.allowCodebreakerItem)
//        {
//            World world = player.world;
//            BlockEntity tileEntity = player.world.getBlockEntity(pos);
//
//            if(tileEntity instanceof IPasswordProtected)
//            {
//                if(player.getStackInHand(hand).getItem() == SCContent.CODEBREAKER.get())
//                    player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
//
//                if(player.isCreative() || new Random().nextInt(3) == 1)
//                    return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(pos), player, !ConfigHandler.CONFIG.allowCodebreakerItem);
//                else return true;
//            }
//        }
//
//        return false;
//    }

    private static String getRandomTip(){
        String[] tips = { // TODO: Change tips
                "messages.securitycraft:tip.scHelp",
                "messages.securitycraft:tip.trello",
                "messages.securitycraft:tip.patreon",
                "messages.securitycraft:tip.discord",
                "messages.securitycraft:tip.scserver"
        };

        return tips[new Random().nextInt(tips.length)];
    }
}
