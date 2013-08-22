package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.color.*;
import ch.epfl.flamemaker.geometry2d.*;

public class FlamePPMMaker {
	public static void main(String[] args) {
		
		/*
		 * InterpolatedPalette
		 */
		 ArrayList<Color> list = new ArrayList<Color>();
		 list.add(Color.RED);
		 list.add(Color.GREEN);
		 list.add(Color.BLUE);
		 InterpolatedPalette pal = new InterpolatedPalette(list);
		 
		/*
		 * RandomPalette
		 */
		//RandomPalette pal = new RandomPalette(10);
		 
		/*
		 * TESTS colorForIndex()
		 System.out.println(pal.colorForIndex(0));
		 System.out.println(pal.colorForIndex(0.5));
		 System.out.println(pal.colorForIndex(1));
		 System.out.println(pal.colorForIndex(0.75));
		 System.out.println(pal.colorForIndex(0.65));
		*/
		 
		
		ArrayList<FlameTransformation> sharkFin = new ArrayList<FlameTransformation>();
		sharkFin.add(new FlameTransformation(new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8), new double[]{1, 0.1, 0, 0, 0, 0}));
		sharkFin.add(new FlameTransformation(new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), new double[]{0, 0, 0, 0, 0.8, 1}));
		sharkFin.add(new FlameTransformation(new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), new double[]{1, 0, 0, 0, 0, 0}));
		FlameAccumulator shark = new Flame(sharkFin).compute(new Rectangle(new Point(-0.25, 0.0), 5, 4), 500, 400, 50);
		System.out.println("Shark!");
		FlamePPMMaker.generateFractal(shark, pal, "shark-fin");

		 
		ArrayList<FlameTransformation> turbulence = new ArrayList<FlameTransformation>();
		turbulence.add(new FlameTransformation(new AffineTransformation(0.712487, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), new double[]{0.5, 0, 0, 0.4, 0, 0}));
		turbulence.add(new FlameTransformation(new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3), new double[]{1, 0, 0.1, 0, 0, 0}));
		turbulence.add(new FlameTransformation(new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3), new double[]{1, 0, 0, 0, 0, 0}));
		FlameAccumulator turbul = new Flame(turbulence).compute(new Rectangle(new Point(0.1, 0.1), 3, 3), 500, 500, 50);
		System.out.println("Turbulence!");
		FlamePPMMaker.generateFractal(turbul, pal, "turbulence");
		
		/*
		 * Fractale custom
		ArrayList<FlameTransformation> turbushark = new ArrayList<FlameTransformation>();
		turbushark.add(new FlameTransformation(new AffineTransformation(0.712487, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), new double[]{0.5, 0, 0.2, 0.4, 0, 1}));
		turbushark.add(new FlameTransformation(new AffineTransformation(-0.6113504, -0.3124804, -0.4, 0.7124795, -0.2113508, 0.8), new double[]{1, 0.1, 0, 0.3, 1, 0}));
		turbushark.add(new FlameTransformation(new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), new double[]{0, 0, 0, 0, 0.8, 1}));
		turbushark.add(new FlameTransformation(new AffineTransformation(0.7810169, 0, 1, 0.453280, 0.4810169, 0.9), new double[]{1, 0, 0, 0, 0, 0.5}));
		FlameAccumulator turbusharkAcc = new Flame(turbushark).compute(new Rectangle(new Point(-0.25, 0.0), 5, 4), 1000, 800, 50);
		System.out.println("CUSTOM!");
		FlamePPMMaker.generateFractal(turbusharkAcc, pal, "custom");
		*/
	}
	
	/**
	 * Cree un printStream dans lequel on insere les couleurs de chaque case de l'accumulateur. <br />
	 * Ce printstream ecrit les triplets de couleur des cases de l'accumulateur dans un fichier au format PPM. <br /> 
	 * On parcourt l'accumulateur, on recupere la couleur a la case parcourue et on l'affiche encodee grace a la methode <code>{@link Color#asPackedRGB() asPackedRGB()}</code> en des valeurs entre 0 et 100.
	 * @param acc l'accumulateur pour lequel on veut generer l'image.
	 * @param palette la palette de couleurs utilisee pour cette image.
	 * @param name le nom du fichier dans lequel on ecrit.
	 */
	public static void generateFractal(FlameAccumulator acc, Palette palette, String name) {
		PrintStream f = null;
		try {
			f = new PrintStream(name + ".ppm");
		} catch(FileNotFoundException e) {
			System.out.println("Fichier introuvable / impossible à créer !");
			e.printStackTrace();
		}
		
		f.println("P3");
		f.println(acc.width() + " " + acc.height());
		f.println("100");
		for(int i=0; i<acc.height(); i++) {
			for(int j=0; j<acc.width(); j++) {
				/*
				 * IFS
				 * f.print((int) (acc.intensity(j, i)*100) + " ");
				 * System.out.print((int) (acc.intensity(j, i)*100) + " ");
				 * System.out.print(acc.intensity(j, i));
				 */
				
				/*
				 * Flame
				 */
				Color c = acc.color(palette, Color.RED, j, i);
				f.print(Color.sRGBEncode(c.red(), 100) + " " + Color.sRGBEncode(c.green(), 100) + " " + Color.sRGBEncode(c.blue(), 100) + " ");
			}
			f.println();
			//System.out.println();
		}
		f.close();
	}
}
