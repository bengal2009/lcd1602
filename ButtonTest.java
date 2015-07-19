/**
 * Created by Lin on 2015/6/2.
 */
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
public class ButtonTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Benny Lin");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 & #03 as an output pins and blink

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital()  {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)  {
                // when button is pressed, speed up the blink rate on LED #2
                if (event.getState().isHigh()) {
                    System.out.println("High");
                } else {
                    System.out.println("Down");

                }
            }
        });


        // keep program running until user aborts (CTRL-C)
        for (; ; ) {
            Thread.sleep(500);
        }
    }
}
