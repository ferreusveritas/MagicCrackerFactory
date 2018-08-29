package com.ferreusveritas.mcf.util;

import net.minecraft.util.math.BlockPos;

public class Arguments {
	
	private final Object[] args;
	private final int numArgs;
	private int curr = 0;
	
	public Arguments(Object[] args) {
		this.args = args;
		this.numArgs = args.length;
	}
	
	public int getNumArgs() {
		return numArgs;
	}
	
	public void reset() {
		curr = 0;
	}
	
	public int i() {
		return i(curr++);
	}
	
	public int i(int arg) {
		return dCheck(arg).intValue();
	}

	public String s() {
		return s(curr++);
	}
	
	public String s(int arg) {
		return o(arg) instanceof String ? ((String)o(arg)) : "";
	}
	
	public BlockPos p() {
		int arg = curr;
		curr += 3;
		return p(arg);
	}
	
	public BlockPos p(int arg) {
		return new BlockPos(i(arg), i(arg + 1), i(arg + 2));
	}
	
	public float f() {
		return f(curr++);
	}
	
	public float f(int arg) {
		return dCheck(arg).floatValue();
	}
	
	public double d() {
		return f(curr++);
	}
	
	public double d(int arg) {
		return dCheck(arg).doubleValue();
	}
	
	public boolean b() {
		return b(curr++);
	}
	
	public boolean b(int arg) {
		return o(arg) instanceof Boolean ? ((Boolean)o(arg)).booleanValue() : false;
	}
	
	public Double dCheck(int arg) {
		return o(arg) instanceof Double ? (Double) o(arg) : new Double(0);
	}
	
	public Object o(int arg) {
		return arg < numArgs ? args[arg] : new Object();
	}
	
}
