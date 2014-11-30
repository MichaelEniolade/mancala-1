package me.dacol.marco.mancala;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;


// This has the purpose to block my test until an event it's posted on the action context
// in this way I can check if the right response is given from the component
public class TestBlockingObserver implements Observer {
    private CountDownLatch latch = new CountDownLatch(1);

    public void waitUntilUpdateIsCalled() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        latch.countDown();
    }
}

