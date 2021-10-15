package net.java.tools.bean;

/**
 * 多种状态
 */
public class MultiBitState extends BitState {

    public MultiBitState(int state) {
        super(state);
    }

    public void include(int mask) {
        set(get() | mask);
    }

    public void exclude(int mask) {
        set(get() & ~mask);
    }
    
}
