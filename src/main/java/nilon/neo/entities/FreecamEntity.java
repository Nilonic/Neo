package nilon.neo.entities;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;

public class FreecamEntity extends LivingEntity {

    public FreecamEntity(ClientWorld world) {
        super(EntityType.PLAYER, world);  // Replace this with a proper FreecamEntity type if available
        this.noClip = true;  // Disable collision
    }

    public void updatePosition(Vec3d position) {
        this.setPos(position.x, position.y, position.z);
    }

    @Override
    public boolean isSpectator() {
        return true;  // Set to spectator to avoid interaction
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) { }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;  // Arbitrary, unused value
    }
}
