package ch.epfl.flamemaker.color;

import java.io.Serializable;

/**
 * <p>La classe <code>Color</code> modelise une couleur a l'aide de ses trois composantes: rouge, vert, bleu, representees par les 3 attributs <code><b>r, g, b</b></code>.</p>
 * <p>La classe <code>Color</code> possede plusieurs champs statiques non modifiables contenant des couleurs de base <code><b>BLACK, WHITE,RED,BLUE,GREEN</b></code>.</p>
 *
 */
public final class Color implements Serializable {	
	private static final long serialVersionUID = 3441641759250755136L;
	public static final Color BLACK = new Color(0,0,0);
	public static final Color WHITE = new Color(1,1,1);
	public static final Color RED = new Color(1,0,0);
	public static final Color BLUE = new Color(0,0,1);
	public static final Color GREEN = new Color(0,1,0);
	
	private final double r, g, b;
	
	/**
	 * construit une couleur en fonction des composantes rouge, verte et bleue donnes.
	 * @param r la composante rouge
	 * @param g la composante verte
	 * @param b la composante bleue
	 * @throws IllegalArgumentException si l'une des composantes ne se trouvent pas entre 0 et 1
	 */
	public Color(double r, double g, double b) {
		if(r > 1 || r < 0 || g > 1 || g < 0 || b > 1 || b < 0)
			throw new IllegalArgumentException("La valeur d'une des composantes n'est pas valide !");
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * Retourne la composante rouge de la couleur.
	 * @return <code>double</code> r
	 */
	public double red() {
		return r;
	}
	
	/**
	 * retourne la composante verte de la couleur.
	 * @return <code>double</code> g
	 */
	public double green() {
		return g;
	}
	
	/**
	 * retourne la composante bleur de la couleur.
	 * @return <code>double</code> b
	 */
	public double blue() {
		return b;
	}
	
	/**
	 * retourne la couleur obtenue en melangeant la couleur representee par le recepteur, en proportion donnee, avec la couleur that.
	 * @param that la couleur avec laquelle on melange le recepteur
	 * @param p la proportion de couleur du recepteur
	 * @throws IllegalArgumentException si la proportion n'est pas comprise entre 0 et 1
	 * @return la couleur <code>Color</code> resultant du melange entre le recepteur et that
	 */
	public Color mixWith(Color that, double p) {
		if(p > 1 || p < 0)
			throw new IllegalArgumentException("La proportion n'est pas valide !");
		double r = p*this.r + (1-p)*that.r;
		double g = p*this.g + (1-p)*that.g;
		double b = p*this.b + (1-p)*that.b;
		return new Color(r, g, b);
	}
	
	/**
	 * <p>Genere un format "packed" pour afficher la couleur: les 3 composantes sont entre 0 et 255 et sont concatenees en une seule valeur.</p>
	 * <p>On utilise les operations sur les bits: shift pour decaler les nombres et <code>OR</code> logique pour les concatener</p> 
	 * @return la valeur <code>int</code> des 3 composantes de la couleur concatenees.
	 */
	public int asPackedRGB() {
		return ((sRGBEncode(r, 255) << 16) | (sRGBEncode(g,255) << 8) | sRGBEncode(b, 255));
	}
	
	/**
	 * Encode la composante de couleur en norme sRGB et renvoie celle-ci sous forme d'un entier entre 0 et max 
	 * @param v la valeur de la composante, entre 0 et 1
	 * @param max la valeur maximale pour la composante
	 * @return un <code>int</code> entre 0 et <code>max</code> qui represente la composante encodee en sRGB
	 */
	public static int sRGBEncode(double v,int max)
	{
		if ( v <= 0.0031308)
			v = 12.92*v;
		else
			v = (1.055*Math.pow(v, 1/2.4))-0.055;
		
		return (int)(v*max);
	}
	
	@Override
	public String toString() {
		return "Color [r=" + r + ", g=" + g + ", b=" + b + "]";
	}
}
