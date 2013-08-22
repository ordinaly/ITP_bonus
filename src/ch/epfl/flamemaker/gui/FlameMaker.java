package ch.epfl.flamemaker.gui;
/**
 * La classe FlameMaker posséde la main du programme.
 * <p> Cette methode cree une instance de la classe FlameMakerGUI puis appelle sa methode start afin de construire et demarrer l'interface utilisateur.<p>
 * @author cherifyasmine
 *
 */
public class FlameMaker {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new FlameMakerGUI().start();
			}
		});
	}
}
