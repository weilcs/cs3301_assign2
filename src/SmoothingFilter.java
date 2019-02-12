/*
    Defines the SmoothingFilter. Implements the median, mean, Kuwahara, and gaussian filter.

    Bugs: Clicking buttons multiple times, doesn't stack the filter

    Submitted by:
    Freddie Taylor Pike - 201252723
    Justin Delaney - 201222684
    Wei Liu - 201759784
 */

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

  private int checkIfXCoordinateIsValid(int x, int oldX) {
    if ((x > width - 1) || (x < 0)) {
      return oldX;
    }
    return x;
  }

  private int checkIfYCoordinateIsValid(int y, int oldY) {
    if ((y > height - 1) || (y < 0)) {
      return oldY;
    }
    return y;
  }

  private Color[] generateBaseMatrix(int y, int x) {
      Color[] subMatrix = new Color[25];
      subMatrix[0] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+2, y)));
      subMatrix[1] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+1, y)));
      subMatrix[2] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y, y)));
      subMatrix[3] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-1, y)));
      subMatrix[4] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-2, y)));

      subMatrix[5] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+2, y)));
      subMatrix[6] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+1, y)));
      subMatrix[7] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y, y)));
      subMatrix[8] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-1, y)));
      subMatrix[9] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-2, y)));

      subMatrix[10] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+2, y)));
      subMatrix[11] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+1, y)));
      subMatrix[12] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y, y)));
      subMatrix[13] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-1, y)));
      subMatrix[14] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-2, y)));

      subMatrix[15] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+2, y)));
      subMatrix[16] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+1, y)));
      subMatrix[17] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y, y)));
      subMatrix[18] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-1, y)));
      subMatrix[19] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-2, y)));

      subMatrix[20] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+2, y)));
      subMatrix[21] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+1, y)));
      subMatrix[22] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y, y)));
      subMatrix[23] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-1, y)));
      subMatrix[24] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-2, y)));

      return subMatrix;
  }

  private Color[] generateKuwaharaRegion1(int y, int x) {
    Color[] subMatrix = new Color[25];

    subMatrix[0] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y, y)));
    subMatrix[1] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y, y)));
    subMatrix[2] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y, y)));
    subMatrix[3] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y, y)));
    subMatrix[4] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y, y)));

    subMatrix[5] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+1, y)));
    subMatrix[6] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+2, y)));
    subMatrix[7] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+3, y)));
    subMatrix[8] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+4, y)));


    subMatrix[9] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+1, y)));
    subMatrix[10] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+2, y)));
    subMatrix[11] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+3, y)));
    subMatrix[12] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y+4, y)));


    subMatrix[13] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+1, y)));
    subMatrix[14] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+2, y)));
    subMatrix[15] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+3, y)));
    subMatrix[16] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y+4, y)));


    subMatrix[17] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y+1, y)));
    subMatrix[18] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y+2, y)));
    subMatrix[19] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y+3, y)));
    subMatrix[20] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y+4, y)));

    subMatrix[21] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y+1, y)));
    subMatrix[22] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y+2, y)));
    subMatrix[23] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y+3, y)));
    subMatrix[24] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y+4, y)));

    return subMatrix;
}

private Color[] generateKuwaharaRegion2(int y, int x) {
  Color[] subMatrix = new Color[25];

  subMatrix[0] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[1] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[2] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[3] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[4] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y, y)));

  subMatrix[5] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+1, y)));
  subMatrix[6] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+2, y)));
  subMatrix[7] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+3, y)));
  subMatrix[8] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y+4, y)));

	subMatrix[9] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+1, y)));
  subMatrix[10] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+2, y)));
  subMatrix[11] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+3, y)));
  subMatrix[12] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y+4, y)));

	subMatrix[13] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+1, y)));
  subMatrix[14] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+2, y)));
  subMatrix[15] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+3, y)));
  subMatrix[16] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y+4, y)));

	subMatrix[17] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y+1, y)));
  subMatrix[18] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y+2, y)));
  subMatrix[19] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y+3, y)));
  subMatrix[20] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y+4, y)));

	subMatrix[21] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y+1, y)));
  subMatrix[22] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y+2, y)));
  subMatrix[23] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y+3, y)));
  subMatrix[24] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y+4, y)));



  return subMatrix;
}

private Color[] generateKuwaharaRegion3(int y, int x) {
  Color[] subMatrix = new Color[25];


	subMatrix[0] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[1] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[2] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[3] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[4] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-4, y)));

  subMatrix[5] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[6] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[7] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[8] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[9] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[10] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[11] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[12] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[13] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[14] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[15] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[16] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[17] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[18] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[19] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[20] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[21] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-1, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[22] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-2, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[23] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-3, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[24] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x-4, x), checkIfYCoordinateIsValid(y, y)));

  return subMatrix;
}

