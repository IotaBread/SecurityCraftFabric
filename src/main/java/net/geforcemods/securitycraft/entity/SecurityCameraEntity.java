package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SecurityCraft;
//import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
//import net.geforcemods.securitycraft.network.client.SetPlayerPositionAndRotation;
//import net.geforcemods.securitycraft.network.server.GivePotionEffect;
//import net.geforcemods.securitycraft.network.server.SetCameraPowered;
//import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
//import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
//import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
//import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;
//import net.minecraftforge.fml.network.PacketDistributor;
//import net.minecraftforge.registries.ForgeRegistries;

public class SecurityCameraEntity extends Entity{

	private final double CAMERA_SPEED = ConfigHandler.CONFIG.cameraSpeed;
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;
	private double cameraUseX;
	private double cameraUseY;
	private double cameraUseZ;
	private float cameraUseYaw;
	private float cameraUsePitch;
	private int id;
	private int screenshotCooldown = 0;
	private int redstoneCooldown = 0;
	private int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	private float zoomAmount = 1F;
	private String playerViewingName = null;
	private boolean zooming = false;

	public SecurityCameraEntity(EntityType<SecurityCameraEntity> type, World world){
		super(SCContent.eTypeSecurityCamera, world);
		noClip = true;
	}

	public SecurityCameraEntity(World world, double x, double y, double z, int id, PlayerEntity player){
		this(SCContent.eTypeSecurityCamera, world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = player.getX();
		cameraUseY = player.getY();
		cameraUseZ = player.getZ();
		cameraUseYaw = player.yaw;
		cameraUsePitch = player.pitch;
		this.id = id;
		playerViewingName = player.getName().getString();
		updatePosition(x + 0.5D, y, z + 0.5D);

		BlockEntity te = world.getBlockEntity(getBlockPos());

		if(te instanceof SecurityCameraTileEntity)
			setInitialPitchYaw((SecurityCameraTileEntity)te);
	}

	public SecurityCameraEntity(World world, double x, double y, double z, int id, SecurityCameraEntity camera){
		this(SCContent.eTypeSecurityCamera, world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		playerViewingName = camera.playerViewingName;
		updatePosition(x + 0.5D, y, z + 0.5D);

		BlockEntity te = world.getBlockEntity(getBlockPos());

		if(te instanceof SecurityCameraTileEntity)
			setInitialPitchYaw((SecurityCameraTileEntity)te);
	}

	private void setInitialPitchYaw(SecurityCameraTileEntity te)
	{
		if(te != null && te.hasModule(ModuleType.SMART) && te.lastPitch != Float.MAX_VALUE && te.lastYaw != Float.MAX_VALUE)
		{
			pitch = te.lastPitch;
			yaw = te.lastYaw;
		}
		else
		{
			pitch = 30F;

			Direction facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ())), SecurityCameraBlock.FACING);

			if(facing == Direction.NORTH)
				yaw = 180F;
			else if(facing == Direction.WEST)
				yaw = 90F;
			else if(facing == Direction.SOUTH)
				yaw = 0F;
			else if(facing == Direction.EAST)
				yaw = 270F;
			else if(facing == Direction.DOWN)
				pitch = 75;
		}
	}

	@Override
	public double getMountedHeightOffset(){
		return -0.75D;
	}

	@Override
	protected boolean shouldSetPositionOnLoad(){
		return false;
	}

//	@Override // Forge method, TODO
//	public boolean canBeRiddenInWater(Entity rider){
//		return false;
//	}

