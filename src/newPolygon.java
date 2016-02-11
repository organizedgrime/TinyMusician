import java.awt.Polygon;
import java.text.DecimalFormat;

public class newPolygon extends Polygon {
	double[] xpoints, ypoints;
	static int radius, realpts;
	static boolean front = true;
	static DecimalFormat fmt = new DecimalFormat("0.00");

	public newPolygon(double[] xpoints, double[] ypoints, int npoints) {
		this.xpoints = xpoints;
		this.ypoints = ypoints;
		this.npoints = npoints;
	}

	public void addPoint(double d, double e) {
		this.addPoint((int) d, (int) e);
	}

	public Polygon getPolygon() {
		int[] xp = new int[this.npoints];
		int[] yp = new int[this.npoints];

		for (int i = 0; i < this.npoints; i++) {
			xp[i] = (int) this.xpoints[i];
			yp[i] = (int) this.ypoints[i];
		}
		return new Polygon(xp, yp, this.npoints);
	}

	public static newPolygon gon(int n, int r) {
		radius = r;
		realpts = n;
		newPolygon p = new newPolygon(new double[n], new double[n], n);
		

		for (double i = 0, j = 0; j < n; i += (2 * Math.PI) / n, j++) {
			p.xpoints[(int) j] = Double.parseDouble(fmt.format(Math.cos(i) * r));
			p.ypoints[(int) j] = Double.parseDouble(fmt.format(Math.sin(i) * r));
		}
		return p;
	}
	
	public static boolean polyEquals(newPolygon p, newPolygon p2)
	{
		
		for(int i = 0; i < p.npoints; i++)
		{
			//check if the values are different by more than 1. This avoids stupid floating point stuff
			if(Math.abs(p.xpoints[i] - p2.xpoints[i]) > 1  || Math.abs(p.ypoints[i] - p2.ypoints[i]) > 1)
			{
				return false;
			}
		}
		return true;
	}
}
