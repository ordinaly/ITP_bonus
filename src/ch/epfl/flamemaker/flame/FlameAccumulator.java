package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.color.*;
import ch.epfl.flamemaker.geometry2d.*;
/**
 * <p>La classe <code>FlameAccumulator</code> sert a representer le nombre de points de la fractale contenus dans chaque case de l'accumulateur, qui correspond a un pixel de l'image finale.</p>
 * <p>Possède un attribut <code>int[][] <b>flameAcc</b></code> qui est un tableau d'entiers contenant le nombre de points par case. <br />
 * Possède un attribut <code>int[][] <b>colorIndexSum</b></code> qui est un tableau de reels contenant la somme des index de couleur de chaque case, grace a laquelle l'index de couleur moyen de la case peut  etre determine. <br />
 * Possède un attribut <code>double <b>maxPoints</b></code> qui donne le nombre de points sur la case qui contient le plus de points dans l'accumulateur.</p>
 */
public final class FlameAccumulator {
	private final int[][] flameAcc;
	private final double[][] colorIndexSum; 
	private final int maxPoints;
	
	/**
	 * Construit un accumulateur avec le tableau bi-dimensionnel hitCount et le tableau bi-dimensionnel colorIndexSum donnes. <br />
	 * Determine le nombre maximum de points sur une case. 
	 * @param hitCount le tableau qui contient le nombre de points par case de l'image.
	 * @param colorIndexSum qui contient l'indice des couleurs a donner a chaque case.
	 */
	private FlameAccumulator(int[][] hitCount, double[][] colorIndexSum) {
		int maxPoints = 0;
		
		this.flameAcc = new int[hitCount.length][hitCount[0].length];
		for(int i=0; i<hitCount.length; i++) {
			for(int j=0; j<hitCount[i].length; j++) {
				this.flameAcc[i][j] = hitCount[i][j];
				maxPoints = maxPoints < hitCount[i][j] ? hitCount[i][j] : maxPoints;
			}
		}
		this.maxPoints = maxPoints;
		
		this.colorIndexSum = new double[colorIndexSum.length][];
		for(int i=0; i<colorIndexSum.length; i++) {
			this.colorIndexSum[i] = colorIndexSum[i].clone();
		}
		
	}
	
	/**
	 * Retourne la couleur de la case de l'accumulateur aux coordonnees donnees (x,y), en calculant l'index de couleur a la case donnee, divise par le nombre de points sur la case. <br />
	 * On melange ensuite cette couleur avec la couleur de fond, dans une proportion qui depend du nombre de points sur la case (methode <code>{@link FlameAccumulator#intensity(int, int) intensity()}</code>).
	 * @param palette la palette de couleurs utilisee.
	 * @param background la couleur de fond.
	 * @param x la coordonnee x de la case.
	 * @param y la coordonnee y de la case
	 * @return la couleur <code>Color</code> de la case (x,y).
	 * @see Palette
	 * @see Palette#colorForIndex(double)
	 * @see Color
	 * @see Color#mixWith(Color, double)
	 * @see FlameAccumulator#intensity(int, int)
	 */
	public Color color(Palette palette, Color background, int x, int y) {
		if(x < 0 || y < 0 || x > width() || y > height())
			throw new IllegalArgumentException("Les coordonn�es ne sont pas valides !");
		double proportion = intensity(x, y);
		if(proportion == 0)
			return background;
		return palette.colorForIndex((double) (colorIndexSum[y][x]/flameAcc[y][x])).mixWith(background, proportion);
	}
		
	/**
	 * Retourne la largeur (en nombre de cases) de l'accumulateur.	
	 * @return la largeur en <code>int</code> de l'accumulateur.
	 */
	public int width() {
		return flameAcc[0].length;
	}
	
	/**
	 * Retourne la hauteur (en nombre de cases) de l'accumulateur.
	 * @return la hauteur en <code>int</code> de l'accumulateur.
	 */
	public int height() {
		return flameAcc.length;
	}
	
