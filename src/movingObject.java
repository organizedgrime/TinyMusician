import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class movingObject extends JPanel {

	// class level variables
	newPolygon p, tp;
	Color color;
	int speed, transformRate = 50;

	// transform per tick
	double[] tptx, tpty;
	double spt;

	double rotation, rotationRate, angle;
	int x, y;

	public movingObject(newPolygon _p, Color _color, int _x, int _y) {
		this.p = _p;
		this.color = _color;
		this.x = _x;
		this.y = _y;
	}

	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setColor(color);
		// scale and rotate the newPolygongon
		AffineTransform PolygonTransform = g.getTransform();

		try {
			AffineTransform transformMatrix = new AffineTransform();
			transformMatrix.rotate(rotation, calculateCentroid().getX(), calculateCentroid().getY());
			transformMatrix.translate(x, y);
			g.setTransform(transformMatrix);

			g.fill(p.getPolygon());
		} finally {
			g.setTransform(PolygonTransform);
		}
	}

	public void tick() {
		// transform on x and y based on the speed and angle of the object
		for (int i = 0; i < this.p.npoints; i++) {
			this.p.xpoints[i] = x + (this.p.xpoints[i] - x) * Math.cos(rotation) - (this.p.ypoints[i] - y) * Math.sin(rotation);
			this.p.ypoints[i] = y + (this.p.xpoints[i] - x) * Math.sin(rotation) + (this.p.ypoints[i] - y) * Math.cos(rotation);
		}
		rotation += rotationRate;
		if(p != tp)
		{
			//System.out.println("NOT");
			transformPoint();
		}
		repaint();
	}

	public void translate(int dx, int dy) {
		// transform the newPolygongon x and y direction
		for (int i = 0; i < p.xpoints.length; i++) {
			p.xpoints[i] = p.xpoints[i] + dx;
		}
		for (int i = 0; i < p.ypoints.length; i++) {
			p.ypoints[i] = p.ypoints[i] + dy;
		}
	}

	public Point2D.Double calculateCentroid() {
		double x = 0.;
		double y = 0.;
		int pointCount = p.xpoints.length;
		for (int i = 0; i < pointCount - 1; i++) {
			x += p.xpoints[i];
			y += p.ypoints[i];
		}

		x = x / pointCount;
		y = y / pointCount;

		return new Point2D.Double(x, y);
	}

	public void transformShape(newPolygon p2) {
		while (p2.npoints > p.npoints) {
			p = new newPolygon(concat(p.xpoints, new double[] { p.xpoints[0] }), concat(p.ypoints, new double[] { p.ypoints[0] }), p.npoints + 1);
		}
		while (p2.npoints < p.npoints) {
			p2 = new newPolygon(concat(p2.xpoints, new double[] { p2.xpoints[0] }), concat(p2.ypoints, new double[] { p2.ypoints[0] }), p2.npoints + 1);
		}
		tpty = new double[p.npoints];
		tptx = new double[p.npoints];

		for (int i = 0; i < p.npoints; i++) {
			tptx[i] = (p2.xpoints[i] - p.xpoints[i]) / transformRate;
			tpty[i] = (p2.ypoints[i] - p.ypoints[i]) / transformRate;
		}
		tp = p2;
	}

	public void transformPoint() {
		for (int i = 0; i < tptx.length; i++) {
			try {
				p.xpoints[i] += tptx[i];
				p.ypoints[i] += tpty[i];
				//System.out.println(Arrays.toString(p.xpoints) + ":" +Arrays.toString(p.ypoints));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}

	public double[] concat(double[] a, double[] b) {
		int aLen = a.length;
		int bLen = b.length;
		double[] c = new double[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

}
