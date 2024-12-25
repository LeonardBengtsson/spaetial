package spaetial.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class BuilderAllayEntity extends AllayEntity implements InventoryOwner, Ownable {

    private static final String ENTITY_ID = "builder_allay";
    public static final EntityType<BuilderAllayEntity> ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, Spaetial.id(ENTITY_ID),
            new BuilderAllayEntityType(BuilderAllayEntity::new, SpawnGroup.MISC, true, true,true, true,
                    ImmutableSet.of(), EntityDimensions.fixed(.35f, .6f), 5, 100, 2, FeatureSet.empty()));

    private final SimpleInventory inventory = new SimpleInventory(1);

    private UUID owner;
    private boolean shackled;
    private BlockPos shackleOrigin;
    private double shackleRange;

    private float field_38936;
    private float field_38935;

    protected BuilderAllayEntity(EntityType<BuilderAllayEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new BuilderAllayMoveControl(this);
        this.noClip = true;
        this.setInvulnerable(true);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);

        if (this.getWorld().isClient) {
            this.field_38936 = this.field_38935;
            if (this.isHoldingItem()) {
                this.field_38935 = MathHelper.clamp(this.field_38935 + 1.0F, 0.0F, 5.0F);
            } else {
                this.field_38935 = MathHelper.clamp(this.field_38935 - 1.0F, 0.0F, 5.0F);
            }
        }

        PlayerEntity player = this.getWorld().getClosestPlayer(this, 5);
        if (player != null) {
            this.setStackInHand(Hand.MAIN_HAND, player.getMainHandStack());
            this.getInventory().markDirty();
//            Spaetial.log("replaced main hand with", player.getMainHandStack().getItem().getName().getString());
        }
//        Spaetial.log("main hand item", this.getMainHandStack());
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return Objects.requireNonNull(this.getWorld().getServer()).getPlayerManager().getPlayer(this.owner);
    }

    public boolean isShackled() {
        return shackled;
    }

    public void shackle() {
        shackled = true;
        owner = null;
    }

    public void unshackle(UUID newOwner) {
        shackled = false;
        owner = newOwner;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return true;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasStackEquipped(EquipmentSlot.MAINHAND) ? SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.owner != null) {
            nbt.putUuid("Owner", this.owner);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.containsUuid("Owner")) {
            this.owner = nbt.getUuid("Owner");
        } else {
            this.owner = null;
        }
    }

    // imma be honest I have no clue what this does, it's just copied from AllayEntity
    public float method_43397(float f) {
        return MathHelper.lerp(f, this.field_38936, this.field_38935) / 5.0F;
    }

    private static class BuilderAllayMoveControl extends MoveControl {
        public BuilderAllayMoveControl(BuilderAllayEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
//            if (this.state == State.MOVE_TO) {
//                Vec3d vec3d = new Vec3d(this.targetX - VexEntity.this.getX(), this.targetY - VexEntity.this.getY(), this.targetZ - VexEntity.this.getZ());
//                double d = vec3d.length();
//                if (d < VexEntity.this.getBoundingBox().getAverageSideLength()) {
//                    this.state = State.WAIT;
//                    VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
//                } else {
//                    VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
//                    if (VexEntity.this.getTarget() == null) {
//                        Vec3d vec3d2 = VexEntity.this.getVelocity();
//                        VexEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
//                        VexEntity.this.bodyYaw = VexEntity.this.getYaw();
//                    } else {
//                        double e = VexEntity.this.getTarget().getX() - VexEntity.this.getX();
//                        double f = VexEntity.this.getTarget().getZ() - VexEntity.this.getZ();
//                        VexEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
//                        VexEntity.this.bodyYaw = VexEntity.this.getYaw();
//                    }
//                }
//
//            }
        }
    }

    public static class BuilderAllayEntityType extends EntityType<BuilderAllayEntity> {
        public BuilderAllayEntityType(EntityFactory<BuilderAllayEntity> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, float spawnBoxScale, int maxTrackDistance, int trackTickInterval, FeatureSet requiredFeatures) {
            super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, spawnBoxScale, maxTrackDistance, trackTickInterval, Spaetial.translationKey("entity_type", "builder_allay"), Optional.empty(), requiredFeatures);
        }

        @Override
        public @Nullable BuilderAllayEntity spawnFromItemStack(ServerWorld world, @Nullable ItemStack stack, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
            Consumer<BuilderAllayEntity> consumer;
            if (stack != null) {
                consumer = copier(world, stack, player);
            } else {
                consumer = (entity) -> {};
            }

            BuilderAllayEntity entity = this.spawn(world, consumer, pos, spawnReason, alignPosition, invertY);

            // this is the entire reason for this subclass
            if (entity != null && player != null) {
                entity.unshackle(player.getUuid());
            }

            return entity;
        }
    }
}
