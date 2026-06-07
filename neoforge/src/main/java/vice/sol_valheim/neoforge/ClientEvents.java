package vice.sol_valheim.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import vice.sol_valheim.SOLValheim;

@EventBusSubscriber(modid = "sol_valheim", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientEvents
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        SOLValheim.addTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiLayerEvent.Pre event)
    {
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL))
            event.setCanceled(true);
    }
}
