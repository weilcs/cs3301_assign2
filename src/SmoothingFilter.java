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
			Calculate mean and variance of each region
			Use mean of region with the smallest variance
			 */

			// Arrays to hold pixel intensities of each region needed for Kuwahara filter
			Color[] region_1 = new Color[25];
			Color[] region_2 = new Color[25];
			Color[] region_3 = new Color[25];
			Color[] region_4 = new Color[25];


			int[] R = new int[25];
			int[] B = new int[25];
			int[] G = new int[25];

			for (int y = 4; y < height - 4; y++) {
				for (int x = 4; x < width - 4; x++) {

				    Color rgb = new Color(input.getRGB(x, y));
				    int r = rgb.getRed();
				    int b = rgb.getBlue();
				    int g = rgb.getGreen();

				    float[] hsb = new float[3];

				    rgb.RGBtoHSB(r, g, b, hsb);

				    
				}

			}
		}
	}



	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}
}
