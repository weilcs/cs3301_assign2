import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.stream.*; // Newly added library.

// Main class
public class SmoothingFilter extends Frame implements ActionListener {
	BufferedImage input;
	BufferedImage unmodifiedInput;
	ImageCanvas source, target;
	TextField texSigma;
	int width, height;
	// Constructor
	public SmoothingFilter(String name) {
		super("Smoothing Filters");
		// load image
		try {
			input = ImageIO.read(new File(name));
			unmodifiedInput = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Add noise");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 mean");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Sigma:"));
		texSigma = new TextField("1", 1);
		controls.add(texSigma);
		button = new Button("5x5 Gaussian");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 median");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 Kuwahara");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+100, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- add random noise
		if ( ((Button)e.getSource()).getLabel().equals("Add noise") ) {
			Random rand = new Random();
			int dev = 64;
			for ( int y=0, i=0 ; y<height ; y++ )
				for ( int x=0 ; x<width ; x++, i++ ) {
					Color clr = new Color(source.image.getRGB(x, y));
					int red = clr.getRed() + (int)(rand.nextGaussian() * dev);
					int green = clr.getGreen() + (int)(rand.nextGaussian() * dev);
					int blue = clr.getBlue() + (int)(rand.nextGaussian() * dev);
					red = red < 0 ? 0 : red > 255 ? 255 : red;
					green = green < 0 ? 0 : green > 255 ? 255 : green;
					blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
					source.image.setRGB(x, y, (new Color(red, green, blue)).getRGB());
					unmodifiedInput.setRGB(x, y, (new Color(red, green, blue)).getRGB());
				}
			source.repaint();
		}
		if ( ((Button)e.getSource()).getLabel().equals("5x5 median")) {

			/*
				5x5 median filter:
				Get intensity values of each (24) neighbouring pixels of target pixel
				Put each R, G, B value in it's own array
				Sort array, get middle (median) value
				Set target pixel intensity as this median value
			*/

            Color[] pixel = new Color[25];

            int[] R = new int[25];
            int[] B = new int[25];
            int[] G = new int[25];

			// y = 3 and x / y < height / width < 3 to stay within border of image
            for (int y = 3; y < height - 3; y++) {
                for (int x = 3; x < width - 3; x++) {


                    // Get each neighbouring pixel intensity
					// Not sure if there's a nicer way to do this?
                    pixel[0] = new Color(unmodifiedInput.getRGB(x, y));
                    pixel[1] = new Color(unmodifiedInput.getRGB(x, y + 1));
                    pixel[2] = new Color(unmodifiedInput.getRGB(x, y - 1));
                    pixel[3] = new Color(unmodifiedInput.getRGB(x, y - 2));
                    pixel[4] = new Color(unmodifiedInput.getRGB(x, y - 3));
                    pixel[5] = new Color(unmodifiedInput.getRGB(x + 1, y));
                    pixel[6] = new Color(unmodifiedInput.getRGB(x + 2, y));
                    pixel[7] = new Color(unmodifiedInput.getRGB(x - 1 , y));
                    pixel[8] = new Color(unmodifiedInput.getRGB(x - 2, y));
					pixel[9] = new Color(unmodifiedInput.getRGB(x + 1, y + 1));
				    pixel[10] = new Color(unmodifiedInput.getRGB(x + 2, y + 1));
					pixel[11] = new Color(unmodifiedInput.getRGB(x - 1, y + 1));
					pixel[12] = new Color(unmodifiedInput.getRGB(x - 2, y + 1));
					pixel[13] = new Color(unmodifiedInput.getRGB(x + 1, y - 1));
					pixel[14] = new Color(unmodifiedInput.getRGB(x + 2, y - 1));
					pixel[15] = new Color(unmodifiedInput.getRGB(x + 2, y - 2));
					pixel[16] = new Color(unmodifiedInput.getRGB(x + 2, y - 2));
					pixel[17] = new Color(unmodifiedInput.getRGB(x + 1, y - 3));
					pixel[18] = new Color(unmodifiedInput.getRGB(x  + 2, y - 3));
					pixel[19] = new Color(unmodifiedInput.getRGB(x - 1, y - 1));
					pixel[20] = new Color(unmodifiedInput.getRGB(x - 1, y - 2));
					pixel[21] = new Color(unmodifiedInput.getRGB(x - 1, y - 3));
					pixel[22] = new Color(unmodifiedInput.getRGB(x - 2, y - 1));
					pixel[23] = new Color(unmodifiedInput.getRGB(x - 2, y - 2));
					pixel[24] = new Color(unmodifiedInput.getRGB(x - 2, y - 3));

                    // Store each intensity in array
                    for (int k = 0; k < 25; k++) {
                        R[k] = pixel[k].getRed();
                        B[k] = pixel[k].getBlue();
                        G[k] = pixel[k].getGreen();
                    }

                    // Sort each array
                    Arrays.sort(R);
                    Arrays.sort(G);
                    Arrays.sort(B);

                    // Since we are dealing with the neighbouring 24 pixels, we want the 16th index from the sorted array
                    int p = (R[16] << 16) | G[16] << 8 | B[16];
                    input.setRGB(x, y, p);

                }
            }
            target.resetImage(input);
        }

