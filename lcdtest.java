
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  LcdExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2015 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class lcdtest {

    public final static int LCD_ROWS = 2;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_COLUMNS = 16;
    public final static int LCD_BITS = 4;

    public static void main(String args[]) throws InterruptedException {
		String s1;

        System.out.println("<--Pi4J--> GPIO 4 bit LCD example program");

		s1=getCPUtemp();

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // initialize LCD
        final GpioLcdDisplay lcd = new GpioLcdDisplay(LCD_ROWS,          // number of row supported by LCD
                                                LCD_COLUMNS,       // number of columns supported by LCD
                                                RaspiPin.GPIO_15,  // LCD RS pin
                                                RaspiPin.GPIO_16,  // LCD strobe pin
                                                RaspiPin.GPIO_25,  // LCD data bit 1
                                                RaspiPin.GPIO_24,  // LCD data bit 2
                                                RaspiPin.GPIO_23,  // LCD data bit 3
                                                RaspiPin.GPIO_28); // LCD data bit 4



        // create and register gpio pin listener
        lcd.clear();
		lcd.writeln(LCD_ROW_1,"Current Temp", LCDTextAlignment.ALIGN_CENTER);
		lcd.writeln(LCD_ROW_2, s1, LCDTextAlignment.ALIGN_CENTER);
		Thread.sleep(3000);
		getAllIpAddress(lcd);

        // clear LCD
        lcd.clear();
        Thread.sleep(1000);

        // write line 1 to LCD

		/*
        // write line 2 to LCD
        lcd.write(LCD_ROW_2, "----------------");

        // line data replacement
        for(int index = 0; index < 5; index++)
        {
            lcd.write(LCD_ROW_2, "----------------");
            Thread.sleep(500);
            lcd.write(LCD_ROW_2, "****************");
            Thread.sleep(500);
        }
        lcd.write(LCD_ROW_2, "----------------");
			*/
		/*	 lcd.clear();
        Thread.sleep(1000);
		lcd.writeln(LCD_ROW_1,"LAN IP", LCDTextAlignment.ALIGN_CENTER);
		lcd.writeln(LCD_ROW_2, "192.168.0.1", LCDTextAlignment.ALIGN_CENTER);
		Thread.sleep(3000);

				lcd.writeln(LCD_ROW_1, "WLAN0 IP", LCDTextAlignment.ALIGN_CENTER);
				lcd.writeln(LCD_ROW_2, "192.168.0.1", LCDTextAlignment.ALIGN_CENTER);
		Thread.sleep(3000);*/

        // single character data replacement
        /*for(int index = 0; index < lcd.getColumnCount(); index++) {
            lcd.write(LCD_ROW_2, index, "Current IP Address:192.168.0.1");
            if(index > 0)
                lcd.write(LCD_ROW_2, index - 1, "-");
            Thread.sleep(300);
        }
        for(int index = lcd.getColumnCount()-1; index >= 0 ; index--) {
            lcd.write(LCD_ROW_2, index, "Eva");
            if(index < lcd.getColumnCount()-1)
                lcd.write(LCD_ROW_2, index + 1, "-");
            Thread.sleep(300);
        }*/

		/*
        // left alignment, full line data
        lcd.write(LCD_ROW_2, "----------------");
        Thread.sleep(500);
        lcd.writeln(LCD_ROW_2, "<< LEFT");
        Thread.sleep(1000);

        // right alignment, full line data
        lcd.write(LCD_ROW_2, "----------------");
        Thread.sleep(500);
        lcd.writeln(LCD_ROW_2, "RIGHT >>", LCDTextAlignment.ALIGN_RIGHT);
        Thread.sleep(1000);

        // center alignment, full line data
        lcd.write(LCD_ROW_2, "----------------");
        Thread.sleep(500);
        lcd.writeln(LCD_ROW_2, "<< CENTER >>", LCDTextAlignment.ALIGN_CENTER);
        Thread.sleep(1000);
		*/
        // mixed alignments, partial line data
        /*lcd.write(LCD_ROW_2, "----------------");
        Thread.sleep(500);
        lcd.write(LCD_ROW_2, "<L>", LCDTextAlignment.ALIGN_LEFT);
        lcd.write(LCD_ROW_2, "<R>", LCDTextAlignment.ALIGN_RIGHT);
        lcd.write(LCD_ROW_2, "CC", LCDTextAlignment.ALIGN_CENTER);
        Thread.sleep(3000);
        */
 		lcd.write(LCD_ROW_1, "Benny Project");

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
 		while(true) {
            // write time to line 2 on LCD

                lcd.writeln(LCD_ROW_2, formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);

            Thread.sleep(1000);
        }


        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
    }

public  static String getCPUtemp()

{
	String line=null,s1="";
	try {
                Runtime rt = Runtime.getRuntime();
                //Process pr = rt.exec("cmd /c dir");
                Process pr = rt.exec("vcgencmd measure_temp");

                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));



                while((line=input.readLine()) != null) {
					if(line!=null){
                    s1=s1+line;
				}
                }

                int exitVal = pr.waitFor();
                //System.out.println("Exited with error code "+exitVal);
				return s1;

            }
            catch(Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }

return s1;

}
public static void getAllIpAddress(GpioLcdDisplay lcd) {
        try {
            //get all network interface
            Enumeration<NetworkInterface> allNetworkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface = null;

            //check if there are more than one network interface
            while (allNetworkInterfaces.hasMoreElements()) {
                //get next network interface
                networkInterface = allNetworkInterfaces.nextElement();
                 lcd.clear();



				lcd.writeln(LCD_ROW_1,networkInterface.getDisplayName(), LCDTextAlignment.ALIGN_CENTER);
                //output interface's name
                /*System.out.println("network interface: " +
                        networkInterface.getDisplayName());
                        */

                                //get all ip address that bound to this network interface
                Enumeration<InetAddress> allInetAddress =
                        networkInterface.getInetAddresses();

                InetAddress ipAddress = null;

                //check if there are more than one ip addresses
                //band to one network interface
                while (allInetAddress.hasMoreElements()) {
                    //get next ip address
                    ipAddress = allInetAddress.nextElement();

                    if (ipAddress != null && ipAddress instanceof Inet4Address) {
                        /*System.out.println("ip address: " +
                                ipAddress.getHostAddress());*/
                    lcd.writeln(LCD_ROW_2,ipAddress.getHostAddress(),LCDTextAlignment.ALIGN_CENTER);
                    Thread.sleep(3000);
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch(InterruptedException e)
        {
			e.printStackTrace();
		}
    }//end method getAllIpAddress

}

