package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.Integer;
import java.nio.file.Path;
import java.text.FieldPosition;

import static java.io.FileWriter.*;

public class main {
    public static void main(String[] args) throws IOException {
        final BufferedImage[] img = {null};
        final BufferedImage[] img1 = {null};


        final int TOP_MARGIN = 132;
        final int SHOW_MARGIN = 0;

        final int[] width= {0};
        final int[] height = {0};
        final int[] pathPixNum = {0};

        // Global array for plot of 1D curve
        double[] plotLine = new double[5000];

        final String[] filename = new String[1];

        // The width of the drawn line in the picture (guiding the eye)
        int lineWidth = 4;
        // Half the width for paining on both sides of the pixel
        lineWidth = lineWidth/2;

        // The integers for the whole RGB representation as well as its single color parts
        final int[] rgb = new int[1];
        int red;
        int green;
        int blue;
        double brightness;


        // Output array for plot of image darkness
        //double[][] xyPlot = new double[width][height];

        // Loop through all pixels in the picture
        /*
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Extract the RGB value of the current pixel
                //(only binary/hexadecimal representation useful for reading!)

                rgb[0] = img[0].getRGB(i,j);

                // Get the components of the single colors by dividing up the RGB integer
                blue = rgb[0] & 0xff;
                green = (rgb[0] & 0xff00) >> 8;
                red = (rgb[0] & 0xff0000) >> 16;


                // Take the average value of all three colors as effective brightness of the pixel
                // Cast the arguments as floats

                brightness = ((float) blue + (float) green + (float) red)/3;

                // Now fill the plot array with the average values, normalized to the range 0 to 1.

                xyPlot[i][j] = brightness/255.0;


            }
        }
        */
        // Print the brightness values to the plot file

        //Path filePath = Path.of("brightness.dat");

        /* try(FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            fileWriter.write("#  x    y    RBG brightness");
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    fileWriter.write(i + "  " + j + "  " + xyPlot[i][j] + "\n");
                }
            }

        }*/

        // Show the image on the screen within JFrame

        JFrame jFrame = new JFrame("Extract 1D curve from image");

        jFrame.setLayout(new FlowLayout());


        jFrame.setSize(500,500);
        //jFrame.setLayout(null);
        JLabel jLabel = new JLabel();

        jFrame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);



        JButton button=new JButton("Select picture file");

        // Add a button for determining the file in which the picture is located that shall be analyzed.

