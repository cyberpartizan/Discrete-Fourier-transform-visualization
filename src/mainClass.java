import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.ConvolveOp;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.naming.ldap.SortKey;
import javax.swing.*;
import javax.swing.Box.Filler;

import org.w3c.dom.css.RGBColor;

public class mainClass implements ActionListener, KeyListener {
	public int height, width, circles;
	public double time, radius, translate;
	ArrayList<double[]> path = new ArrayList<double[]>();
	ArrayList<Double> test = new ArrayList<Double>();
	fourie fourievar = new fourie();
	public boolean dotatupperleftcorner = true;
	trainvarX trainvarX = new trainvarX();
	trainvarY trainvarY = new trainvarY();
	ArrayList<double[]> fourielistY = new ArrayList<double[]>();
	ArrayList<double[]> fourielistX = new ArrayList<double[]>();
	// ArrayList<fourie> fourie = new ArrayList<fourie>(5);
	ArrayList<Double> Y = new ArrayList<Double>();
	double[] VectorX, VectorY;
	ArrayList<Double> X = new ArrayList<Double>();
	public static mainClass mainClass;
	public Renderer renderer;
	Font font = new Font("TimesRoman", Font.BOLD, 50);
	DecimalFormat df = new DecimalFormat("0.000");

	public mainClass() {
		Random random = new Random();
		/*
		 * for (double i = 0; i < Math.PI * 2; i += 0.05) {
		 * 
		 * Y.add(100 * Math.sin(i)); X.add(100 * Math.cos(i)); }
		 */
		for (int i = 0; i < trainvarY.Y.size(); i += 10) {
			Y.add(trainvarY.Y.get(i));
			X.add(trainvarX.X.get(i));
		}

		df.setRoundingMode(RoundingMode.UP);
		width = 1280;
		height = 800;
		radius = 50;
		
		time = 0f;
		JFrame window = new JFrame();
		renderer = new Renderer();

		fourielistY.addAll(dft(Y));
		fourielistX.addAll(dft(X));
		circles = Y.size();
		Collections.sort(fourielistY, new Comparator<double[]>() {
		    @Override
			public int compare(double[] o1, double[] o2) {
		    	int first=(int)o1[0];
		    	int second=(int)o2[0];
				return first > second ? -1 : (first < second) ? 1 : 0;
			}
		});
		Collections.sort(fourielistX, new Comparator<double[]>() {
		    @Override
			public int compare(double[] o1, double[] o2) {
		    	int first=(int)o1[0];
		    	int second=(int)o2[0];
				return first > second ? -1 : (first < second) ? 1 : 0;
			}
		});
		window.add(renderer);
		window.setSize(width, height);
		window.setTitle("Прога");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.addKeyListener(this);
		Timer sleep = new Timer(20, this);
		sleep.start();
	
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

			fourievar.setAmp(Math.sqrt(re * re + im * im));
			fourievar.setFreq(k);
			fourievar.setIm(im);
			fourievar.setRe(re);
			fourievar.setPhase(Math.atan2(im, re));
			X.add(new double[] { fourievar.getAmp(), fourievar.getFreq(), fourievar.getPhase(), fourievar.getRe(),
					fourievar.getIm() });
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

			// graphics.fillOval((int) Math.round(x), (int) Math.round(y), 6, 6);
			graphics.setColor(Color.white);

		}
		// graphics.drawLine((int) Math.round(x + 3), (int) Math.round(y + 3), 300,
		// (int) Math.round(y + 3));
		return new double[] { x, y };
	}

	public void repaint(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(String.valueOf(circles), 0, 40);
		double x, y;
		x = y = 0;
		VectorY = epiCycles(150, 500, Math.PI / 2, fourielistY, g, gg);
		VectorX = epiCycles(800, 150, 0, fourielistX, g, gg);
		/*
		 * for (int i = 0; i < fourielistY.size(); i++) { double prevx = x; double prevy
		 * = y; int n = (int) (fourielistY.get(i)[1]); radius = fourielistY.get(i)[0]; x
		 * += radius * Math.cos(n * time + fourielistY.get(i)[2] + (Math.PI / 2)); y +=
		 * radius * Math.sin(n * time + fourielistY.get(i)[2] + (Math.PI / 2));
		 * g.setColor(Color.white); gg.draw(new Line2D.Double(translate + prevx + 3,
		 * translate + prevy + 3, x + 200 + radius, y + 200 + radius)); g.setColor(new
		 * Color(50, 50, 50)); translate = 200 + radius - 3; g.drawOval((int)
		 * Math.round(prevx + translate - radius) + 3, (int) Math.round(prevy +
		 * translate - radius) + 3, ((int) Math.round(radius * 2)), ((int)
		 * Math.round(radius * 2)));
		 * 
		 * g.fillOval((int) Math.round(x + translate), (int) Math.round(y + translate),
		 * 6, 6); g.setColor(Color.white);
		 * 
		 * } g.drawLine((int) Math.round(x + translate + 3), (int) Math.round(y +
		 * translate + 3), (int) Math.round(translate + 300), (int) Math.round(y +
		 * translate + 3));
		 */
		double[] Vector = new double[] { VectorX[0], VectorY[1] };
		if (dotatupperleftcorner) {
			path.add(0, Vector);
			dotatupperleftcorner = false;
		}
		path.add(0, Vector);

		gg.draw(new Line2D.Double(VectorX[0] + 3, VectorX[1] + 3, Vector[0] + 3, Vector[1] + 3));
		gg.draw(new Line2D.Double(VectorY[0] + 3, VectorY[1] + 3, Vector[0] + 3, Vector[1] + 3));

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
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
}
