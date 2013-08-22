package ch.epfl.flamemaker.geometry2d;

import java.io.Serializable;

/**
 * <p>La classe Rectangle permet de modeliser des rectangles paralleles aux axes.</p>
 * <p>Un rectangle est caracterise par : <br /> 
 * le <code><b>centre</b></code> du rectangle qui est de type <code>Point</code>. <br />
 * la <code><b>largeur</b></code> du rectangle <br />
 * la <code><b>hauteur</b></code> du rectangle</p> 
 */
public final class Rectangle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3916946421005995595L;
	private final Point centre;
	private final double largeur;
	private final double hauteur;
	
	/**
	 * Constructeur de la classe Rectangle
	 * @param centre le <code>Point</code> au centre du rectangle
	 * @param largeur la largeur du rectangle
	 * @param hauteur la hauteur du rectangle
	 * @throws IllegalArgumentException si les coordonnees sont negatives ou nulles.
	 * @see Point
	 */
	public Rectangle(Point centre, double largeur, double hauteur) {
		this.centre = centre;
		
		if(largeur <= 0)
			throw new IllegalArgumentException("La largeur doit être positive !");
		if(hauteur <= 0)
			throw new IllegalArgumentException("La hauteur doit être positive !");

		this.largeur = largeur;
		this.hauteur = hauteur;
	}
	
	/**
	 * Retourne la plus petite coordonnee x du rectangle. 
	 * @return la plus petite coordonnee x en <code>double</code> du rectangle
	 */
	public double left() {
		return (double) centre.x()-(largeur/2.00);
	}
	
	/**
	 * Retourne la plus grande coordonnre x du rectangle.
	 * @return la plus grande coordonnre x en <code>double</code> du rectangle.
	 */
	public double right() {
		return (double) centre.x()+(largeur/2.00);
	}
	
	/**
	 * Retourne la plus petite coordonnee y du rectangle.
	 * @return la plus petite coordonnee y en <code>double</code> du rectangle.
	 */
	public double bottom() {
		return (double) centre.y()-(hauteur/2.00);
	}
	
	/**
	 * Retourne la plus grande coordonnee y du rectangle.
	 * @return la plus grande coordonnee y en <code>double</code> du rectangle.
	 */
	public double top() {
		return (double) centre.y()+(hauteur/2.00);
	}
	
	/**
	 * Retourne la largeur du rectangle
	 * @return la largeur du rectangle en <code>double</code>
	 */
	public double width() {
		return largeur;
	}
	
	/**
	 * Retourne la hauteur du rectangle
	 * @return la hauteur du rectangle en <code>double</code>
	 */
	public double height() {
		return hauteur; 
	}
	
	/**
	 * Retourne le centre du rectangle 
	 * @return le <code>Point</code> au centre du rectangle
	 * @see Point
	 */
	public Point center() {
		return centre;
	}
	
	/**
	 * Retourne vrai si le point <code>p</code> se trouve a l'interieur du rectangle
	 * @param p le point a tester
	 * @return true si le point <code>p</code> est dans le rectangle, false sinon.
	 * @see Point
	 */
	public boolean contains(Point p) {
		if(p.x() >= left() && p.x() < right() && p.y() >= bottom() && p.y() < top()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Retourne le rapport largeur/hauteur du rectangle.
	 * @return le rapport largeur/hauteur du rectangle en <code>double</code>
	 */
	public double aspectRatio() {
		return (double) largeur/hauteur;
	}
	
	/**
	 * Retourne le plus petit rectangle ayant le meme centre que le recepteur, le meme rapport largeur/hauteur (<code>aspectRatio</code> et contenant totalement le recepteur
	 * @param newAspectRatio le rapport largeur/hauteur du nouveau rectangle
	 * @throws IllegalArgumentException si l'aspectRatio est negatif ou nul
	 * @return le nouveau rectangle au rapport <code>newAspectRatio</code>
	 * @see Rectangle#aspectRatio()
	 */
	
	public Rectangle expandToAspectRatio(double newAspectRatio) {
		if(newAspectRatio <= 0)
			throw new IllegalArgumentException("Le nouveau ratio doit être strictement positif !");
		if(newAspectRatio > aspectRatio()) {
			return new Rectangle(centre, newAspectRatio*hauteur, hauteur);
		}
		else if(newAspectRatio < aspectRatio()) {
			return new Rectangle(centre, largeur, largeur/newAspectRatio);
		}
		// Eventuellement renvoyer un nouvel objet si nécessaire
		return this;
	}
	
	/**
	 * Retourne la representation textuelle du rectangle en <code>String</code>
	 */
	public String toString() {
		return "(" + centre.toString() + ","+largeur+","+hauteur+")";
	}
}
