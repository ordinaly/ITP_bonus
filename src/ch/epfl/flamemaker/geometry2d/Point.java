package ch.epfl.flamemaker.geometry2d;

import java.io.Serializable;



/**
 * <p>La classe point permet de modeliser des points.</p>
 * <p>Un point est caracterise par : <br />
 * la composante <code><b>x</b></code> parallele a l'abscisse dans le systeme cartesien. <br />
 * la composante <code><b>y</b></code> parallele a l'ordonnee dans le systeme cartesien. <br />
 * le <code>Point <b>ORIGIN</b></code>, qui contient le point de coordonnees (0,0).</p>
 * <p>De plus, elle fournit des methodes permettant d'obtenir: <br />
 * la distance <code>r</code> du point a une origine O dans le systeme polaire. <br />
 * l'angle <code>theta</code> entre l'abscisse et la ligne le reliant cette origine.</p>
 */
public final class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1087981482368359610L;
	private final double x;
    private final double y;
    public static final  Point ORIGIN = new Point(0,0);
    
   /**
    * Constructeur de la classe Point
    * @param x la coordonnee x du point
    * @param y la coordonnee y du point
    */
    public Point(double x, double y){
        this.x=x;
        this.y=y;
    }
    
   /**
    * Retourne l'abscisse du point
    * @return la coordonnee x du point
    */
    public double x(){
        return x;
    }
    
    /**
     * Retourne l'ordonnee du point
     * @return la coordonnee y du point
     */
    public double y(){
        return y;
    }
    
    /**
     * Retourne la distance du point(x,y) par rapport a l'origine
     * @return la distance en <code>double</code> entre le point et l'orgine
     */
    public double r(){
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    /**
     * Retourne l'angle que fait le point(x,y) par rapport a l'axe des abscisses.
     * @return l'angle en <code>double</code> entre le point et l'axe des abscisses
     */
    public double theta(){
    	return Math.atan2(y, x);
    }
    
   /**
    * Retourne representation textuelle du point(x,y), en <code>String</code>
    */
    public String toString(){
        return "("+x+","+y+")";
       
    }
}