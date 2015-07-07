
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

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
public class LcdExample {

    public final static int LCD_ROWS = 2;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_COLUMNS = 16;
    public final static int LCD_BITS = 4;
    public static String OldTemp="";
    public  static long currentTime,pasttime=0;
    final static GpioController gpio = GpioFactory.getInstance();
    static GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
    public static void main(String args[]) throws InterruptedException {

        String s1;

		s1=getCPUtemp();

        // create gpio controller


        // initialize LCD
        final GpioLcdDisplay lcd = new GpioLcdDisplay(LCD_ROWS,          // number of row supported by LCD
                                                LCD_COLUMNS,       // number of columns supported by LCD
                                                RaspiPin.GPIO_29,  // LCD RS pin
                                                RaspiPin.GPIO_25,  // LCD strobe pin
                                                RaspiPin.GPIO_24,  // LCD data bit 1
                                                RaspiPin.GPIO_23,  // LCD data bit 2
                                                RaspiPin.GPIO_22,  // LCD data bit 3
                                                RaspiPin.GPIO_21); // LCD data bit 4

// provision gpio pins as input pins with its internal pull up resistor enabled
        final GpioPinDigitalInput myButtons[] = {
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "Button", PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, "SoundDetect", PinPullResistance.PULL_UP),
        };

        // create and register gpio pin listener
        gpio.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                Integer Second;
                if(event.getState() == PinState.HIGH){
                    if(event.getPin().getName().equals("Button")) {
                        lcd.clear();
                        lcd.writeln(LCD_ROW_1, "Button Press!", LCDTextAlignment.ALIGN_CENTER);
                        ShutdownPI();
                    }
                    if(event.getPin().getName().equals("SoundDetect")) {
                        /*lcd.clear();
                        lcd.writeln(LCD_ROW_1, "SoundDetect", LCDTextAlignment.ALIGN_CENTER);*/
                        if(pasttime==0) {
                            pasttime=System.currentTimeMillis();
                            ToggleLED();
                        }
                        else {
                            currentTime=System.currentTimeMillis();
                            Second=(int)(getTimeDistanceInMillisecond(currentTime,pasttime)/1000);
                            pasttime=currentTime;
//                            System.out.println("Second:"+Second);
                            if(Second>2)  ToggleLED();
                        }
                    }
                }
                else {
//                    lcd.writeln(LCD_ROW_2,  event.getPin().getName() + " RELEASED" , LCDTextAlignment.ALIGN_CENTER);
//                    System.out.println("Pressed");
                }
            }
        }, myButtons);
        /*final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);
        final GpioPinDigitalInput LightDetect = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_UP);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // when button is pressed, speed up the blink rate on LED #2
                if (event.getState().isHigh()) {
                    lcd.writeln(LCD_ROW_1, "Button Press", LCDTextAlignment.ALIGN_CENTER);
                    ShutdownPI();
                } else {
//                    System.out.println("Low");
                }
            }
        });*/
   /* LightDetect.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // when button is pressed, speed up the blink rate on LED #2
                if (event.getState().isLow()) {
//                    lcd.writeln(LCD_ROW_2, "Dark", LCDTextAlignment.ALIGN_CENTER);
                } else {
//                    System.out.println("Light");
                }
            }
        });*/
        // clear LCD
         lcd.clear();
				lcd.writeln(LCD_ROW_1, "Current CPU Temp", LCDTextAlignment.ALIGN_CENTER);
				lcd.writeln(LCD_ROW_2, s1, LCDTextAlignment.ALIGN_CENTER);
		Thread.sleep(3000);
        lcd.clear();
        lcd.writeln(LCD_ROW_1, "Current Temp", LCDTextAlignment.ALIGN_CENTER);
        lcd.writeln(LCD_ROW_2, getOutsideTemp(), LCDTextAlignment.ALIGN_CENTER);
        Thread.sleep(3000);
        lcd.clear();
        getAllIpAddress(lcd);

        // write line 1 to LCD
//        lcd.write(LCD_ROW_1, "Benny Project");
//        Thread.sleep(3000);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simformatter = new SimpleDateFormat("HH:mm");
        SimpleDateFormat yearformatter = new SimpleDateFormat("yyyy/MM/dd");
        Integer count=0;
        String curtime="";
        lcd.writeln(LCD_ROW_1, yearformatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
        lcd.writeln(LCD_ROW_2, simformatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
        Thread.sleep(3000);
        // update time
        while(true) {
            // write time to line 2 on LCD
            curtime=formatter.format(new Date());

            count+=1;
//            count%10==0
            if(curtime.substring(curtime.lastIndexOf(':') + 1).equals("00")) {
                lcd.clear();
                lcd.writeln(LCD_ROW_1, "Current Temp", LCDTextAlignment.ALIGN_CENTER);
                lcd.writeln(LCD_ROW_2, getOutsideTemp(), LCDTextAlignment.ALIGN_CENTER);
                Thread.sleep(10000);
                lcd.writeln(LCD_ROW_1, yearformatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
                lcd.writeln(LCD_ROW_2, simformatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
                Thread.sleep(1000);
            }
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
    }

    public static void ToggleLED()
    {


        led1.toggle();

    }
    public  static void ShutdownPI()
    {
        String line=null,s1="";
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            Process pr = rt.exec("sudo shutdown -h now");
            Thread.sleep(3000);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));



            while((line=input.readLine()) != null) {
                if(line!=null){
                    s1=s1+line;
                }
            }

            int exitVal = pr.waitFor();
//            System.out.println("Exited with error code "+exitVal);


        }
        catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }
    public  static String  getOutsideTemp()
    {
        String sOut = "";
        try {
            Runtime run = Runtime.getRuntime();
            Process proc= run.exec("sudo /home/pi/prog/lcd1602/sensor-3");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            // read the output from the command
            String s = "";

            while ((s = stdInput.readLine()) != null) {
                sOut=sOut+s;
            }
//            System.out.println(sOut);
            if((sOut.contains("RH")))
            {
//                System.out.println("OUT:"+sOut);
                sOut=sOut.substring(0,7)+" "+sOut.substring(8,16);
                OldTemp=sOut;
//                System.out.println(sOut+"OldTemp:"+OldTemp);
                return  sOut;
            }
            else
                sOut=OldTemp;

//                System.out.println("DHT11 Error");

            // read any errors from the attempted comman
//            Thread.sleep(100000);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  sOut;
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
    public static long getTimeDistanceInMillisecond(long now,long before){
        long dis = -1 ;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowtime = format.format(new Date(now));
        String beforetime = format.format(new Date(before));
        try {
            Date d1 = format.parse(nowtime);  //后的时间
            Date d2 = format.parse(beforetime); //前的时间
            dis = d1.getTime() - d2.getTime();   //两时间差，精确到毫秒
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        return dis ;
    }

}

