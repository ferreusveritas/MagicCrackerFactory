package com.ferreusveritas.mcf.tileentity;

/**
 * A bunch of code held in case I need to revisit testing poisson discs.
 * 
 * @author ferreusveritas
 */
public class PoissonTester {
	
	//testPoisson("nnn", true, "radius1", "radius2", "angle"),
	//testPoisson2("nnnn", true, "radius1", "radius2", "angle", "radius3", "onlyTight"),
	//testPoisson3("nnnnnb", true, "radius1", "delX", "delZ", "radius2", "radius3", "onlyTight"),
	
	
	//case testPoisson: dendroCoil.testPoisson(world, getPos(), cmd.i(), cmd.i(), cmd.d(), cmd.b()); break;
	//case testPoisson2: dendroCoil.testPoisson2(world, getPos(), cmd.i(), cmd.i(), cmd.d(), cmd.i(), cmd.b()); break;
	//case testPoisson3: dendroCoil.testPoisson3(world, getPos(), cmd.i(), getPos().add(cmd.i(), 0, cmd.i()), cmd.i(), cmd.i()); break;

	
	
	/*
	public void testPoisson(World world, BlockPos pos, int rad1, int rad2, double angle, boolean onlyTight) {
		pos = pos.up();
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(pos.add(x, y, z));
				}
			}
		}
		
		if(rad1 >= 2 && rad2 >= 2 && rad1 <= 8 && rad2 <= 8) {
			Circle circleA = new Circle(pos, rad1);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, pos.getY(), EnumGeneratorResult.NOTREE, 3);

			Circle circleB = CircleHelper.findSecondCircle(circleA, rad2, angle, onlyTight);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			world.setBlockState(new BlockPos(circleB.x, pos.up().getY(), circleB.z), circleB.isTight() ? Blocks.DIAMOND_BLOCK.getDefaultState() : Blocks.COBBLESTONE.getDefaultState());
		}
	}
	
	public void testPoisson2(World world, BlockPos pos, int rad1, int rad2, double angle, int rad3, boolean onlyTight) {
		pos = pos.up();
				
		//System.out.println("Test: " + "R1:" + rad1 + ", R2:" + rad2 + ", angle:" + angle + ", R3:" + rad3);
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(pos.add(x, y, z));
				}
			}
		}
		
		if(rad1 >= 2 && rad2 >= 2 && rad1 <= 8 && rad2 <= 8 && rad3 >= 2 && rad3 <= 8) {
			Circle circleA = new Circle(pos, rad1);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			
			Circle circleB = CircleHelper.findSecondCircle(circleA, rad2, angle, onlyTight);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			
			CircleHelper.maskCircles(circleA, circleB);
			
			Circle circleC = CircleHelper.findThirdCircle(circleA, circleB, rad3);
			if(circleC != null) {
				TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleC, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			} else {
				System.out.println("Angle:" + angle);
				world.setBlockState(new BlockPos(circleA.x, pos.up().getY(), circleA.z), Blocks.REDSTONE_BLOCK.getDefaultState());
			}
		}
	}
	
	public void testPoisson3(World world, BlockPos posA, int radA, BlockPos posB, int radB, int radC) {
		posA = posA.up();
		posB = posB.up();
				
		//System.out.println("Test: " + "R1:" + rad1 + ", R2:" + rad2 + ", angle:" + angle + ", R3:" + rad3);
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(posA.add(x, y, z));
				}
			}
		}
		
		if(radA >= 2 && radB >= 2 && radA <= 8 && radB <= 8 && radC >= 2 && radC <= 8) {
			Circle circleA = new Circle(posA, radA);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, posA.getY(), EnumGeneratorResult.NOTREE, 3);
			
			Circle circleB = new Circle(posB, radB);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, posB.getY(), EnumGeneratorResult.NOTREE, 3);
			
			CircleHelper.maskCircles(circleA, circleB);
			
			Circle circleC = CircleHelper.findThirdCircle(circleA, circleB, radC);
			if(circleC != null) {
				TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleC, posA.getY(), EnumGeneratorResult.NOTREE, 3);
			} else {
				world.setBlockState(new BlockPos(circleA.x, posA.up().getY(), circleA.z), Blocks.REDSTONE_BLOCK.getDefaultState());
			}
		}
	}*/

	
}
