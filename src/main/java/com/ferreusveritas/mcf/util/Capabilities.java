package com.ferreusveritas.mcf.util;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    @CapabilityInject(IPeripheral.class)
    public static Capability<IPeripheral> PERIPHERAL = null;

}
