package slimeknights.tmechworks.integration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public interface IInformationProvider {
    @OnlyIn(Dist.CLIENT)
    void getInformation(@Nonnull List<String> info, @Nonnull InformationType type);

    enum InformationType {
        HEAD, BODY, TAIL
    }
}
