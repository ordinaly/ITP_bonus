package ch.epfl.flamemaker.geometry2d;

/**
 * L'interface Transformation modelise une fonction faisant correspondre e tout point du plan un autre point du plan (=concept de transformation).
 */
public interface Transformation {
	/**
	 * Retourne le point p transforme
	 * @param p le point a transformer
	 * @return le point transforme
	 * @see Point
	 */
	Point transformPoint(Point p);
}
