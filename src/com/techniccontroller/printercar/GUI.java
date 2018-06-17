/**
 * GUI-class for PrinterCar, which draws an pixel graphic on paper
 * 
 * this class represent the GUI for the User. It allows to select a 
 * image and convert it to a 100px width Black-White image
 * 
 * Last modified by techniccontroller 17.06.2018
 */

package com.techniccontroller.printercar;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JSlider;

import lejos.nxt.Motor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JCheckBox;

public class GUI extends JFrame {

	private File input;							// Variable for image-file
	private BufferedImage originalImage;		// Variable for original image
	private BufferedImage resizeImage;			// Variable for resized image
	private BufferedImage originalImageBW;		// Variable for original image in BLACK/WHITE
	private static final int IMG_WIDTH = 100;	// Width of the printed image
	
	private JPanel contentPane;					// Main-JPanel
	private JPanel panelPreview;				// JPanel for Preview image
	private JPanel panelOriginal;				// JPanel for 
	private JLabel lblOriginalImage;			// JLabel that displays the original image 
	private JLabel lblPreviewImage;				// JLabel that displays the preview image
	private JLabel lblPreview;					// Text-label
	private JLabel lblOriginal;					// Text-label
	private JLabel lblSpeed;					// Text-label
	private JLabel lblSpeed_Value;				// Text-label for speed value
	private JButton btnStart;					// JButton to start printing
	private JButton btnBrowse;					// JButton to Browse image-file
	private JSlider sliderSpeed;				// JSlider for driving speed
	private JCheckBox chckbxSteering;			// Check box to enable the steering function of the car with
												// the arrow keys. I implemented a mechanical function
												// which makes it possible to steer the front wheels when
												// the Z-Axis is in maximum upper position


	// Create the frame
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 745, 595);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelOriginal = new JPanel();
		panelOriginal.setBounds(10, 36, 350, 400);
		contentPane.add(panelOriginal);
		panelOriginal.setLayout(null);

		panelPreview = new JPanel();
		panelPreview.setBounds(370, 36, 350, 400);
		contentPane.add(panelPreview);
		panelPreview.setLayout(null);
		
		
		lblOriginalImage = new JLabel();
		lblPreviewImage = new JLabel();
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Open a file chooser windows when button is pressed
				final JFileChooser chooser = new JFileChooser("Choose directory"); 
		        chooser.setDialogType(JFileChooser.OPEN_DIALOG); 
		        //Setting Up The Filter
		        FileFilter imageFilter = new FileNameExtensionFilter(
		            "Image files", ImageIO.getReaderFileSuffixes());
		        //Attaching Filter to JFileChooser object
		        chooser.setFileFilter(imageFilter);
		        //Displaying Filechooser
		        final int result = chooser.showOpenDialog(null); 
		        
		        // If the User selected a file
		        if (result == JFileChooser.APPROVE_OPTION) { 
		            input = chooser.getSelectedFile(); 
		            // Check if the file is of type PNG or JPG
		            if(input.getName().endsWith(".png") || input.getName().endsWith(".jpg")){
		            	int type = 0;
						try {
							// Read input file as image
							originalImage = ImageIO.read(input);
							type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
							
							// Set width and height for images to display them in GUI
							int widthImageDisplay = 350;
							int heightImageDisplay = (int) (((widthImageDisplay*1.0)/originalImage.getWidth())*originalImage.getHeight());
							
							// Create a BLACK/WHITE version of the original image
							originalImageBW = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
			                Graphics2D g2d = originalImageBW.createGraphics();
			                g2d.drawImage(originalImage, 0, 0, null);
			                g2d.dispose();
			                
			                // Create a resized version of the original BLACK/WHITE image but in original color space
			                resizeImage = resizeImage(originalImageBW, type);
			                
			                // Display the two images in the GUI frame
							Image scaledImageOriginal = originalImage.getScaledInstance(widthImageDisplay,heightImageDisplay,Image.SCALE_SMOOTH);
							Image scaledImagePreview = resizeImage.getScaledInstance(widthImageDisplay,heightImageDisplay,Image.SCALE_SMOOTH);
							lblOriginalImage.setBounds(0, 0, widthImageDisplay, heightImageDisplay);
							lblOriginalImage.setIcon(new ImageIcon(scaledImageOriginal));
							lblPreviewImage.setBounds(0, 0, widthImageDisplay, heightImageDisplay);
							lblPreviewImage.setIcon(new ImageIcon(scaledImagePreview));
							
							panelOriginal.add(lblOriginalImage);
							panelPreview.add(lblPreviewImage);
							
							repaint();
							
							// Enable the start-button
							btnStart.setEnabled(true);
						} catch (IOException e) {
							System.out.println("Error");
							e.printStackTrace();
						}
		            }
		        }
			}
		});
		btnBrowse.setBounds(10, 527, 149, 23);
		contentPane.add(btnBrowse);
		
		lblOriginal = new JLabel("Original:");
		lblOriginal.setBounds(10, 11, 74, 14);
		contentPane.add(lblOriginal);
		
		lblPreview = new JLabel("Preview:");
		lblPreview.setBounds(370, 11, 74, 14);
		contentPane.add(lblPreview);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.startPosition();
				Main.print(resizeImage);
				Main.initSettingsCalibrate();
			}
		});
		btnStart.setBounds(635, 527, 89, 23);
		btnStart.setEnabled(false);
		contentPane.add(btnStart);
		
		lblSpeed = new JLabel("Speed:");
		lblSpeed.setBounds(10, 447, 132, 14);
		contentPane.add(lblSpeed);
		
		sliderSpeed = new JSlider();
		sliderSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblSpeed_Value.setText("" + sliderSpeed.getValue());
				chckbxSteering.setSelected(false);
			}
		});
		
		sliderSpeed.setBounds(152, 447, 334, 26);
		contentPane.add(sliderSpeed);
		
		lblSpeed_Value = new JLabel("50");
		lblSpeed_Value.setBounds(496, 447, 46, 14);
		contentPane.add(lblSpeed_Value);
		
		chckbxSteering = new JCheckBox("Steuern");
		chckbxSteering.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				// Move te motors depending on the pressed key
				if(chckbxSteering.getSelectedObjects() != null){
					Motor.A.setSpeed((360*sliderSpeed.getValue())/100);
					Motor.B.setSpeed(200);
					if(arg0.getKeyCode() == KeyEvent.VK_UP){
						Motor.A.backward();
					}
					if(arg0.getKeyCode() == KeyEvent.VK_DOWN){
						Motor.A.forward();
					}
					if(arg0.getKeyCode() == KeyEvent.VK_LEFT){
						Motor.B.backward();
					}
					if(arg0.getKeyCode() == KeyEvent.VK_RIGHT){
						Motor.B.forward();	
					}
				}
				
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// Stop the motors depending on the released Key
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
					Motor.A.stop();
				}
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT){
					Motor.B.stop();
				}
			}
		});
		chckbxSteering.setBounds(623, 443, 97, 23);
		contentPane.add(chckbxSteering);
	}
	
	// Convert a image into a resized version
	private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, (int) (((IMG_WIDTH*1.0)/originalImage.getWidth())*originalImage.getHeight()), type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
		g.dispose();
		return resizedImage;
	}
}
