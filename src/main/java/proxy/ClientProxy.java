package proxy;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		registerClientEventHandlers();
	}
	
	public void registerClientEventHandlers() {

	}
	
	@Override
	public void init() {
		super.init();
		registerColorHandlers();
	}
	
	@Override
	public void registerModels() {
		
	}
	
	public void registerColorHandlers() {
		
	}
	
}
