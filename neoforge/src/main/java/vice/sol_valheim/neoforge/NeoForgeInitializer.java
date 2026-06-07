package vice.sol_valheim.neoforge;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.ValheimFoodData;

@Mod(SOLValheim.MOD_ID)
public class NeoForgeInitializer
{
    private static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, SOLValheim.MOD_ID);

    static {
        ENTITY_DATA_SERIALIZERS.register("food_data", () -> ValheimFoodData.FOOD_DATA_SERIALIZER);
    }

    public NeoForgeInitializer(IEventBus modEventBus)
    {
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        SOLValheim.init();
    }
}
