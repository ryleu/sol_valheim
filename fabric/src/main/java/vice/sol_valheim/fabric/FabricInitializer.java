package vice.sol_valheim.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.ValheimFoodData;

public class FabricInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        EntityDataSerializers.registerSerializer(ValheimFoodData.FOOD_DATA_SERIALIZER);
        SOLValheim.init();
    }
}