//	@Override
//	public void tick(){
//		if(world.isClient && hasPassengers()){
//			PlayerEntity lowestEntity = (PlayerEntity)getPassengerList().get(0);
//
//			if(lowestEntity != MinecraftClient.getInstance().player)
//				return;
//
//			if(screenshotCooldown > 0)
//				screenshotCooldown -= 1;
//
//			if(redstoneCooldown > 0)
//				redstoneCooldown -= 1;
//
//			if(toggleNightVisionCooldown > 0)
//				toggleNightVisionCooldown -= 1;
//
//			if(toggleLightCooldown > 0)
//				toggleLightCooldown -= 1;
//
//			if(lowestEntity.yaw != yaw){
//				lowestEntity.updatePositionAndAngles(lowestEntity.getX(), lowestEntity.getY(), lowestEntity.getZ(), yaw, pitch);
//				lowestEntity.yaw = yaw;
//			}
//
//			if(lowestEntity.pitch != pitch)
//				lowestEntity.updatePositionAndAngles(lowestEntity.getX(), lowestEntity.getY(), lowestEntity.getZ(), yaw, pitch);
//
//			checkKeysPressed();
//
//			boolean isMiddleDown = MinecraftClient.getInstance().mouse.isMiddleDown(); // Forge method, TODO
//
//			if(isMiddleDown && screenshotCooldown == 0){
//				screenshotCooldown = 30;
//				ClientUtils.takeScreenshot();
//				MinecraftClient.getInstance().world.playSound(new BlockPos(getX(), getY(), getZ()), ForgeRegistries.SOUND_EVENTS.getValue(SCSounds.CAMERASNAP.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
//			}
//
//			if(getPassengerList().size() != 0 && shouldProvideNightVision)
//				SecurityCraft.channel.sendToServer(new GivePotionEffect(StatusEffect.getRawId(ForgeRegistries.POTIONS.getValue(new Identifier("night_vision"))), 3, -1));
//		}
//
//		if(!world.isClient)
//			if(getPassengerList().size() == 0 || BlockUtils.getBlock(world, blockPosX, blockPosY, blockPosZ) != SCContent.SECURITY_CAMERA){
//				remove();
//				return;
//			}
//	}

	private void checkKeysPressed() {
		if(MinecraftClient.getInstance().options.keyForward.isPressed())
			moveViewUp();

		if(MinecraftClient.getInstance().options.keyBack.isPressed())
			moveViewDown();

		if(MinecraftClient.getInstance().options.keyLeft.isPressed())
			moveViewLeft();

		if(MinecraftClient.getInstance().options.keyRight.isPressed())
			moveViewRight();

		if(KeyBindings.cameraEmitRedstone.wasPressed() && redstoneCooldown == 0){
			setRedstonePower();
			redstoneCooldown = 30;
		}

		if(KeyBindings.cameraActivateNightVision.wasPressed() && toggleNightVisionCooldown == 0)
			enableNightVision();

		if(KeyBindings.cameraZoomIn.wasPressed())
		{
			zoomIn();
			zooming = true;
		}
		else if(KeyBindings.cameraZoomOut.wasPressed())
		{
			zoomOut();
			zooming = true;
		}
		else
			zooming = false;
	}

	public void moveViewUp() {
		if(isCameraDown())
		{
			if(pitch > 40F)
				setRotation(yaw, pitch -= CAMERA_SPEED);
		}
		else if(pitch > -25F)
			setRotation(yaw, pitch -= CAMERA_SPEED);

//		updateServerRotation();
	}

	public void moveViewDown(){
		if(isCameraDown())
		{
			if(pitch < 100F)
				setRotation(yaw, pitch += CAMERA_SPEED);
		}
		else if(pitch < 60F)
			setRotation(yaw, pitch += CAMERA_SPEED);

//		updateServerRotation();
	}

	public void moveViewLeft() {
		if(world.getBlockState(BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ()))).contains(SecurityCameraBlock.FACING)) {
			Direction facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ())), SecurityCameraBlock.FACING);

			if(facing == Direction.EAST)
			{
				if((yaw - CAMERA_SPEED) > -180F)
					setRotation(yaw -= CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.WEST)
			{
				if((yaw - CAMERA_SPEED) > 0F)
					setRotation(yaw -= CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.NORTH)
			{
				// Handles some problems the occurs from the way the rotationYaw value works in MC
				if((((yaw - CAMERA_SPEED) > 90F) && ((yaw - CAMERA_SPEED) < 185F)) || (((yaw - CAMERA_SPEED) > -190F) && ((yaw - CAMERA_SPEED) < -90F)))
					setRotation(yaw -= CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.SOUTH)
			{
				if((yaw - CAMERA_SPEED) > -90F)
					setRotation(yaw -= CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.DOWN)
				setRotation(yaw -= CAMERA_SPEED, pitch);

//			updateServerRotation();
		}
	}

	public void moveViewRight(){
		if(world.getBlockState(BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ()))).contains(SecurityCameraBlock.FACING)) {
			Direction facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ())), SecurityCameraBlock.FACING);

			if(facing == Direction.EAST)
			{
				if((yaw + CAMERA_SPEED) < 0F)
					setRotation(yaw += CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.WEST)
			{
				if((yaw + CAMERA_SPEED) < 180F)
					setRotation(yaw += CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.NORTH)
			{
				if((((yaw + CAMERA_SPEED) > 85F) && ((yaw + CAMERA_SPEED) < 185F)) || ((yaw + CAMERA_SPEED) < -95F) && ((yaw + CAMERA_SPEED) > -180F))
					setRotation(yaw += CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.SOUTH)
			{
				if((yaw + CAMERA_SPEED) < 90F)
					setRotation(yaw += CAMERA_SPEED, pitch);
			}
			else if(facing == Direction.DOWN)
				setRotation(yaw += CAMERA_SPEED, pitch);

//			updateServerRotation();
		}
	}

	public void zoomIn()
	{
		zoomAmount = Math.min(zoomAmount - 0.1F, 2.0F);

		if(!zooming)
			MinecraftClient.getInstance().world.playSound(getBlockPos(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
	}

	public void zoomOut()
	{
		zoomAmount = Math.max(zoomAmount + 0.1F, -0.5F);

		if(!zooming)
			MinecraftClient.getInstance().world.playSound(getBlockPos(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
	}

	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(getX()), (int) getY(), (int) Math.floor(getZ()));

//		if(((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE)) // TODO
//			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !BlockUtils.getBlockProperty(world, pos, SecurityCameraBlock.POWERED)));
	}

	public void enableNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

//	private void updateServerRotation(){ // TODO
//		SecurityCraft.channel.sendToServer(new SetCameraRotation(yaw, pitch));
//	}

	private boolean isCameraDown()
	{
		return world.getBlockEntity(getBlockPos()) instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)world.getBlockEntity(getBlockPos())).down;
	}

//	@Override
//	public void remove(){
//		super.remove();
//
//		if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
//			PlayerEntity player = PlayerUtils.getPlayerFromName(playerViewingName);
//			player.requestTeleport(cameraUseX, cameraUseY, cameraUseZ);
//			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch)); // TODO
//		}
//	}

	@Override
	protected void initDataTracker(){}

	@Override
	public void writeCustomDataToTag(CompoundTag tag){
		tag.putInt("CameraID", id);

		if(playerViewingName != null)
			tag.putString("playerName", playerViewingName);

		if(cameraUseX != 0.0D)
			tag.putDouble("cameraUseX", cameraUseX);

		if(cameraUseY != 0.0D)
			tag.putDouble("cameraUseY", cameraUseY);

		if(cameraUseZ != 0.0D)
			tag.putDouble("cameraUseZ", cameraUseZ);

		if(cameraUseYaw != 0.0D)
			tag.putDouble("cameraUseYaw", cameraUseYaw);

		if(cameraUsePitch != 0.0D)
			tag.putDouble("cameraUsePitch", cameraUsePitch);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag){
		id = tag.getInt("CameraID");

		if(tag.contains("playerName"))
			playerViewingName = tag.getString("playerName");

		if(tag.contains("cameraUseX"))
			cameraUseX = tag.getDouble("cameraUseX");

		if(tag.contains("cameraUseY"))
			cameraUseY = tag.getDouble("cameraUseY");

		if(tag.contains("cameraUseZ"))
			cameraUseZ = tag.getDouble("cameraUseZ");

		if(tag.contains("cameraUseYaw"))
			cameraUseYaw = tag.getFloat("cameraUseYaw");

		if(tag.contains("cameraUsePitch"))
			cameraUsePitch = tag.getFloat("cameraUsePitch");
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
//		return NetworkHooks.getEntitySpawningPacket(this); // TODO
		return null;
	}
}