private Color[] generateKuwaharaRegion4(int y, int x) {
  Color[] subMatrix = new Color[25];

  subMatrix[0] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[1] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[2] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[3] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[4] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x, x), checkIfYCoordinateIsValid(y-4, y)));

  subMatrix[5] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[6] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[7] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y, y)));
  subMatrix[8] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y, y)));

  subMatrix[9] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[10] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[11] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[12] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+1, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[13] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[14] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[15] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[16] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+2, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[17] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[18] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[19] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[20] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+3, x), checkIfYCoordinateIsValid(y-4, y)));

	subMatrix[21] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y-1, y)));
  subMatrix[22] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y-2, y)));
  subMatrix[23] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y-3, y)));
  subMatrix[24] = new Color(unmodifiedInput.getRGB(checkIfXCoordinateIsValid(x+4, x), checkIfYCoordinateIsValid(y-4, y)));

  return subMatrix;
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
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get each neighbouring pixel intensity
					          pixel = generateBaseMatrix(y, x);

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
                    int p = (R[R.length / 2] << 16) | G[G.length / 2] << 8 | B[B.length / 2];
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
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					// Not sure if there's a nicer way to do this?
					pixel = generateBaseMatrix(y, x);

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

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
          region_1 = generateKuwaharaRegion1(y, x);
          region_2 = generateKuwaharaRegion2(y, x);
          region_3 = generateKuwaharaRegion3(y, x);
          region_4 = generateKuwaharaRegion4(y, x);

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
					mean2 /= region_2.length;
					mean3 /= region_3.length;
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

		if ( ((Button)e.getSource()).getLabel().equals("5x5 Gaussian")) {
			int[][] kernelRed = new int[5][5];
			int[][] kernelGreen = new int[5][5];
			int[][] kernelBlue = new int[5][5];
			Color[][] clrKernel = new Color[5][5];
			int[] xDirection = new int[5];
			int[] yDirection = new int[5];
			int red, green, blue;
			for(int y = 0; y < height; y++)
				for (int x = 0; x < width; x++){
					for (int i = 0; i < 5; i++){
						xDirection[i] = x - 2 + i;
						yDirection[i] = y - 2 + i;
						xDirection[i] = xDirection[i] < 0 ? 0 : (xDirection[i] >= width ? width - 1 : xDirection[i]);
						yDirection[i] = yDirection[i] < 0 ? 0 : (yDirection[i] >= height ? height - 1 : yDirection[i]);
					}

					for(int n=0;n<5;n++ ){
						for(int m=0;m<5;m++){
							clrKernel[m][n]= new Color(source.image.getRGB(xDirection[m], yDirection[n]));
							kernelRed[m][n] = clrKernel[m][n].getRed();
							kernelGreen[m][n] = clrKernel[m][n].getGreen();
							kernelBlue[m][n] = clrKernel[m][n].getBlue();
						}
					}

					red = gaussianFilter(kernelRed);
					green = gaussianFilter(kernelGreen);
					blue = gaussianFilter(kernelBlue);
					red = red < 0 ? 0 : red > 255 ? 255 : red;
					green = green < 0 ? 0 : green > 255 ? 255 : green;
					blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
					int p = (red << 16) | green << 8 | blue;
					input.setRGB(x, y, p);

				}
			target.resetImage(input);

		}
	}

	public static float[][] makeGaussianKernel(int rows, int cols, float sigma){
		int r = (rows-1)/2;
		//float r2 = r*r;
		float[][] matrix = new float[rows][cols];
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = 2*(float)Math.PI*sigma;
		float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
		float total = 0;

		for (int col = -r; col <= r; col++)
			for (int row = -r; row <= r; row++) {
				float distance = row*row;

				matrix[row+2][col+2] = (float)Math.exp(-(distance)/sigma22)/sqrtSigmaPi2;
				total += matrix[row+2][col+2];
		}

		for (int n = 0; n < cols; n++)
		for (int m = 0; m < rows; m++){
			matrix[m][n] /= total;
		}
		return matrix;
	}

	public int gaussianFilter(int[][] kernel){
		int center = 0;
		float sigma = Float.valueOf(texSigma.getText());
		float[][] GaussianKernel = makeGaussianKernel(5,5, sigma);
		for (int y = 0; y < 5; y++)
		for (int x = 0; x < 5; x++)
			center += kernel[x][y] * GaussianKernel[x][y];
		return center;
	}


	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}
}
