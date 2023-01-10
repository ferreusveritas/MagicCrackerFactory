package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.network.CommsThread;
import com.ferreusveritas.mcf.tileentity.WebModemTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
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
     * @param socketHashCode the hash code of the socket the request came in on, used as a connection identifier
     * @param computerID     the ID of the computer the request is being sent to
     * @param path           the path information provided in the request
     * @param params         the additional parameters provided in the request
     * @param headers        the header parameters provided in the request
     * @param data           the actual data for post, put, and patch requests, or otherwise {@code null}
     */
    public void receiveRequest(int socketHashCode, String method, int computerID, String path, Map<String, String> params, Map<String, String> headers, @Nullable String data) {
        IComputerAccess computer = attachedComputers.get(computerID);

        if (computer == null) {
            CommsThread.getInstance().transmitResponse(socketHashCode, 404, "Computer ID " + computerID + " is not available.");
        } else {
            // Combine the path and the parameters into a single array
            Object[] args = new Object[]{socketHashCode, path, params, headers, data};
            computer.queueEvent(method + "_request", args);
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
