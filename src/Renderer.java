import java.awt.Graphics;

import javax.swing.JPanel;

public class Renderer extends JPanel{
	
	private static final long serialVersionUID=1l;
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		mainClass.mainClass.repaint(g);
	}
}
