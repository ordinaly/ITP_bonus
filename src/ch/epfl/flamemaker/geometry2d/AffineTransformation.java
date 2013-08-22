package ch.epfl.flamemaker.geometry2d;

import java.io.Serializable;

/**
 * la classe AffineTransformation represente les transformations affines sous forme de matrices.  <br />
 * Elle contient les six elements <code>double <b>a, b, c, d, e, f</b></code> qui sont les 6 premieres composantes de la matrice de transformation ainsi que la matrice identite <code><b>IDENTITY</b></code> <br />
 * Elle implemente l'interface <code>Transformation</code>
 * @see Transformation
 */

public final class AffineTransformation implements Transformation, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5399030925175015127L;

	private final double a, b, c, d, e, f;
	
	public final static AffineTransformation IDENTITY = new AffineTransformation(1, 0, 0, 0, 1, 0); 
	/**
	 * Constructeur de la classe AffineTransformation
	 * qui construit une transformation affine étant donnés les éléments variables de la matrice.
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 */
	public AffineTransformation(double a, double b, double c, double d, double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}
	
	/**
	 * Cree une transformation affine representant une translation de <code>dx</code> unites parallelement a l'abscisse et <code>dy</code> unites parallelement a l'ordonnee.
	 * @param dx la translation parallele a l'abscisse
	 * @param dy la translation parallele a l'ordonnee
	 * @return une transformation affine de type translation.
	 */
	
	public static AffineTransformation newTranslation(double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 0, 1, dy);
	}
	
	/**
	 * Cree une transformation representant une rotation d'angle theta (en radians) autour de l'origine.
	 * @param theta l'angle de la rotation en radians.
	 * @return une transformation affine de type rotation.
	 */
	public static AffineTransformation newRotation(double theta) {
		return new AffineTransformation(Math.cos(theta), -Math.sin(theta), 0, Math.sin(theta), Math.cos(theta), 0);
	}
	
	/**
	 * Cree une transformation representant une dilatation d'un facteur <code>sx</code> parallelement a l'abscisse et d'un facteur <code>sy</code> parallelement a l'ordonnee.
	 * @param sx le facteur de dilatation parallele a l'abscisse
	 * @param sy le facteur de dilatation parallele a l'ordonnee.
	 * @return une transformation de type dilatation
	 */
	public static AffineTransformation newScaling(double sx, double sy) {
		return new AffineTransformation(sx, 0, 0, 0, sy, 0);
	}
	
	/**
	 * Cree une transformation affine representant une transvection d'un facteur <code>sx</code> parallelement a l'abscisse.
	 * @param sx le facteur de la transvection
	 * @return une transformation de type transvection horizontale
	 */
	public static AffineTransformation newShearX(double sx) {
		return new AffineTransformation(1, sx, 0, 0, 1, 0);
	}
	
	/**
	 * Cree une transformation affine representant une transvection d'un facteur <code>sy</code> parallelement a l'ordonnee.
	 * @param sy le facteur de la transvection
	 * @return une transformation de type transvection verticale
	 */
	public static AffineTransformation newShearY(double sy) {
		return new AffineTransformation(1, 0, 0, sy, 1, 0);
	}
	
	/**
	 * Retourne le point <code>p</code> transforme par cette transformation (methode de l'interface <code>Transformation</code>).
	 * @param p le point a transformer
	 * @return le point tranforme
	 * @see Transformation
	 * @see Point
	 */
	@Override
	public Point transformPoint(Point p) {
		return new Point((a*p.x() + b*p.y() + c), (d*p.x() + e*p.y() + f));
	}
	
	/**
	 * Retourne la composante horizontale de la translation
	 * @return la composante horizontale de la translation
	 */
	public double translationX() {
		return c;
	}
	
	/**
	 * Retourne la composante verticale de la translation
	 * @return la composante verticale de la translation
	 */
	public double translationY() {
		return f;
	}
	
	/**
	 * Retourne la transformation affine composee de cette transformation affine avec la transformation affine that.
	 * @param that la transformation affine avec laquelle on veut composer
	 * @return la <code>AffineTransformation</code> composee des deux transformations affine
	 */
	public AffineTransformation composeWith(AffineTransformation that) {
		return new AffineTransformation(a*that.a + b*that.d, a*that.b + b*that.e, a*that.c + b*that.f + c, d*that.a + e*that.d, d*that.b + e*that.e, d*that.c + e*that.f + f);
	}
	
}