		if ( ((Button)e.getSource()).getLabel().equals("5x5 mean")) {
			/*
				Get intensity values of each neighbouring pixel of target pixel
				Put each R, G, B value in it's own array
				Calculate the mean of each RGB array.
				Set target pixel intensity as the mean value.
			*/

			Color[] pixel = new Color[25];

			int[] R = new int[25];
			int[] B = new int[25];
			int[] G = new int[25];

			// y = 3 and x / y < height / width < 3 to stay within border of image
			for (int y = 3; y < height - 3; y++) {
				for (int x = 3; x < width - 3; x++) {

					// Not sure if there's a nicer way to do this?
					pixel[0] = new Color(unmodifiedInput.getRGB(x, y));
					pixel[1] = new Color(unmodifiedInput.getRGB(x, y + 1));
					pixel[2] = new Color(unmodifiedInput.getRGB(x, y - 1));
					pixel[3] = new Color(unmodifiedInput.getRGB(x, y - 2));
					pixel[4] = new Color(unmodifiedInput.getRGB(x, y - 3));
					pixel[5] = new Color(unmodifiedInput.getRGB(x + 1, y));
					pixel[6] = new Color(unmodifiedInput.getRGB(x + 2, y));
					pixel[7] = new Color(unmodifiedInput.getRGB(x - 1 , y));
					pixel[8] = new Color(unmodifiedInput.getRGB(x - 2, y));
					pixel[9] = new Color(unmodifiedInput.getRGB(x + 1, y + 1));
					pixel[10] = new Color(unmodifiedInput.getRGB(x + 2, y + 1));
					pixel[11] = new Color(unmodifiedInput.getRGB(x - 1, y + 1));
					pixel[12] = new Color(unmodifiedInput.getRGB(x - 2, y + 1));
					pixel[13] = new Color(unmodifiedInput.getRGB(x + 1, y - 1));
					pixel[14] = new Color(unmodifiedInput.getRGB(x + 2, y - 1));
					pixel[15] = new Color(unmodifiedInput.getRGB(x + 2, y - 2));
					pixel[16] = new Color(unmodifiedInput.getRGB(x + 2, y - 2));
					pixel[17] = new Color(unmodifiedInput.getRGB(x + 1, y - 3));
					pixel[18] = new Color(unmodifiedInput.getRGB(x  + 2, y - 3));
					pixel[19] = new Color(unmodifiedInput.getRGB(x - 1, y - 1));
					pixel[20] = new Color(unmodifiedInput.getRGB(x - 1, y - 2));
					pixel[21] = new Color(unmodifiedInput.getRGB(x - 1, y - 3));
					pixel[22] = new Color(unmodifiedInput.getRGB(x - 2, y - 1));
					pixel[23] = new Color(unmodifiedInput.getRGB(x - 2, y - 2));
					pixel[24] = new Color(unmodifiedInput.getRGB(x - 2, y - 3));

					// Store each intensity in array
					for (int k = 0; k < 25; k++) {
						R[k] = pixel[k].getRed();
						B[k] = pixel[k].getBlue();
						G[k] = pixel[k].getGreen();
					}

					// Calculating the mean of each RGB array and applying it to the image.
					int rMean = (int) (IntStream.of(R).sum() / R.length);
					int gMean = (int) (IntStream.of(G).sum() / G.length);
					int bMean = (int) (IntStream.of(B).sum() / B.length);
					int p = (rMean << 16) | gMean << 8 | bMean;
					input.setRGB(x, y, p);

				}
			}
			target.resetImage(input);
		}