	/**
	 * Retourne l'intensite de la case de l'accumulateur aux coordonnees donnees.
	 * @param x la coordonnee x de la case.
	 * @param y la coordonnee y de la case.
	 * @throws IndexOutOfBoundsException si l'une des coordonnees est negative ou depasse la coordonnee maximale.
	 * @return une valeur <code>double</code> qui depend du nombre de points sur la case, par rapport au nombre maximum de points sur une case.
	 */
	public double intensity(int x, int y) {
		if(x < 0 || x > width() || y < 0 || y > height())
			throw new IndexOutOfBoundsException("Coordonnees invalides !");
		double a = Math.log(flameAcc[y][x] + 1);
		double b = Math.log(maxPoints + 1);
		return a/b; 
	}
	
	/**
	 * Retourne le nombre de points sur la case de l'accumulateur aux coordonnees (x, y)  
	 * @param x la coordonnee x de la case.
	 * @param y la coordonnee y de la case.
	 * @return le nombre en <code>int</code> de points sur cette case.
	 */
	public int hitCount(int x, int y) {
		if(x < 0 || x>width() || y < 0 || y>height())
			throw new IndexOutOfBoundsException("Coordonnées invalides!");
		return flameAcc[y][x];
	}
	
	/**
	 * <p>La classe <code>FlameAccumulator.Builder</code> est une classe imbriquee qui permet de batir un {@link FlameAccumulator} de maniere incrementale.</p>
	 * @see FlameTransformation
	 * @see FlameTransformation.Builder
	 */
	public static class Builder {
		private Rectangle frame;
		private int [][] flameAcc;
		private double[][] colorIndexSum;
		private int height;
		private AffineTransformation t;
		
		/**
		 * Construit un batisseur d'accumulateur pour la region du plan delimitee par le cadre {@link Rectangle frame}, de largeur width et de hauteur height (en nombre de cases). 
		 * @param frame le cadre qui delimite la region dans laquelle on genere la fractale.
		 * @param width la largeur de l'accumulateur en cases.
		 * @param height la hauteur de l'accumulateur en cases.
		 * @throws IllegalArgumentException si la largeur ou la hauteur sont negatives ou nulles.
		 */
		public Builder(Rectangle frame, int width, int height) {
			this.height = height;
			t = AffineTransformation.newScaling(width/frame.width(), height/frame.height()).composeWith(AffineTransformation.newTranslation(-frame.left(), -frame.bottom()));
			if(width <= 0 || height <= 0)
				throw new IllegalArgumentException("La largeur et la hauteur doivent être strictement positives !");
			this.frame = new Rectangle(frame.center(), frame.width(), frame.height());
			this.flameAcc = new int [height][width];
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < width; j++) {
					flameAcc[i][j] = 0;
				}
			}
			
			this.colorIndexSum = new double [height][width];
			for(int i=0; i<height; i++) {
				for(int j=0; j<width; j++) {
					colorIndexSum[i][j] = 0;
				}
			}
		}
		
		/**
		 * Incremente le compteur de la case correspondant au point p donne. Ne fait rien si le point est en dehors du cadre de l'accumulateur (c-a-d le parametre {@link Rectangle frame} passe au constructeur).
		 * Pour placer le point correctement, on fait une transformation composee d'une mise a l'echelle au rectangle frame, et d'une translation sur son systeme de coordonnees.
		 * On remplit egalement l'accumulateur dans le meme sens que le plan, c-a-d en partant du bas et non du haut (on utilise <code>height-y</code> a la place de <code>y</code>).
		 * @param p le point a placer sur l'accumulateur.
		 * @param c l'index de couleur associe.
		 * @see Point
		 * @see Rectangle#contains(Point)
		 * @see AffineTransformation
		 * @see AffineTransformation#transformation(Point)
		 * @see AffineTransformation#newScaling(double, double)
		 * @see AffineTransformation#newTranslation(double, double)
		 * @see AffineTransformation#composeWith(AffineTransformation)
		 */
		public void hit(Point p, double c) {
			if(frame.contains(p)) {
				p = t.transformPoint(p); 
				int y = (int) (height-p.y());
				int x = (int) p.x();
				flameAcc[y][x]++;
				colorIndexSum[y][x] += c;
			}
		}
		
		/**
		 * Retourne un accumulateur contenant les points collectes jusqu'a present, avec la somme des index de couleurs.
		 * @return le <code>FlameAccumulator</code> construit.
		 * @see FlameAccumulator
		 * @see FlameAccumulator#FlameAccumulator(int[][], double[][])
		 */
		public FlameAccumulator build() {
			return new FlameAccumulator(flameAcc, colorIndexSum);
		}
	}
}
