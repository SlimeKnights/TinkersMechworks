package slimeknights.tmechworks.integration;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public interface IInformationProvider {
    @SideOnly(Side.CLIENT)
    void getInformation(@Nonnull List<String> info, @Nonnull InformationType type);

    enum InformationType {
        HEAD, BODY, TAIL
    }
}
