package com.ferreusveritas.mcf.util;

import dan200.computercraft.api.lua.IArguments;
import net.minecraft.core.BlockPos;

public class Arguments {

    private final IArguments args;
    private final int numArgs;
    private int curr = 0;

    public Arguments(IArguments args) {
        this.args = args;
        this.numArgs = args.count();
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
        return o(arg) instanceof String ? ((String) o(arg)) : "";
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
        return o(arg) instanceof Boolean && ((Boolean) o(arg)).booleanValue();
    }

    public Double dCheck(int arg) {
        return o(arg) instanceof Double ? (Double) o(arg) : Double.valueOf(0);
    }

    public Object o(int arg) {
        return arg < numArgs ? args.get(arg) : new Object();
    }

}
