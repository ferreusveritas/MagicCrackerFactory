package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.network.CommsThread;
import com.ferreusveritas.mcf.tileentity.WebModemTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebModemPeripheral extends MCFPeripheral<WebModemTileEntity> {

    private final Map<Integer, IComputerAccess> attachedComputers = new HashMap<>();

    public WebModemPeripheral(WebModemTileEntity block) {
        super(block);
    }

    /**
     * Send a reply back to the outside world.
     */
    @LuaFunction
    public int sendReply(int socketHashCode, int responseCode, String responseData) {
        CommsThread.getInstance().transmitResponse(socketHashCode, responseCode, responseData);
        return 0;
    }

    /**
     * Receives a request from the outside world and passes it to a computer we're attached to.
     *
     * @param socketHashCode The hash code of the socket the request came in on, used as a connection identifier.
     * @param computerID     The ID of the computer the request is being sent to.
     * @param path           The path information provided in the request.
     * @param params         The additional parameters provided in the request.
     */
    public void receiveRequest(int socketHashCode, int computerID, String path, List<String> params) {
        IComputerAccess computer = attachedComputers.get(computerID);

        if (computer == null) {
            CommsThread.getInstance().transmitResponse(socketHashCode, 404, "Computer ID " + computerID + " is not available.");
        } else {
            // Combine the path and the parameters into a single array
            Object[] args = new Object[params.size() + 2];
            args[0] = socketHashCode;
            args[1] = path;

            for (int i = 0; i < params.size(); i++) {
                args[i + 2] = params.get(i);
            }

            computer.queueEvent("webModem_request", args);
        }
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        attachedComputers.put(computer.getID(), computer);
        CommsThread.getInstance().registerModem(computer.getID(), this);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        CommsThread.getInstance().unregisterModem(computer.getID());
        attachedComputers.remove(computer.getID());
    }
}
