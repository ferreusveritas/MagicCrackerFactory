package com.ferreusveritas.mcf.tileentity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ferreusveritas.mcf.network.CommsThread;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileWebModem extends MCFPeripheral {

	private final Map<Integer, IComputerAccess> m_attachedComputers;
	
	public static final String WEBMODEM = "webmodem";
	
	public TileWebModem() {
		super(WEBMODEM);
		m_attachedComputers = new HashMap<Integer, IComputerAccess>();
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		sendReply("nns" , "connId,respCode,respData", true, (world, peri, args) -> obj(getTool(peri).sendReply(args.i(0), args.i(1), args.s(2))) );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, boolean synced, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process, synced); }
		
		public static TileWebModem getTool(MCFPeripheral peripheral) {
			return (TileWebModem) peripheral;
		}
		
		@Override
		public MethodDescriptor getMethodDescriptor() {
			return md;
		}
		
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	/**
	 * Send a reply back to the outside world.
	 * @param arguments The data provided by the ComputerCraft program.
	 */
	private int sendReply(int socketHashCode, int responseCode, String responseData) {
		CommsThread.getInstance().transmitResponse(socketHashCode, responseCode, responseData);
		return 0;
	}

	/**
	 * Receives a request from the outside world and passes it to a computer we're attached to.
	 * @param socketHashCode The hash code of the socket the request came in on, used as a connection identifier.
	 * @param computerID The ID of the computer the request is being sent to.
	 * @param path The path information provided in the request.
	 * @param params The additional parameters provided in the request.
	 */
	public void receiveRequest(int socketHashCode, int computerID, String path, List<String> params) {
		IComputerAccess computer = m_attachedComputers.get(computerID);
		
		if (computer == null) {
			CommsThread.getInstance().transmitResponse(socketHashCode, 404, "Computer ID " + computerID + " is not available.");
		}
		else {
			// Combine the path and the parameters into a single array
			Object[] args = new Object[params.size() + 2];
			args[0] = socketHashCode;
			args[1] = path;
			
			for(int i = 0; i < params.size(); i++) {
				args[i + 2] = params.get(i);
			}
			
			computer.queueEvent("webModem_request", args);
		}
	}
	
	@Override
	public void attach(IComputerAccess computer) {
		m_attachedComputers.put(computer.getID(), computer);
		CommsThread.getInstance().registerModem(computer.getID(), this);
	}

	@Override
	public void detach(IComputerAccess computer) {
		CommsThread.getInstance().unregisterModem(computer.getID(), this);
		m_attachedComputers.remove(computer.getID());
	}
	
	
}