		if ( ((Button)e.getSource()).getLabel().equals("5x5 Kuwahara")) {
			/*
			Define 4 sub-regions within neighbourhood of target pixel
			Convert each region to HSB
			Calculate mean and variance of each region
			Use mean of region with the smallest variance as the new brightness channel
			Convert back to RGB
			Set values
			 */

			// Arrays to hold pixel intensities of each region needed for Kuwahara filter
			Color[] region_1 = new Color[25];
			Color[] region_2 = new Color[25];
			Color[] region_3 = new Color[25];
			Color[] region_4 = new Color[25];

			for (int y = 4; y < height - 4; y++) {
				for (int x = 4; x < width - 4; x++) {

					// Region 1
					region_1[0] = new Color(unmodifiedInput.getRGB(x, y));
					region_1[1] = new Color(unmodifiedInput.getRGB(x - 1, y));
					region_1[2] = new Color(unmodifiedInput.getRGB(x - 2, y));
					region_1[3] = new Color(unmodifiedInput.getRGB(x - 3, y));
					region_1[4] = new Color(unmodifiedInput.getRGB(x - 4, y));

					region_1[5] = new Color(unmodifiedInput.getRGB(x, y + 1));
					region_1[6] = new Color(unmodifiedInput.getRGB(x, y + 2));
					region_1[7] = new Color(unmodifiedInput.getRGB(x, y + 3));
					region_1[8] = new Color(unmodifiedInput.getRGB(x, y + 4));

					region_1[9] = new Color(unmodifiedInput.getRGB(x - 1, y + 1));
					region_1[10] = new Color(unmodifiedInput.getRGB(x - 1, y + 2));
					region_1[11] = new Color(unmodifiedInput.getRGB(x - 1, y + 3));
					region_1[12] = new Color(unmodifiedInput.getRGB(x - 1, y + 4));

					region_1[13] = new Color(unmodifiedInput.getRGB(x - 2, y + 1));
					region_1[14] = new Color(unmodifiedInput.getRGB(x - 2, y + 2));
					region_1[15] = new Color(unmodifiedInput.getRGB(x - 2, y + 3));
					region_1[16] = new Color(unmodifiedInput.getRGB(x - 2, y + 4));

					region_1[17] = new Color(unmodifiedInput.getRGB(x - 3, y + 1));
					region_1[18] = new Color(unmodifiedInput.getRGB(x - 3, y + 2));
					region_1[19] = new Color(unmodifiedInput.getRGB(x - 3, y + 3));
					region_1[20] = new Color(unmodifiedInput.getRGB(x - 3, y + 4));

					region_1[21] = new Color(unmodifiedInput.getRGB(x - 4, y + 1));
					region_1[22] = new Color(unmodifiedInput.getRGB(x - 4, y + 2));
					region_1[23] = new Color(unmodifiedInput.getRGB(x - 4, y + 3));
					region_1[24] = new Color(unmodifiedInput.getRGB(x - 4, y + 4));


					//Region 2
					region_2[0] = new Color(unmodifiedInput.getRGB(x, y));
					region_2[1] = new Color(unmodifiedInput.getRGB(x + 1, y));
					region_2[2] = new Color(unmodifiedInput.getRGB(x + 2, y));
					region_2[3] = new Color(unmodifiedInput.getRGB(x + 3, y));
					region_2[4] = new Color(unmodifiedInput.getRGB(x + 4, y));

					region_2[5] = new Color(unmodifiedInput.getRGB(x, y + 1));
					region_2[6] = new Color(unmodifiedInput.getRGB(x, y + 2));
					region_2[7] = new Color(unmodifiedInput.getRGB(x, y + 3));
					region_2[8] = new Color(unmodifiedInput.getRGB(x, y + 4));

					region_2[9] = new Color(unmodifiedInput.getRGB(x + 1, y + 1));
					region_2[10] = new Color(unmodifiedInput.getRGB(x + 1, y + 2));
					region_2[11] = new Color(unmodifiedInput.getRGB(x + 1, y + 3));
					region_2[12] = new Color(unmodifiedInput.getRGB(x + 1, y + 4));

					region_2[13] = new Color(unmodifiedInput.getRGB(x + 2, y + 1));
					region_2[14] = new Color(unmodifiedInput.getRGB(x + 2, y + 2));
					region_2[15] = new Color(unmodifiedInput.getRGB(x + 2, y + 3));
					region_2[16] = new Color(unmodifiedInput.getRGB(x + 2, y + 4));

					region_2[17] = new Color(unmodifiedInput.getRGB(x + 3, y + 1));
					region_2[18] = new Color(unmodifiedInput.getRGB(x + 3, y + 2));
					region_2[19] = new Color(unmodifiedInput.getRGB(x + 3, y + 3));
					region_2[20] = new Color(unmodifiedInput.getRGB(x + 3, y + 4));

					region_2[21] = new Color(unmodifiedInput.getRGB(x + 4, y + 1));
					region_2[22] = new Color(unmodifiedInput.getRGB(x + 4, y + 2));
					region_2[23] = new Color(unmodifiedInput.getRGB(x + 4, y + 3));
					region_2[24] = new Color(unmodifiedInput.getRGB(x + 4, y + 4));

					// Region 3

					region_3[0] = new Color(unmodifiedInput.getRGB(x, y));
					region_3[1] = new Color(unmodifiedInput.getRGB(x, y - 1));
					region_3[2] = new Color(unmodifiedInput.getRGB(x, y - 2));
					region_3[3] = new Color(unmodifiedInput.getRGB(x, y - 3));
					region_3[4] = new Color(unmodifiedInput.getRGB(x, y - 4));

					region_3[5] = new Color(unmodifiedInput.getRGB(x - 1, y - 1));
					region_3[6] = new Color(unmodifiedInput.getRGB(x - 1, y - 2));
					region_3[7] = new Color(unmodifiedInput.getRGB(x - 1, y - 3));
					region_3[8] = new Color(unmodifiedInput.getRGB(x - 1, y - 4));

					region_3[9] = new Color(unmodifiedInput.getRGB(x - 2, y - 1));
					region_3[10] = new Color(unmodifiedInput.getRGB(x - 2, y - 2));
					region_3[11] = new Color(unmodifiedInput.getRGB(x - 2, y - 3));
					region_3[12] = new Color(unmodifiedInput.getRGB(x - 2, y - 4));

					region_3[13] = new Color(unmodifiedInput.getRGB(x - 3, y - 1));
					region_3[14] = new Color(unmodifiedInput.getRGB(x - 3, y - 2));
					region_3[15] = new Color(unmodifiedInput.getRGB(x - 3, y - 3));
					region_3[16] = new Color(unmodifiedInput.getRGB(x - 3, y - 4));

					region_3[17] = new Color(unmodifiedInput.getRGB(x - 4, y - 1));
					region_3[18] = new Color(unmodifiedInput.getRGB(x - 4, y - 2));
					region_3[19] = new Color(unmodifiedInput.getRGB(x - 4, y - 3));
					region_3[20] = new Color(unmodifiedInput.getRGB(x - 4, y - 4));

					region_3[21] = new Color(unmodifiedInput.getRGB(x - 1, y));
					region_3[22] = new Color(unmodifiedInput.getRGB(x - 2, y));
					region_3[23] = new Color(unmodifiedInput.getRGB(x - 3, y));
					region_3[24] = new Color(unmodifiedInput.getRGB(x - 4, y));

					// Region 4

					region_4[0] = new Color(unmodifiedInput.getRGB(x, y));
					region_4[1] = new Color(unmodifiedInput.getRGB(x, y - 1));
					region_4[2] = new Color(unmodifiedInput.getRGB(x, y - 2));
					region_4[3] = new Color(unmodifiedInput.getRGB(x, y - 3));
					region_4[4] = new Color(unmodifiedInput.getRGB(x, y - 4));

					region_4[5] = new Color(unmodifiedInput.getRGB(x + 1, y));
					region_4[6] = new Color(unmodifiedInput.getRGB(x + 2, y));
					region_4[7] = new Color(unmodifiedInput.getRGB(x + 3, y));
					region_4[8] = new Color(unmodifiedInput.getRGB(x + 4, y));

					region_4[9] = new Color(unmodifiedInput.getRGB(x + 1, y - 1));
					region_4[10] = new Color(unmodifiedInput.getRGB(x + 1, y - 2));
					region_4[11] = new Color(unmodifiedInput.getRGB(x + 1, y - 3));
					region_4[12] = new Color(unmodifiedInput.getRGB(x + 1, y - 4));

					region_4[13] = new Color(unmodifiedInput.getRGB(x + 2, y - 1));
					region_4[14] = new Color(unmodifiedInput.getRGB(x + 2, y - 2));
					region_4[15] = new Color(unmodifiedInput.getRGB(x + 2, y - 3));
					region_4[16] = new Color(unmodifiedInput.getRGB(x + 2, y - 4));

					region_4[17] = new Color(unmodifiedInput.getRGB(x + 3, y - 1));
					region_4[18] = new Color(unmodifiedInput.getRGB(x + 3, y - 2));
					region_4[19] = new Color(unmodifiedInput.getRGB(x + 3, y - 3));
					region_4[20] = new Color(unmodifiedInput.getRGB(x + 3, y - 4));

					region_4[21] = new Color(unmodifiedInput.getRGB(x + 4, y - 1));
					region_4[22] = new Color(unmodifiedInput.getRGB(x + 4, y - 2));
					region_4[23] = new Color(unmodifiedInput.getRGB(x + 4, y - 3));
					region_4[24] = new Color(unmodifiedInput.getRGB(x + 4, y - 4));


					// Variables to store the mean of each region
					float mean1 = 0;
					float mean2 = 0;
					float mean3 = 0;
					float mean4 = 0;

					// Float array to store HSB values for each region

					float[] hsb1 = new float[3];
					float[] hsb2 = new float[3];
					float[] hsb3 = new float[3];
					float[] hsb4 = new float[3];

					int r, g, b;


					// Convert each region from RGB to HSB and calculate the mean
					for (int i = 0; i < 25; i++) {
						r = region_1[i].getRed();
						g = region_1[i].getGreen();
						b = region_1[i].getBlue();

						// Convert pixel from RGB to HSB
						hsb1 = region_1[i].RGBtoHSB(r, g, b, hsb1);

						// Add to mean
						mean1 += hsb1[2];

						r = region_2[i].getRed();
						g = region_2[i].getGreen();
						b = region_2[i].getBlue();

						hsb2 = region_2[i].RGBtoHSB(r, g, b, hsb2);
						mean2 += hsb2[2];

						r = region_3[i].getRed();
						g = region_3[i].getGreen();
						b = region_3[i].getBlue();

						hsb3 = region_3[i].RGBtoHSB(r, g, b, hsb3);
						mean3 += hsb3[2];

						r = region_4[i].getRed();
						g = region_4[i].getGreen();
						b = region_4[i].getBlue();

						hsb4 = region_4[i].RGBtoHSB(r, g, b, hsb4);
						mean4 += hsb4[2];
					}

					// Divide each value by the number of pixels to get actual mean
					mean1 /= region_1.length;
					//System.out.println(mean1);

					mean2 /= region_2.length;
					//System.out.println(mean2);

					mean3 /= region_3.length;
					//System.out.println(mean3);

					mean4 /= region_4.length;

					// Variables to store values for calculating each variance

					float temp1 = 0;
					float temp2 = 0;
					float temp3 = 0;
					float temp4 = 0;

					float var1 = 0;
					float var2 = 0;
					float var3 = 0;
					float var4 = 0;

					// Calculate variance of each region

					for (int i = 0; i < 25; i++) {
						temp1 += (hsb1[2] - mean1) * (hsb1[2] - mean1);
						var1 = temp1 / (region_1.length - 1);

						temp2 += (hsb2[2] - mean2) * (hsb2[2] - mean2);
						var2 = temp2 / (region_2.length - 1);

						temp3 += (hsb3[2] - mean3) * (hsb3[2] - mean3);
						var3 = temp3 / (region_3.length - 1);

						temp4 += (hsb4[2] - mean4) * (hsb4[2] - mean4);
						var4 = temp4 / (region_4.length - 1);
					}

					// Find which variance is the smallest

					float smallestVar = Math.min(var1, Math.min(var2, Math.min(var3, var4)));

					// Check to see which region has the smallest variance, then use that region's mean
					if (smallestVar == var1) {
								// Convert from HSB back to RGB now using the mean as the brightness value
								int rgb = Color.HSBtoRGB(hsb1[0], hsb1[1], mean1);
								Color newColor = new Color(rgb);

								int newR = newColor.getRed();
								int newG = newColor.getGreen();
								int newB = newColor.getBlue();

								int p = (newR << 16 | newG << 8 | newB);
								input.setRGB(x, y, p);

					}

					if (smallestVar == var2) {
								int rgb = Color.HSBtoRGB(hsb2[0], hsb2[1], mean2);
								Color newColor = new Color(rgb);

								int newR = newColor.getRed();
								int newG = newColor.getGreen();
								int newB = newColor.getBlue();

								int p = (newR << 16 | newG << 8 | newB);
								input.setRGB(x, y, p);

					}

					if (smallestVar == var3) {

								int rgb = Color.HSBtoRGB(hsb3[0], hsb3[1], mean3);
								Color newColor = new Color(rgb);

								int newR = newColor.getRed();
								int newG = newColor.getGreen();
								int newB = newColor.getBlue();

								int p = (newR << 16 | newG << 8 | newB);
								input.setRGB(x, y, p);
					}

					if (smallestVar == var4) {

								int rgb = Color.HSBtoRGB(hsb4[0], hsb4[1], mean4);
								Color newColor = new Color(rgb);

								int newR = newColor.getRed();
								int newG = newColor.getGreen();
								int newB = newColor.getBlue();

								int p = (newR << 16 | newG << 8 | newB);
								input.setRGB(x, y, p);
					}
				}
			}
			target.resetImage(input);
		}
	}



	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}
}
