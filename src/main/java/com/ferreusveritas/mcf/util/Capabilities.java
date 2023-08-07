package com.ferreusveritas.mcf.util;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {

    public static Capability<IPeripheral> PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {});

}
