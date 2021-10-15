package net.java.tools.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiBitStateTest {
    @Test
    public void whenIncludeOperations_thenCorrect() {
        CarState state = new CarState(CarState.ENGINE_STOP);
        state.include(CarState.CAR_STOP);

        Assertions.assertFalse(state.is(CarState.CAR_STOP));
        Assertions.assertFalse(state.is(CarState.ENGINE_STOP));
        Assertions.assertFalse(state.is(CarState.CAR_RUN));
        
        Assertions.assertTrue(state.isAnyOf(CarState.ENGINE_STOP | CarState.CAR_RUN));
        Assertions.assertFalse(state.isNotOf(CarState.ENGINE_STOP));
        Assertions.assertFalse(state.isNotOf(CarState.ENGINE_STOP | CarState.CAR_RUN));
        Assertions.assertTrue(state.isNotOf(CarState.ENGINE_START));
    }

    private static final class CarState extends MultiBitState {
        private static final int ENGINE_START = bit(0);
    
        private static final int CAR_RUN = bit(1);
    
        private static final int ENGINE_STOP = bit(2); 

        private static final int CAR_STOP = bit(1);
    
    
        private CarState(int state) {
            super(state);
        }
    }
}
