package vice.sol_valheim;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.platform.Platform;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

public class FoodHUD implements ClientGuiEvent.RenderHud
{
    static Minecraft client;

    public FoodHUD() {
        ClientGuiEvent.RENDER_HUD.register(this);
        client = Minecraft.getInstance();
    }


    @Override
    public void renderHud(GuiGraphics graphics, DeltaTracker tickDelta) {
        if (client.player == null)
            return;

        var solPlayer = (PlayerEntityMixinDataAccessor) client.player;

        var foodData = solPlayer.sol_valheim$getFoodData();
        if (foodData == null)
            return;

        boolean useLargeIcons = SOLValheim.Config.client.useLargeIcons;

        int width = client.getWindow().getGuiScaledWidth() / 2 + 91;
        int height = client.getWindow().getGuiScaledHeight() - 39 - (useLargeIcons ? 6 : 0);

        int offset = 1;
        int size = useLargeIcons ? 14 : 9;

        for (var food : foodData.ItemEntries) {
            renderFoodSlot(graphics, food, width, size, offset, height, useLargeIcons);
            offset++;
        }

        if (foodData.DrinkSlot != null)
            renderFoodSlot(graphics, foodData.DrinkSlot, width, size, offset, height, useLargeIcons);
    }

    private static void renderFoodSlot(GuiGraphics graphics, ValheimFoodData.EatenFoodItem food, int width, int size, int offset, int height, boolean useLargeIcons)
    {
        var foodConfig = ModConfig.getFoodConfig(food.item);
        if (foodConfig == null)
            return;

        var isDrink = food.item.getDefaultInstance().getUseAnimation() == UseAnim.DRINK;
        int bgColor = isDrink ? FastColor.ARGB32.color(96, 52, 104, 163) : FastColor.ARGB32.color(96, 0, 0, 0);
        int yellow = FastColor.ARGB32.color(255, 255, 191, 0);

        int startWidth = width - (size * offset) - offset + 1;
        float ticksLeftPercent = Float.min(1.0F, (float) food.ticksLeft / foodConfig.getTime());
        int barHeight = Integer.max(1, (int)((size + 2f) * ticksLeftPercent));
        int barColor = ticksLeftPercent < SOLValheim.Config.common.eatAgainPercentage ?
                FastColor.ARGB32.color(180, 255, 10, 10) :
                FastColor.ARGB32.color(96, 0, 0, 0);

        var time = (float) food.ticksLeft / (20 * 60);
        var scale = useLargeIcons ? 0.75f : 0.5f;
        var isSeconds = false;
        String minutes;

        if (time >= 60f)
        {
            var hours = (int) (time / 60f);
            minutes = hours > 9 ? "9+" : hours + "h";
        }
        else if (time < 1f)
        {
            isSeconds = true;
            minutes = String.format("%.0f", (float) food.ticksLeft / 20);
        }
        else
        {
            minutes = String.format("%.0f", time);
        }

        var pose = graphics.pose();

        graphics.fill(startWidth, height, startWidth + size, height + size, bgColor);
        graphics.fill(startWidth, Integer.max(height, height - barHeight + size), startWidth + size, height + size, barColor);

        pose.pushPose();
        pose.scale(scale, scale, scale);
        pose.translate(startWidth * (useLargeIcons ? 0.3333f : 1f), height * (useLargeIcons ? 0.3333f : 1f), 0f);

        if (food.item == Items.CAKE && Platform.isModLoaded("farmersdelight"))
        {
            var cakeSlice = SOLValheim.ITEMS.getRegistrar().get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "cake_slice"));
            graphics.renderItem(new ItemStack(cakeSlice == null ? food.item : cakeSlice, 1), startWidth + 1, height + 1);
        }
        else
        {
            graphics.renderItem(new ItemStack(food.item, 1), startWidth + 1, height + 1);
        }

        pose.pushPose();
        pose.translate(0.0f, 0.0f, 200.0f);

        graphics.drawString(client.font, minutes, startWidth + (minutes.length() > 1 ? 6 : 12), height + 10, isSeconds ? FastColor.ARGB32.color(255, 237, 57, 57) : FastColor.ARGB32.color(255, 255, 255, 255));
        if (!foodConfig.extraEffects.isEmpty())
            graphics.drawString(client.font, "+" + foodConfig.extraEffects.size(), startWidth + 6, height, yellow);

        pose.popPose();
        pose.popPose();
    }
}
