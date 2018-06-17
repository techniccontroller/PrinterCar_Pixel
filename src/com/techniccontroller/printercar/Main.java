/**
 * Main-class for PrinterCar, which draws an pixel graphic on paper
 * 
 * this class handles all the movements of the robot
 * 
 * Last modified by techniccontroller 17.06.2018
 */

package com.techniccontroller.printercar;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.remote.RemoteMotor;
import lejos.util.Delay;

import com.techniccontroller.printercar.GUI;


public class Main {
	
	private static RemoteMotor ZMotor;			// motor for Z-axis (UP/DOWN)
	private static RemoteMotor YMotor;			// motor for Y-axis (FORWARD/BACKWARD)
	private static RemoteMotor XMotor;			// motor for X-axis	(LEFT/RIGHT)
	private static TouchSensor Ztouch;			// Touch Sensor for Calibration of Z-Axis
	private static TouchSensor Xtouch;			// Touch Sensor for Calibration of X-Axis
	
	
	// Initialise the Motors and Sensors
	public static void init() throws IOException {
		Ztouch = new TouchSensor(SensorPort.S3);
		Xtouch = new TouchSensor(SensorPort.S4);
		XMotor = Motor.B;
		YMotor = Motor.A;
		ZMotor = Motor.C;
	}

	// Set the motor settings and calibrate the Z-axis
	public static void initSettingsCalibrate(){
		YMotor.setSpeed(80);
		YMotor.resetTachoCount();
		ZMotor.setSpeed(180);
		ZMotor.forward();
		while (!Ztouch.isPressed());
		ZMotor.stop();
		ZMotor.resetTachoCount();
		XMotor.setSpeed(700);
		Delay.msDelay(2000);
	}
	
	// Move all Motors in startposition, 
	// call some motors commands twice to reduce influence of gear backlash
	public static void startPosition() {
		YMotor.setSpeed(80);
		YMotor.resetTachoCount();
		ZMotor.setSpeed(180);
		ZMotor.rotateTo(-500);
		ZMotor.rotateTo(-420);
		XMotor.setSpeed(700);
		XMotor.backward();
		while (!Xtouch.isPressed());
		XMotor.stop();
		XMotor.resetTachoCount();
		XMotor.rotateTo(3000);
		Delay.msDelay(2000);

	}
	
	// this function prints the given image
	public static void print(BufferedImage image){
		// Move to line begin (LEFT)
		// call the motors command twice to reduce influence of gear backlash
		XMotor.rotateTo(3200);
		XMotor.rotateTo(3000);
		
		boolean black = false;			// Variable that saves the state (BLACK/WHITE) of the next move
		int deltaXStepsCount = 0;		// Variable that counts how many steps the next move is long
		Color tempColor;				// Color of the current Pixel
		
		// Loop through each row and column of the image
		// getRed() delivers a value of 0 if the pixel is black otherwise larger than 0
		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				tempColor = new Color(image.getRGB(x, y));
				// If the current pixel is black and the current planned move is also black, 
				// we can simply increment the steps.   
				if(tempColor.getRed() == 0 && black){
					deltaXStepsCount++;
				}
				// If current pixel is black and the current planned move is not black, 
				// this move must first be executed and then a new planned move must be started. 
				else if(tempColor.getRed() == 0 && !black){
					up();
					XMotor.rotateTo(XMotor.getTachoCount() - 20*deltaXStepsCount);
					// Start new planned move with color BLACK and length 1
					black = true;
					deltaXStepsCount = 1;
				}
				// If the current pixel is not black and the current planned move is also not black, 
				// we can simply increment the steps.  
				else if(tempColor.getRed() > 0 && !black){
					deltaXStepsCount++;
				}
				// If current pixel is not black and the current planned move is black, 
				// this move must first be executed and then a new planned move must be started. 
				else if(tempColor.getRed() > 0 && black){
					down();
					XMotor.rotateTo(XMotor.getTachoCount() - 20*deltaXStepsCount);
					// Start new planned move with color NOT BLACK and length 1
					black = false;
					deltaXStepsCount = 1;
				}
				// If the user press Escape or the slider reaches the mechanical end
				// stop the execution
				if (Button.ESCAPE.isDown() || Xtouch.isPressed()) {
					break;
				}
			}
			// If current planned move is black, 
			// we need to execute it at the end of the row
			if(black){
				down();
				XMotor.rotateTo(XMotor.getTachoCount() - 20*deltaXStepsCount);
			}
			deltaXStepsCount = 0;
			// If the user press Escape or the pen reaches the mechanical end
			// stop the execution
			if (Button.ESCAPE.isDown() || Xtouch.isPressed()) {
				break;
			}
			// move the pen up
			up();
			// Move to line begin (LEFT)
			// call the motors command twice to reduce influence of gear backlash 
			XMotor.rotateTo(3200);
			XMotor.rotateTo(3000);
			// increment the Y-Axis to draw next line
			YMotor.rotateTo(YMotor.getTachoCount() + 4);
		}
	}
	
	// moves the pen down
	public static void down() {
		ZMotor.rotateTo(-500);

	}
	
	// moves the pen up
	public static void up() {
		ZMotor.rotateTo(-450);
	}
	
	// Everything starts here.... 
	public static void main(String[] args) throws IOException {
		init();
		initSettingsCalibrate();
		// Open GUI window
		GUI frame = new GUI();
		frame.setVisible(true);

	}

}
