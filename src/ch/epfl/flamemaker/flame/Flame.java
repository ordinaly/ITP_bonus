package ch.epfl.flamemaker.flame;

import java.util.*;
import ch.epfl.flamemaker.geometry2d.*;
/**
 * La classe Flame modélise les fractales <code>Flame</code>. <br />
 * Elle est equipe d'un seul champ prive et non modifiable <code><b>ftransformations</b></code> de type <code>List</code> contenant les <code>{@link FlameTransformation}</code> caracterisant la fractale.
 * @see FlameTransformation
 */
public final class Flame {
	private final List<FlameTransformation> ftransformations;
		/**
		 * construit une fractale Flame etant donne sa liste de transformations.
		 * @param ftransformations
		 */
	public Flame(List <FlameTransformation> ftransformations) {
		this.ftransformations = new ArrayList<FlameTransformation>(ftransformations);
	}

	
	/**
	 * <p>Calcule la fractale dans la region du plan delimitee par le cadre <code>{@link Rectangle frame}</code> et stocke le resultat dans un nouvel <code>{@link FlameAccumulator accumulateur}</code> de largeur width et de hauteur height, qui est retourne. <br />
	 * Utilise l'algorithme du chaos pour generer des points sur le cadre.</p>
	 * <p>La variable <code>m</code> represente le nombre de points a generer.</p>
	 * <p>Utilise la formule donnee pour calculer les index de couleurs incrementalement.</p> 
	 * @param frame le cadre qui delimite la region du plan dans laquelle on genere la fractale.
	 * @param width la largeur de l'accumulateur.
	 * @param height la hauteur de l'accumulateur.
	 * @param density modifie le nombre de points a generer.
	 * @see FlameAccumulator
	 * @see Rectangle
	 * @see Point
	 * @see FlameTransformation
	 * @see FlameAccumulator.Builder
	 * @see FlameAccumulator.Builder#hit(Point, double)
	 * @see FlameAccumulator.Builder#build()
	 * @return un accumulateur qui contient les points qui forment la fractale.
	*/
	public FlameAccumulator compute(Rectangle frame, int width, int height, int density) {
		FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame, width, height);
		double[] colorIndexTab = new double[ftransformations.size()];
		Point p = Point.ORIGIN;
		double c = 0;
		int m = density*width*height;
		Random randy = new Random(2013);
		colorIndexTab[0] = 0;
		for(int i=1; i<ftransformations.size(); i++) {
			if(i>1) {
				int log2 = (int) (Math.ceil(Math.log(i))/(Math.log(2)));
				colorIndexTab[i] = (i-((Math.pow(2, log2))/2.0))/(Math.pow(2, log2));
			} 
			else {
				colorIndexTab[i] = i;
			}
		}
		
		for(int i=0; i<m+20; i++) {
			if(ftransformations.size() < 1)
				throw new UnsupportedOperationException("Impossible de generer des points si la liste de transformations est vide !");
			int random = randy.nextInt(ftransformations.size());
			FlameTransformation t = ftransformations.get(random);
			c = (colorIndexTab[random] + c)/2.0;
			p = t.transformPoint(p);
			if(i>20)
				builder.hit(p, c);
		}
		
