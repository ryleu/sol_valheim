package vice.sol_valheim.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

@Mixin(value = {Item.class}, priority = 1500)
public class ItemMixin
{
    @Inject(at= {@At("HEAD")}, method = {"use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"}, cancellable = true)
    private void onCanConsume(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info)
    {
        var item = (Item) (Object) this;
        ItemStack itemStack = player.getItemInHand(usedHand);
        var foodProperties = itemStack.get(DataComponents.FOOD);

        if (foodProperties != null) {
            if (item == Items.ROTTEN_FLESH) {
                player.startUsingItem(usedHand);

                info.setReturnValue(InteractionResultHolder.consume(itemStack));
                info.cancel();
                return;
            }

            var canEat = ((PlayerEntityMixinDataAccessor) player).sol_valheim$getFoodData().canEat(item);
            if (canEat || foodProperties.canAlwaysEat()) {
                player.startUsingItem(usedHand);

                info.setReturnValue(InteractionResultHolder.consume(itemStack));
                info.cancel();
                return;
            }

            info.setReturnValue(InteractionResultHolder.fail(itemStack));
            info.cancel();
            return;
        }

        info.setReturnValue(InteractionResultHolder.pass(itemStack));
        info.cancel();
    }
}
