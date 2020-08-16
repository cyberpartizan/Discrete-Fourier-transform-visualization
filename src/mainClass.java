import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
	ArrayList<ComplexNumber> path = new ArrayList<ComplexNumber>();
	public boolean dotatupperleftcorner = true;
	File file;
	Timer sleep;
	boolean pressed = false;
	String s;
	Graphics publicg;
	Scanner sc;
	int comaindex;
	ArrayList<double[]> fourielistX = new ArrayList<double[]>();
	ArrayList<Double> Y = new ArrayList<Double>();
	ComplexNumber VectorX, VectorY;
	ArrayList<ComplexNumber> X = new ArrayList<ComplexNumber>();
	public static mainClass mainClass;
	public Renderer renderer;
	public String a;
	File currentfile, ipimif, calibrii, flamingo, dog;
	Font font = new Font("TimesRoman", Font.BOLD, 10);
	DecimalFormat df = new DecimalFormat("0.000");
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public mainClass() {
		a = null;
		try {
			a = new File(".").getCanonicalPath();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		width = 1280;
		height = 720;
		minusheight = height / 1.65;
		minuswidth = width / 2.5;
		devide = height / 300;

		ipimif = new File(a+"\\Paths\\ipimif.txt");
		
		calibrii = new File(a+"\\Paths\\calibrii.txt");
		flamingo = new File(a+"\\Paths\\flamingo.txt");
		dog = new File(a+"\\Paths\\dog.txt");
		currentfile = calibrii;
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
			//Sorting cycles by amplitude in descending order
			@Override
			public int compare(double[] o1, double[] o2) {
				int first = (int) o1[0];
				int second = (int) o2[0];
				return first > second ? -1 : (first < second) ? 1 : 0;
			}
		});
		return sortinglist;
	}
	
	private ArrayList<double[]> dft(ArrayList<ComplexNumber> x) {
		// Discrete Fourier transform 
		// Return amplitude, frequency, phase , cycle position x , cycle position y
		ArrayList<double[]> X = new ArrayList<double[]>();
		int N = x.size();
		for (int k = 0; k < N; k++) {
			ComplexNumber sum = new ComplexNumber(0, 0);
			for (int n = 0; n < N; n++) {
				double phi = (Math.PI * 2 * k * n) / N;
				ComplexNumber phiC = new ComplexNumber(Math.cos(phi), -Math.sin(phi));
				sum = new ComplexNumber(sum.re + x.get(n).re * phiC.re - x.get(n).im * phiC.im,
						sum.im + x.get(n).re * phiC.im + x.get(n).im * phiC.re);
			}
			sum.re = sum.re / N;
			sum.im = sum.im / N;
			X.add(new double[] { Math.sqrt(sum.re * sum.re + sum.im * sum.im), k, Math.atan2(sum.im, sum.re), sum.re,
					sum.im });
		}
		return X;
	}

	public static void main(String[] args) {
		mainClass = new mainClass();
	}

	public ComplexNumber epiCycles(double x, double y, double r, ArrayList<double[]> f, Graphics graphics,
			Graphics2D graphics2d) {
		// Drawing epicycles
		for (int i = 0; i < circles; i++) {
			double prevx = x;
			double prevy = y;
			int n = (int) (f.get(i)[1]);
			radius = f.get(i)[0];
			x += radius * Math.cos(n * time + f.get(i)[2] + r);
			y += radius * Math.sin(n * time + f.get(i)[2] + r);
			graphics.setColor(Color.GREEN.darker().darker().darker().darker());
			graphics.drawOval((int) Math.round(prevx - radius) + 3, (int) Math.round(prevy - radius) + 3,
					((int) Math.round(radius * 2)), ((int) Math.round(radius * 2)));
			graphics.setColor(Color.red);
			graphics2d.draw(new Line2D.Double(prevx + 3, prevy + 3, x + 3, y + 3));
			graphics.setColor(Color.white);
		}
		return new ComplexNumber(x, y);
	}

	public void initializepath(File f) {
		//Initialization
		path.clear();
		fourielistX.clear();
		X.clear();
		Y.clear();
		time = 0;

		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException exeption) {
			exeption.printStackTrace();
		}

		while (sc.hasNextLine()) {
			s = sc.next();
			comaindex = s.indexOf(",");
			ComplexNumber c = new ComplexNumber((Double.valueOf(s.substring(0, comaindex)) - minuswidth) / devide,
					(Double.valueOf(s.substring(comaindex + 1, s.length())) - minusheight) / devide);
			X.add(c);
		}
		fourielistX.addAll(dft(X));
		circles = X.size();
		fourielistX = sortArraylistOfDoubleArray(fourielistX);
	}

	public void repaint(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(String.valueOf(circles), 0, 40);
		g.drawString(String.valueOf(sleep.getDelay()), 0, 90);
		VectorX = epiCycles(width / 2, height / 2, 0, fourielistX, g, gg);
		
		g.drawString(ipimif.getAbsolutePath().toString(), 0, 300);
		ComplexNumber Vector = new ComplexNumber(VectorX.re, VectorX.im);
		if (dotatupperleftcorner) {
			path.add(0, Vector);
			dotatupperleftcorner = false;
		}
		path.add(0, Vector);
		int color = 255;
		gg.setColor(new Color(color, color, color));
		for (int i = 1; i < path.size(); i++) {
			gg.draw(new Line2D.Double(path.get(i - 1).re, path.get(i - 1).im, path.get(i).re, path.get(i).im));
		}
		double dt = (2 * Math.PI) / X.size();
		time += dt;
		if (time > Math.PI * 2) {
			path.clear();
			time = 0;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		fourielistX.addAll(dft(X));
		circles = X.size();
		fourielistX = sortArraylistOfDoubleArray(fourielistX);
		sleep.start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP && circles < fourielistX.size()) {
			circles++;
		} else if (key == KeyEvent.VK_DOWN && circles > 1) {
			circles--;
		} else if (key == KeyEvent.VK_1) {
			sleep.stop();
			initializepath(ipimif);
			sleep.start();
		} else if (key == KeyEvent.VK_2) {
			sleep.stop();
			initializepath(calibrii);
			sleep.start();
		} else if (key == KeyEvent.VK_3) {
			sleep.stop();
			initializepath(flamingo);
			sleep.start();
		} else if (key == KeyEvent.VK_4) {
			sleep.stop();
			initializepath(dog);
			sleep.start();
		} else if (key == KeyEvent.VK_9) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				sleep.stop();
				initializepath(new File(chooser.getSelectedFile().getPath()));
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
	public void mouseDragged(MouseEvent e) {
		ComplexNumber temp = new ComplexNumber((double) e.getX() - width / 2, (double) e.getY() - height / 2);
		X.add(temp);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		X.clear();
		Y.clear();
		sleep.stop();
		path.clear();
		fourielistX.clear();
		time = 0;
		sc.reset();
		renderer.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		renderer.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

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

}