        jFrame.add(button);
        button.setLocation(1000,5);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog((Frame)null, "Select picture file for analyzing");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);
                filename[0] = dialog.getDirectory() + dialog.getFile();

                System.out.println("filename" + filename[0]);
                try {
                    img[0] = ImageIO.read(new File(filename[0]));
                    img1[0] = img[0];

                } catch (IOException e3) {
                    throw new RuntimeException(e3);
                }

                // Height and width of the image (in pixels)
                height[0] = img[0].getHeight();
                width[0] = img[0].getWidth();
                jFrame.setSize(width[0],height[0]+200);

                ImageIcon imageIcon = new ImageIcon(img[0]);
                jLabel.setIcon(imageIcon);
                jFrame.remove(jLabel);
                jFrame.add(jLabel);
                jLabel.setLocation(0,100);

            }
        });

        JButton buttonSave = new JButton("Save modified picture and plot");
        jFrame.add(buttonSave);
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog((Frame)null, "Select picture file for analyzing");
                dialog.setMode(FileDialog.SAVE);
                dialog.setVisible(true);
                String outputName = dialog.getDirectory() + dialog.getFile();
                try {
                    ImageIO.write(img[0], "png", new File(outputName));

                    String message = "The 1D plot data of the shown curve was written to " + dialog.getDirectory() +
                            "1D_plot.dat.";
                    JOptionPane.showMessageDialog(null, message);
                } catch (IOException e4) {
                    throw new RuntimeException(e4);
                }
                Path filePath = Path.of(dialog.getDirectory() + "1D_plot.dat");
                try(FileWriter fileWriter = new FileWriter(filePath.toFile())) {
                    fileWriter.write("# width: " + width[0] + " height: " + height[0] +
                            " Path len: " + pathPixNum[0] + "\n");
                    for (int i = 0; i < pathPixNum[0]; i++) {
                        fileWriter.write(i + " " + plotLine[i] + " \n");
                    }
                } catch (IOException io) {
                    System.out.println("The file 1D_brightness.dat could not be written!");
                    io.printStackTrace();
                }
            }

        });


        // Add a text area describing the purpose and handling of the program

        JTextArea textArea = new JTextArea("This program extracts the brightness of a picture along a 1D path \n" +
                "that is given by the user. Simply click at two points on the shown picture and a path between \n" +
                "them will be calculated.");

        jFrame.add(textArea);
        textArea.setLocation(20,5);

        // Add a file dialogue for opening the picture to be analyzed

        jFrame.setVisible(true);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final int[] clickNumber = {0};

        final int[] xStart = {0};
        final int[] yStart = {0};
        final int[] xEnd = {0};
        final int[] yEnd = {0};



        // Extract a single 1D path with endpoints given by the user
        // for each width pixel on the way, extract the nearest height pixel value and
        // store them into a 1D array

        // Determine the position of the mouse relative to the main GUI window (where the picture is)

        int finalLineWidth = lineWidth;

        jFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


                int xClick = e.getX();
                int yClick = e.getY();
                clickNumber[0]++;


                //&System.out.println(xpos + " , " + ypos);

                System.out.println(xClick + " " + yClick);

                if (clickNumber[0] == 1) {
                    xStart[0] = xClick;
                    yStart[0] = yClick - TOP_MARGIN;
                } else if (clickNumber[0] == 2) {

                    try {
                         img[0] = ImageIO.read(new File(filename[0]));

                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                    ImageIcon imageIcon = new ImageIcon(img[0]);
                    jLabel.setIcon(imageIcon);
                    jFrame.remove(jLabel);
                    jFrame.add(jLabel);
                    jLabel.setLocation(0,100);

                    xEnd[0] = xClick;
                    yEnd[0] = yClick - TOP_MARGIN;

                    int xLen = xEnd[0] - xStart[0];
                    int yLen = yEnd[0] - yStart[0];

                    // Determine path length in pixels for subsequent determination of pixels to change
                    double pathLen = Math.sqrt(Math.pow(xEnd[0] - xStart[0], 2) + Math.pow(yEnd[0] - yStart[0], 2));
                    pathPixNum[0] = (int) pathLen;

                    System.out.println(pathLen);
                    // Calculate equation of line in order to determine the nearest pixels after it

                    double slope = ((float) (yEnd[0] - yStart[0])) / ((float) xEnd[0] - xStart[0]);

                    // Manipulate nearest pixels along the drawn path!
                    int xPos = 0;
                    int yPos = 0;

                    double xAct = 0.0;
                    double yAct = 0.0;

                    // Extract the pixel information along the desired path

                    double deltaX = ((double) (xEnd[0]-xStart[0]) )/(double) pathPixNum[0];
                    double deltaY = deltaX * slope;

                    int red;
                    int green;
                    int blue;
                    double brightness;



                        for (int i = 0; i < pathPixNum[0]; i++) {
                            xAct = xStart[0] + deltaX * i;
                            yAct = yStart[0] + deltaY * i;
                            xPos = (int) Math.round(xAct);
                            yPos = (int) Math.round(yAct);
                            rgb[0] = img[0].getRGB(xPos, yPos);


                            // Get the components of the single colors by dividing up the RGB integer
                            blue = rgb[0] & 0xff;
                            green = (rgb[0] & 0xff00) >> 8;
                            red = (rgb[0] & 0xff0000) >> 16;


                            // Take the average value of all three colors as effective brightness of the pixel
                            // Cast the arguments as floats

                            brightness = ((float) blue + (float) green + (float) red) / 3;
                            plotLine[i] = brightness;

                        }




                    // If the slope is larger than 45 %, loop over the y instead of x values!

                    double[] pixelPos = new double[Math.max(width[0],height[0])];
                    if (slope > 0.5) {
                        for (int i = 0; i < yLen; i++) {
                            yPos = yStart[0] + i;

                            xPos = (int) (xStart[0] + i/slope);

                            // Extract gray scale of the current
                            //System.out.println(finalLineWidth);
                            //System.out.println("act " + xPos + " " + yPos);


                            // In order to broaden the line, also manipulate the pixels within a range of 10 around each point!
                            //img.setRGB(xPos, yPos, 0);
                            int xPaint = 0;
                            int yPaint = 0;
                            for (int j = -finalLineWidth; j < finalLineWidth; j++) {
                                yPaint = yPos + j;
                                if (yPaint > SHOW_MARGIN && yPaint < height[0]) {
                                    for (int k = -finalLineWidth; k < finalLineWidth; k++) {
                                        xPaint = xPos + k;
                                        if (xPaint > SHOW_MARGIN && xPaint + k < width[0]) {
                                            img[0].setRGB(xPaint, yPaint, Color.RED.getRGB());


                                        }
                                    }
                                }
                            }
                            //System.out.println(img.getRGB(xPos,yPos));
                        }
                    } else {
                        for (int i = 0; i < xLen; i++) {
                            xPos = xStart[0] + i;

                            yPos = (int) (yStart[0] + i * slope);

                            // Extract gray scale of the current
                            //System.out.println(finalLineWidth);
                            //System.out.println("act " + xPos + " " + yPos);

                            // In order to broaden the line, also manipulate the pixels within a range of 10 around each point!
                            //img.setRGB(xPos, yPos, 0);
                            int xPaint = 0;
                            int yPaint = 0;
                            for (int j = -finalLineWidth; j < finalLineWidth; j++) {
                                xPaint = xPos + j;
                                if (xPaint > SHOW_MARGIN && xPaint < width[0]) {
                                    for (int k = -finalLineWidth; k < finalLineWidth; k++) {
                                        yPaint = yPos + k;
                                        if (yPaint > SHOW_MARGIN && yPaint + k < height[0]) {
                                            img[0].setRGB(xPaint, yPaint, Color.RED.getRGB());


                                        }
                                    }
                                }
                            }
                            //System.out.println(img.getRGB(xPos,yPos));
                        }
                    }
                    // Overwrite the currently shown image
                    ImageIcon imageIcon2 = new ImageIcon(img[0]);
                    jLabel.setIcon(imageIcon2);
                    jFrame.remove(jLabel);
                    jFrame.add(jLabel);
                    jLabel.setLocation(0,100);
                    clickNumber[0] = 0;

                    // Save the current image with marker line to file

                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        System.out.println("test44");








        // Determine Eucledian distance og
    }
}
