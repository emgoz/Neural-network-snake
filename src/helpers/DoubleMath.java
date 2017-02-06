package helpers;


public class DoubleMath {
	public static double doubleModulo(double a, double b){
		int k = (int)(a/b);
		if (a < 0) k--;
		return a-b*k;
	}
	public static double signedDoubleModulo(double a, double b){
		double c = doubleModulo(a, b);
		if (c >= b/2) c-=b;
		return c;
	}
}
