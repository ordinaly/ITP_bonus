package ch.epfl.flamemaker.flame;

import java.io.Serializable;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * <p>La classe FlameTransformation modelise une transformation flame c-a-d la composition ponderee d'une transformation affine avec plusieurs variations.</p>
 * <p>Elle est caracterisee par deux champs: <br /> 
 * <code><b>affineTransformation</b></code> de type <code>AffineTransformation</code>, contient la composante affine. <br />
 * un tableau <code><b>variationWeight</b> de double []</code> des poids des differentes variations.</p>
 * @see AffineTransformation
 */
public final class FlameTransformation implements Transformation, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8430725525782008060L;
	private final AffineTransformation affineTransformation;
	private final double[] variationWeight;
	
	/**
	 * Constructeur de la classe FlameTransformation
	 * @param affineTransformation la transformation affine qui compose la <code>FlameTransformation</code>
	 * @param variationWeight le tableau de poids associe a la transformation
	 * @throws IllegalArgumentException si la taille du tableau de poids est differente du nombre de variations
	 * @see AffineTransformation
	 * @see Variation
	 */
	public FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight){
		this.affineTransformation=affineTransformation;
		if(variationWeight.length != Variation.ALL_VARIATIONS.size())
			throw new IllegalArgumentException("La taille des poids est diffŽrente de celle des variations!");
		this.variationWeight = variationWeight;
	}
	
	/**
	 * Retourne le point p transforme selon la formule <code>Fi(x,y)=·j=05wi,j*Vj(Gi(x,y))</code>.
	 * @param p le point a transformer
	 * @return le point transforme
	 * @see Point
	 * @see Variation
	 */
	@Override
	public Point transformPoint(Point p) {
		double wx = 0, wy = 0;
		for(int j=0; j<Variation.ALL_VARIATIONS.size(); j++){
			Point p2 = Variation.ALL_VARIATIONS.get(j).transformPoint(affineTransformation.transformPoint(p));
			wx += variationWeight[j]*p2.x();
			wy += variationWeight[j]*p2.y();
		}
		return new Point(wx, wy);
	}
	
	/**
	 * La classe <code>FlameTransformation.Builder</code> est une classe imbriquee qui permet de batir une <code>FlameTransformation</code> de maniere incrementale.
	 */
	/**
	 * @author Whity
	 *
	 */
	/**
	 * @author Whity
	 *
	 */
	public static class Builder {
		private AffineTransformation affineTransformation;
		private double[] variationWeight;
		
		public Builder(FlameTransformation f) {
			affineTransformation = f.affineTransformation;
			variationWeight = f.variationWeight.clone();
		}
		
		/**
		 * Construit et retourne la transformation <code>FlameTransformation</code> du builder.	
		 * @return la <code>FlameTransformation</code> cree a partir de ce builder
		 * @see FlameTransformation
		 * @see FlameTransformation#FlameTransformation(AffineTransformation, double[])
		 */
		public FlameTransformation build() {
			return new FlameTransformation(affineTransformation, variationWeight);
		}
		
		/**
		 * Retourne la transformation affine qui compose cette <code>FlameTransformation</code> 
		 * @return la composante <code>AffineTransformation</code> de cette <code>FlameTransformation</code>.
		 * @see FlameTransformation
		 * @see AffineTransformation
		 */
		public AffineTransformation getAffineTransformation() {
			return affineTransformation;
		}
			
		
		/**
		 * Change la composante <code>AffineTransformation</code> de cette <code>FlameTransformation</code>.
		 * @param newTransformation la transformation a inserer$
		 * @throws IllegalArgumentException si la <code>AffineTransformation</code> passee en parametre n'a pas ete initialisee
		 */
		public void setAffineTransformation(AffineTransformation newTransformation) {
			if(newTransformation == null)
				throw new IllegalArgumentException("La transformation affine n'a pas été initalisée !");
			affineTransformation = newTransformation;
		}
		
		
		/**
		 * Retourne le poids de la <code>FlameTransformation</code> a l'index donne.
		 * @param index l'index du poids.
		 * @return le poids de la <code>FlameTransformation</code> a l'index donne.
		 */
		public double getVariationWeight(int index) {
			return variationWeight[index];
		}
		
		
		/**
		 * Change le poids de la <code>FlameTransformation</code> a l'index donne
		 * @param index l'index du poids a modifier
		 * @param newWeight le poids qu'on veut inserer
		 */
		public void setNewWeight(int index, double newWeight) {
			variationWeight[index] = newWeight; 
		}
	}
}