		return builder.build();
	}
	/**
	 * <p>La classe <code>Flame.Builder</code> est une classe imbriquee qui permet de batir une fractale <code>{@link Flame}</code> de maniere incrementale.</p>
	 * <p>Elle possede un attribut <code><b>ftransformationBuilders</b></code> de type <code>List</code> qui contient les {@link FlameTransformation.Builder builders de FlameTransformation} afin de pouvoir les modifier avant de creer la <code>Flame</code>
	 * @see FlameTransformation
	 * @see FlameTransformation.Builder
	 */
	public static class Builder {
		private List<FlameTransformation.Builder> ftransformationBuilders;
		
		/**
		 * Construit un <code>Flame.Builder</code> a partir d'une <code>{@link Flame}</code>. <br />
		 * Recupere les <code>FlameTransformation</code> pour les retransformer en <code>FlameTransformation.Builder</code> afin qu'elles soient modifiables dans le builder.  
		 * @param flame la <code>Flame</code> deja construite qu'on souhaite reconstruire.
		 * @see Flame
		 * @see FlameTransformation
		 * @see FlameTransformation.Builder
		 */
		public Builder(Flame flame) {
			ftransformationBuilders = new ArrayList<FlameTransformation.Builder>();
			for(FlameTransformation ft : flame.ftransformations) {
				ftransformationBuilders.add(new FlameTransformation.Builder(ft));
			}
		}
		
		/**
		 * Construit et retourne la fractale <code>Flame</code>, en builant les <code>{@link FlameTransformation}</code> dans une liste qui est passee en parametre du {@link Flame#Flame(List) constructeur de <code>Flame</code>}.
		 * @return la <code>Flame</code> construite a partir de ce Builder.
		 * @see Flame
		 * @see Flame#Flame(List)
		 * @see FlameTransformation
		 * @see FlameTransformation.Builder
		 * @see FlameTransformation.Builder#build()
		 */
		public Flame build() {
			ArrayList<FlameTransformation> a = new ArrayList<FlameTransformation>();
			for(FlameTransformation.Builder b : ftransformationBuilders) {
				a.add(b.build());
			}
			return new Flame(a);
		}
		
		/**
		 * retourne le nombre de transformations <code>FlameTransformation</code> caracterisant la fractale en cours de construction.
		 * @return le nombre en <code>int</code> de <code>FlameTransformation</code> de cette <code>Flame</code>. 
		 * @see Flame
		 * @see FlameTransformation
		 */
		public int transformationCount() {
			return ftransformationBuilders.size();
		}
		
		/**
		 * Ajoute la transformation <code>{@link FlameTransformation}</code> a la liste de <code>{@link FlameTransformation.Builder}</code>, en derniere position.
		 * @param transformation la transformation a ajouter a la liste de <code>FlameTransformation</code>.
		 */
		public void addTransformation(FlameTransformation transformation) {
			ftransformationBuilders.add(new FlameTransformation.Builder(transformation));			
		}
		
		/**
		 * Retourne la composante affine de la transformation <code>{@link FlameTransformation}</code> d'index donne.
		 * @param index l'index de la <code>FlameTransformation</code>.
		 * @return la composante <code>AffineTransformation</code> de la <code>FlameTransformation</code> d'index donne.
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 * @see Flame.Builder#checkIndex(int)
		 * @see AffineTransformation
		 */
		public AffineTransformation affineTransformation(int index) {
			checkIndex(index);
			return ftransformationBuilders.get(index).getAffineTransformation();
		}
		
		/**
		 * Change la composante affine de la transformation <code>{@link FlameTransformation} d'index donne.
		 * @param index l'index de la <code>FlameTransformation.
		 * @param newTransformation la nouvelle <code>AffineTransformation</code> qu'on veut inserer.
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 * @see Flame.Builder#checkIndex(int)
		 * @see AffineTransformation
		 */
		public void setAffineTransformation(int index, AffineTransformation newTransformation) {
			checkIndex(index);
			ftransformationBuilders.get(index).setAffineTransformation(newTransformation);
		}
		
		/**
		 * Retourne le poids de la variation donnee pour la transformation <code>{@link FlameTransformation}</code> d'index donne.
		 * @param index l'index de la <code>FlameTransformation</code>.
		 * @param variation la <code>Variation</code> dont on cherche le poids.
		 * @return le poids en <code>double</code> associe a la <code>Variation</code> donnee.
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 * @see Flame.Builder#checkIndex(int)
		 * @see Variation
		 * @see Variation#getIndex()
		 */
		public double variationWeight(int index, Variation variation) {
			checkIndex(index);
			return ftransformationBuilders.get(index).getVariationWeight(variation.getIndex());
		}
		
		/**
		 * Change le poids de la variation donnee pour la transformation <code>{@link FlameTransformation}</code> d'index donne.
		 * @param index l'index de la <code>FlameTransformation</code> dont on veut changer un des poids
		 * @param variation la <code>Variation</code> dont on veut changer le poids.
		 * @param newWeight le nouveau <code>double</code> poids qu'on veut inserer.
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 * @see Flame.Builder#checkIndex(int)
		 * @see Variation
		 * @see Variation#getIndex()
		 */
		public void setVariationWeight(int index, Variation variation, double newWeight) {
			checkIndex(index);
			ftransformationBuilders.get(index).setNewWeight(variation.getIndex(), newWeight);
		}
		
		/**
		 * Supprime la transformation <code>{@link FlameTransformation}</code> d'index donne.
		 * @param index l'index de la <code>FlameTransformation</code> a supprimer.
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 * @see Flame.Builder#checkIndex(int)
		 */
		public void removeTransformation(int index) {
			checkIndex(index);
			ftransformationBuilders.remove(index);
		}
		
		/**
		 * Verifie si l'index donne est valide pour les methodes de <code>Flame.Builder</code>
		 * @throws IndexOutOfBoundsException si l'index est negatif ou superieur a la taille de la liste de <code>FlameTransformation</code>.
		 */
		public void checkIndex(int index) {
			if(index < 0 || index > ftransformationBuilders.size())
				throw new IndexOutOfBoundsException("L'index n'est pas valide !");
		}
		
		public List<FlameTransformation> getTransfoListCopy() {
			List<FlameTransformation> listCopy = new ArrayList<FlameTransformation>();
			for(FlameTransformation.Builder b : ftransformationBuilders) {
				listCopy.add(b.build());
			}
			return listCopy;
		}
	}
}
