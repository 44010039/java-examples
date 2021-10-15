package net.java.tools.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BitStateTest {

    @Test
    public void givenValue_whenIsOperations_thenCorrect() {
        CarState state = new CarState(CarState.STOP);

        Assertions.assertTrue(state.is(CarState.STOP));
        state.set(CarState.START);
        Assertions.assertTrue(state.is(CarState.START));
    }

    @Test
    public void givenValue_whenNotOperations_thenCorrect() {
        CarState state = new CarState(CarState.STOP);

        Assertions.assertFalse(state.not(CarState.STOP));
        Assertions.assertTrue(state.not(CarState.START));
        Assertions.assertTrue(state.not(CarState.RUN));
    }

    @Test
    public void givenValue_whenIsAnyOfOperations_thenCorrect() {
        CarState state = new CarState(CarState.STOP);

        Assertions.assertTrue(state.isAnyOf(CarState.STOP));
        Assertions.assertTrue(state.isAnyOf(CarState.START | CarState.STOP));
        Assertions.assertFalse(state.isAnyOf(CarState.RUN | CarState.START));
    }

    @Test
    public void givenValue_whenIsNotOfOperations_thenCorrect() {
        CarState state = new CarState(CarState.STOP);

        Assertions.assertFalse(state.isNotOf(CarState.STOP));
        Assertions.assertFalse(state.isNotOf(CarState.START | CarState.STOP));
        Assertions.assertTrue(state.isNotOf(CarState.RUN | CarState.START));
    }

    private static final class CarState extends BitState {
        private static final int START = bit(0);
    
        private static final int RUN = bit(1);
    
        private static final int STOP = bit(2); 
    
    
        private CarState(int state) {
            super(state);
        }
    }
}
