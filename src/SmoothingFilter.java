import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class SmoothingFilter extends Frame implements ActionListener {
	BufferedImage input;
	ImageCanvas source, target;
	TextField texSigma;
	int width, height;
	// Constructor
	public SmoothingFilter(String name) {
		super("Smoothing Filters");
		// load image
		try {
			input = ImageIO.read(new File(name));
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
				Get intensity values of each neighbouring pixel of target pixel
				Put each R, G, B value in it's own array
				Sort array, get middle (median) value
				Set target pixel intensity as this median value
			*/

            Color[] pixel = new Color[9];

            int[] R = new int[9];
            int[] B = new int[9];
            int[] G = new int[9];

            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {

                    // Get each neighbouring pixel intensity
                    pixel[0] = new Color(input.getRGB(x - 1, y - 1));
                    pixel[1] = new Color(input.getRGB(x - 1, y));
                    pixel[2] = new Color(input.getRGB(x - 1, y + 1));
                    pixel[3] = new Color(input.getRGB(x, y + 1));
                    pixel[4] = new Color(input.getRGB(x + 1, y + 1));
                    pixel[5] = new Color(input.getRGB(x + 1, y));
                    pixel[6] = new Color(input.getRGB(x + 1, x - 1));
                    pixel[7] = new Color(input.getRGB(x, y - 1));
                    pixel[8] = new Color(input.getRGB(x, y));

                    // Store each intensity in array
                    for (int k = 0; k < 9; k++) {
                        R[k] = pixel[k].getRed();
                        B[k] = pixel[k].getBlue();
                        G[k] = pixel[k].getGreen();
                    }

                    // Sort each array
                    Arrays.sort(R);
                    Arrays.sort(G);
                    Arrays.sort(B);

                    // Since we are dealing with the neighbouring 8 pixels, we want the 4th index from the sorted array
                    int p = (R[4] << 16) | G[4] << 8 | B[4];
                    input.setRGB(x, y, p);

                }
            }
            target.resetImage(input);
        }
	}

	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}
}
