import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.Random;
import javax.swing.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

public class videoController extends JFrame {

	static movingObject mainPolygon;

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	static String OS = System.getProperty("os.name");

	static long nextTime;
	static JFrame frame = new JFrame("MIDI Music");
	static JPanel all = new JPanel();
	static JPanel GUI = new JPanel();
	static int bgHue = 0;
	static int targetHue = 60;

	static final int RADIUS = 350;
	static final int MIN_SIDES = 3;
	static final int MAX_SIDES = 8;
	static final int REFRESHRATE = 50;
	static int side = 3;

	static boolean menu = true;
	// static JTextField seedBox = new JTextField("Enter seed", 20);
	static JButton button = new JButton("generate");
	static JLabel seedLabel = new JLabel("Seed: ");
	static JTextField seedText = new JTextField(20);

	static JLabel bpmLabel = new JLabel("BPM: ");
	static JTextField bpmText = new JTextField(20);

	static JLabel numLabel = new JLabel("Time Sign Numerator: ");
	static JTextField numText = new JTextField(10);

	static JLabel denLabel = new JLabel("Time Sign Denominator: ");
	static JTextField denText = new JTextField(10);

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (OS.contains("Windows")) {
					GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
					device.setFullScreenWindow(frame);
				}
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				all.setLayout(new OverlayLayout(all));

				mainPolygon = new movingObject(newPolygon.gon(side, RADIUS), Color.BLACK, screenSize.width / 2,
						screenSize.height / 2);
				mainPolygon.transformShape(newPolygon.gon(getNextSide(side), RADIUS));
				GUI.add(button);
				GUI.add(seedLabel);
				GUI.add(seedText);

				GUI.add(bpmLabel);
				GUI.add(bpmText);

				GUI.add(numLabel);
				GUI.add(numText);

				GUI.add(denLabel);
				GUI.add(denText);

				all.add(mainPolygon);
				all.add(GUI);
				frame.add(all);

				// if mac

				if (OS.contains("OS X")) {
					enableFullScreenMode(frame);
				}

				ActionListener taskPerformer = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						// Execute the main loop
						mainLoop();
					}
				};
				Timer timer = new Timer(REFRESHRATE, taskPerformer);
				timer.setRepeats(true);
				timer.start();
			}
		});
	}

	public static void mainLoop() {
		mainPolygon.tick();
		if (newPolygon.polyEquals(mainPolygon.p, mainPolygon.tp)) {
			mainPolygon.transformShape(newPolygon.gon(getNextSide(mainPolygon.p.realpts), RADIUS));
		}
		// check if generate is clicked
		if (menu && button.getModel().isPressed()) {
			try {
				MidiTest.key = Integer.parseInt(seedText.getText());
				MidiTest.standardNote = Integer.parseInt(denText.getText());
				MidiTest.beatsPerMeasure = Integer.parseInt(numText.getText());
				MidiTest.duration = MidiTest.BPMtoMPB(120, MidiTest.beatsPerMeasure);
				hidePointer();
				menu = false;
				GUI.setVisible(false);
				(new Thread(new ThreadManager())).start();
				// music generation has now begun
			} catch (Exception e) {
				// USER INPUT IS IMPROPERLY FORMATTED
			}
		}
		frame.repaint();
	}

	public static void readNote(int sides, double sleep, int measure) {
		mainPolygon.transformRate = (int) (sleep / REFRESHRATE);
		mainPolygon.p = newPolygon.gon(mainPolygon.p.realpts, RADIUS);
		mainPolygon.color = Color.getHSBColor((float) measure / 12, 1, 1);
		mainPolygon.transformShape(newPolygon.gon(getNextSide(mainPolygon.p.realpts), RADIUS));
	}

	public static int getNextSide(int s) {
		Random rand = new Random();
		if (s == MIN_SIDES) {
			return MIN_SIDES + 1;
		} else if (s == MAX_SIDES) {
			return MAX_SIDES - 1;
		}
		return s + new int[] { -1, 1 }[rand.nextInt(2)];
	}

	public static void enableFullScreenMode(Window window) {
		// Mac OS ONLY
		String className = "com.apple.eawt.FullScreenUtilities";
		String methodName = "setWindowCanFullScreen";

		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod(methodName, new Class<?>[] { Window.class, boolean.class });
			method.invoke(null, window, true);
		} catch (Throwable t) {
			System.err.println("Full screen mode is not supported");
			t.printStackTrace();
		}
	}

	public static void hidePointer() {
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		frame.getContentPane().setCursor(blankCursor);
	}
}