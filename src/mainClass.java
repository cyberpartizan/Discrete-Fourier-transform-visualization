import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class mainClass implements ActionListener, KeyListener, MouseMotionListener, MouseListener {
	public int height, width, circles;
	public double time, radius, translate, minusheight, minuswidth, devide;
	ArrayList<double[]> path = new ArrayList<double[]>();
	ArrayList<Double> test = new ArrayList<Double>();
	public boolean dotatupperleftcorner = true;
	File file;
	Timer sleep;
	boolean pressed = false;
	String s;
	Graphics publicg;
	Scanner sc;
	int comaindex;
	ArrayList<double[]> fourielistY = new ArrayList<double[]>();
	ArrayList<double[]> fourielistX = new ArrayList<double[]>();
	ArrayList<Double> Y = new ArrayList<Double>();
	double[] VectorX, VectorY;
	ArrayList<Double> X = new ArrayList<Double>();
	public static mainClass mainClass;
	public Renderer renderer;
	File currentfile, ipimif, train, calibrii, flamingo, dog;
	Font font = new Font("TimesRoman", Font.BOLD, 50);
	DecimalFormat df = new DecimalFormat("0.000");
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public mainClass() {
		width = 1280;
		height = 720;
		minusheight = height / 1.65;
		minuswidth = width / 2.5;
		devide = height / 300;
		ipimif = new File("C:\\Users\\Я\\eclipse-workspace\\123\\src\\ipimif.txt");
		calibrii = new File("C:\\Users\\Я\\eclipse-workspace\\123\\src\\calibrii.txt");
		flamingo = new File("C:\\Users\\Я\\eclipse-workspace\\123\\src\\flamingo.txt");
		dog = new File("C:\\Users\\Я\\eclipse-workspace\\123\\src\\dog.txt");
		currentfile = calibrii;
		try {
			sc = new Scanner(currentfile);
		} catch (FileNotFoundException exeption) {
			exeption.printStackTrace();
		}
		time = 0f;
		df.setRoundingMode(RoundingMode.UP);

		JFrame window = new JFrame();
		renderer = new Renderer();
		window.add(renderer);
		window.setSize(width, height);
		window.setTitle("Прога");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.addKeyListener(this);
		window.addMouseMotionListener(this);
		window.addMouseListener(this);
		sleep = new Timer(20, this);
	}

	public ArrayList<double[]> sortArraylistOfDoubleArray(ArrayList<double[]> sortinglist) {
		Collections.sort(sortinglist, new Comparator<double[]>() {
			@Override
			public int compare(double[] o1, double[] o2) {
				int first = (int) o1[0];
				int second = (int) o2[0];
				return first > second ? -1 : (first < second) ? 1 : 0;
			}
		});
		return sortinglist;
	}

	private ArrayList<double[]> dft(ArrayList<Double> x) {
		ArrayList<double[]> X = new ArrayList<double[]>();
		int N = x.size();
		for (int k = 0; k < N; k++) {
			double re = 0;
			double im = 0;
			for (int n = 0; n < N; n++) {
				double phi = (Math.PI * 2 * k * n) / N;
				re += x.get(n) * Math.cos(phi);
				im -= x.get(n) * Math.sin(phi);
			}
			re = re / N;
			im = im / N;
			X.add(new double[] { Math.sqrt(re * re + im * im), k, Math.atan2(im, re), re, im });
		}
		return X;
	}

	public static void main(String[] args) {

		mainClass = new mainClass();
	}

	public double[] epiCycles(double x, double y, double r, ArrayList<double[]> f, Graphics graphics,
			Graphics2D graphics2d) {
		for (int i = 0; i < circles; i++) {
			double prevx = x;
			double prevy = y;
			int n = (int) (f.get(i)[1]);
			radius = f.get(i)[0];
			x += radius * Math.cos(n * time + f.get(i)[2] + r);
			y += radius * Math.sin(n * time + f.get(i)[2] + r);
			graphics.setColor(Color.gray);
			graphics2d.draw(new Line2D.Double(prevx + 3, prevy + 3, x + 3, y + 3));
			graphics.setColor(Color.white);
			graphics.setColor(new Color(50, 50, 50));
			graphics.drawOval((int) Math.round(prevx - radius) + 3, (int) Math.round(prevy - radius) + 3,
					((int) Math.round(radius * 2)), ((int) Math.round(radius * 2)));

			graphics.setColor(Color.white);

		}
		return new double[] { x, y };
	}

	public void initializepath() {
		path.clear();
		fourielistY.clear();
		fourielistX.clear();
		X.clear();
		Y.clear();
		time = 0;
		sc.reset();
		try {
			sc = new Scanner(currentfile);
		} catch (FileNotFoundException exeption) {
			exeption.printStackTrace();
		}
		while (sc.hasNextLine()) {
			s = sc.next();
			comaindex = s.indexOf(",");
			X.add((Double.valueOf(s.substring(0, comaindex)) - minuswidth) / devide);
			Y.add((Double.valueOf(s.substring(comaindex + 1, s.length())) - minusheight) / devide);
		}
		fourielistY.addAll(dft(Y));
		fourielistX.addAll(dft(X));
		circles = Y.size();
		fourielistY = sortArraylistOfDoubleArray(fourielistY);
		fourielistX = sortArraylistOfDoubleArray(fourielistX);
	}

	public void repaint(Graphics g) {
		publicg = g;
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(String.valueOf(circles), 0, 40);
		g.drawString(String.valueOf(sleep.getDelay()), 0, 90);
		VectorY = epiCycles(150, 500, Math.PI / 2, fourielistY, g, gg);
		VectorX = epiCycles(800, 150, 0, fourielistX, g, gg);
		double[] Vector = new double[] { VectorX[0], VectorY[1] };
		if (dotatupperleftcorner) {
			path.add(0, Vector);
			dotatupperleftcorner = false;
		}
		path.add(0, Vector);
		gg.setColor(Color.gray);
		if (sleep.isRunning()) {
			gg.draw(new Line2D.Double(VectorX[0] + 3, VectorX[1] + 3, Vector[0] + 3, Vector[1] + 3));
			gg.draw(new Line2D.Double(VectorY[0] + 3, VectorY[1] + 3, Vector[0] + 3, Vector[1] + 3));
		}
		for (int i = 1; i < path.size(); i++) {
			gg.draw(new Line2D.Double(path.get(i - 1)[0] + 3, path.get(i - 1)[1] + 3, path.get(i)[0] + 3,
					path.get(i)[1] + 3));

		}
		double dt = (2 * Math.PI) / Y.size();
		time += dt;
		if (time > Math.PI * 2) {
			path.clear();
			time = 0;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		renderer.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP && circles < fourielistY.size()) {
			circles++;
		} else if (key == KeyEvent.VK_DOWN && circles > 1) {
			circles--;
		} else if (key == KeyEvent.VK_1) {
			sleep.stop();
			currentfile = ipimif;
			initializepath();
			sleep.start();
		} else if (key == KeyEvent.VK_2) {
			sleep.stop();
			currentfile = calibrii;
			initializepath();
			sleep.start();
		} else if (key == KeyEvent.VK_3) {
			sleep.stop();
			currentfile = flamingo;
			initializepath();
			sleep.start();
		} else if (key == KeyEvent.VK_4) {
			sleep.stop();
			currentfile = dog;
			initializepath();
			sleep.start();
		} else if (key == KeyEvent.VK_9) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				sleep.stop();
				currentfile = new File(chooser.getSelectedFile().getPath());
				initializepath();
				sleep.start();
			}
		} else if (key == KeyEvent.VK_0) {
			if (sleep.isRunning()) {
				sleep.stop();
			} else {
				sleep.start();
			}

		} else if (key == KeyEvent.VK_LEFT) {
			if (sleep.getDelay() > 20) {
				sleep.setDelay(sleep.getDelay() - 1);
			}
		} else if (key == KeyEvent.VK_RIGHT) {
			sleep.setDelay(sleep.getDelay() + 1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		X.add((double) e.getX() - 800 - 10);
		Y.add((double) e.getY() - 500 - 10);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		X.clear();
		Y.clear();
		sleep.stop();
		path.clear();
		fourielistY.clear();
		fourielistX.clear();
		time = 0;
		sc.reset();
		renderer.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		fourielistY.addAll(dft(Y));
		fourielistX.addAll(dft(X));
		circles = Y.size();
		fourielistY = sortArraylistOfDoubleArray(fourielistY);
		fourielistX = sortArraylistOfDoubleArray(fourielistX);
		sleep.start();

	}
}
