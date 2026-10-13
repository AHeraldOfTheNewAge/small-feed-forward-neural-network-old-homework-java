import java.util.*;
import java.io.*;

class smallnn {
	public static String citire_teste(int linie) {
		{
			String temporar = "";

			try {
				File file = new File("teste.txt");
				Scanner sc = new Scanner(file);
				
				for (int i = 0; i < linie; i++) {
					temporar = sc.nextLine();
				}
			
				return temporar;
			}

			catch (Exception e) {
				System.out.println("nu a mers");
				return ("0000000000000000000000000");
			}
		}
	}

	public static void functiune() {
		String temporar = "";

		try {
			File file = new File("sabloane.txt");
			Scanner sc = new Scanner(file);
			
			for (int i = 0; i < 4; i++) {
				temporar = sc.nextLine();
		
				for (int j = 0; j < 25; j++) {
					sabloaneIn[i][j] = Character.getNumericValue(temporar.charAt(j));
				}
			}
		} catch (Exception e) {
			System.out.println("nu a mers");
		}
	}

	static final int DIMENSIUNE = 5;

	// dimensiunea matricii patrate de pixeli,aplicate pe intrari
	static final int NR_NEURONI_IN = DIMENSIUNE * DIMENSIUNE;
	static final int NR_NEURONI_OUT = 4;
	static final int NR_NEURONI_HIDDEN = 20;
	static final int NR_TOTAL_SABLOANE = 4;
	
	// cate 3 sabloane, pentru fiecare orientare.
	static final double EROARE_IMPUSA = 0.01;
	static final int NR_MAXIM_EPOCI = 40000;
	static double w1[][] = new double[NR_NEURONI_HIDDEN][NR_NEURONI_IN];
	static double w2[][] = new double[NR_NEURONI_OUT][NR_NEURONI_HIDDEN];
	
	// sabloaneIn – contine intrarile a 12 sabloane fiecare avand cate
	// 25 de valori binare. Este deci un array ce contine 12 array-uri:
	static int sabloaneIn[][] = {
		{ // 1 -- aici va fi +
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0 
		},
		{ // 2 -- aici va fi -
			0, 0, 0, 0, 0,
		  	0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0 
		}, 
		{ // 3 -- aici va fi x
			0, 0, 0, 0, 0,
		 	0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0 
		}, 
		{ // 4 -- aici va fi / in total vor fi 4 forme de recunoscut
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, 0, 0, 0 
		},

	};

	// sabloaneOut – contine iesirile a 12 sabloane, fiecare avand cate
	// 4 valori (corespunzatoare celor 4 orientari):
	static int sabloaneOut[][] = {
		{ 1, 0, 0, 0 },
		{ 0, 1, 0, 0 },
		{ 0, 0, 1, 0 },
		{ 0, 0, 0, 1 },
	};

	public static void main(String args[]) {
		functiune();
		
		boolean converge;
		
		// antrenare retea:
		initPonderi();
		converge = antrenare();
		
		if (converge == false) {
			System.out.println("Nu a convers in nr. de epoci specificat !");
		} else {
			System.out.println("A convers. !");
			testare();
		}
	}

	private static void initPonderi() {
		int i, j;
		Random r = new Random();

		for (i = 0; i < NR_NEURONI_HIDDEN; i++) { // initializare primul strat de ponderi:
			for (j = 0; j < NR_NEURONI_IN; j++) {
				w1[i][j] = r.nextDouble() - 0.5;
			}
		}
		
		for (i = 0; i < NR_NEURONI_OUT; i++) { // initializare stratul al doilea de ponderi:
			for (j = 0; j < NR_NEURONI_HIDDEN; j++) {
				w2[i][j] = r.nextDouble() - 0.5;
			}
		}
	}

	private static boolean antrenare() {
		double yHidden[] = new double[NR_NEURONI_HIDDEN];
		double yOut[] = new double[NR_NEURONI_OUT];
		double eroareIesireSablonCrt[] = new double[NR_TOTAL_SABLOANE];
		double eroareTotala;
		int nrEpoci = 0;
		boolean converge = false;
		boolean stop = false;
		int i, ex;

		while (!stop) {
			for (ex = 0; ex < NR_TOTAL_SABLOANE; ex++) {
				calculIesiriNeuroni(yHidden, yOut, ex);

				eroareIesireSablonCrt[ex] = 0;
				
				for (i = 0; i < NR_NEURONI_OUT; i++) {
					eroareIesireSablonCrt[ex] += 0.5 * (yOut[i] - sabloaneOut[ex][i]) * (yOut[i] - sabloaneOut[ex][i]);
				}

				modificaPonderi(yOut, yHidden, ex);
			} // for ex

			nrEpoci++;
			eroareTotala = 0;

			for (ex = 0; ex < NR_TOTAL_SABLOANE; ex++) {
				eroareTotala += eroareIesireSablonCrt[ex];
			}
		
			System.out.println("Epoca nr: " + nrEpoci + " eroare totala= " + eroareTotala);
			
			if (eroareTotala <= EROARE_IMPUSA) {
				converge = true;
				stop = true;
			}

			if (nrEpoci > NR_MAXIM_EPOCI) {
				stop = true;
			}
		}

		return converge;
	}

	private static void calculIesiriNeuroni(double yHidden[], double yOut[], int ex) {
		int i, j;
		double xTotal_i;

		for (i = 0; i < NR_NEURONI_HIDDEN; i++) {
			xTotal_i = 0;

			for (j = 0; j < NR_NEURONI_IN; j++) {
				xTotal_i = xTotal_i + sabloaneIn[ex][j] * w1[i][j];
			}

			yHidden[i] = 1 / (1 + Math.exp(-xTotal_i));
		}

		for (i = 0; i < NR_NEURONI_OUT; i++) {
			xTotal_i = 0;

			for (j = 0; j < NR_NEURONI_HIDDEN; j++) {
				xTotal_i = xTotal_i + yHidden[j] * w2[i][j];
			}

			yOut[i] = 1 / (1 + Math.exp(-xTotal_i));
		}
	}

	private static void modificaPonderi(double yOut[], double yHidden[], int ex) {
		double deltaOut[] = new double[NR_NEURONI_OUT];
		double deltaHidden[] = new double[NR_NEURONI_HIDDEN];
		int i, j, k;
		double sumaPonderataDeltaFii;
		double alfa = 1.0;
		double eta = 1.0;

		for (i = 0; i < NR_NEURONI_OUT; i++) {
			deltaOut[i] = (yOut[i] - sabloaneOut[ex][i]) * (1 - yOut[i]) * yOut[i];
		}

		for (i = 0; i < NR_NEURONI_OUT; i++) {
			for (j = 0; j < NR_NEURONI_HIDDEN; j++) {
				w2[i][j] = alfa * w2[i][j] - eta * deltaOut[i] * yHidden[j];
			}
		}
		
		for (i = 0; i < NR_NEURONI_HIDDEN; i++) {
			sumaPonderataDeltaFii = 0;

			for (j = 0; j < NR_NEURONI_OUT; j++) {
				sumaPonderataDeltaFii += w2[j][i] * deltaOut[j];
			}

			deltaHidden[i] = sumaPonderataDeltaFii * (1 - yHidden[i]) * yHidden[i];
		}

		for (i = 0; i < NR_NEURONI_HIDDEN; i++) {
			for (j = 0; j < NR_NEURONI_IN; j++) {
				w1[i][j] = alfa * w1[i][j] - eta * deltaHidden[i] * sabloaneIn[ex][j];
			}
		}
	} // modificaPonderi

	private static void testare() {
		int i, j;
		// iesiri neuroni din strat hidden:
		double yHidden[] = new double[NR_NEURONI_HIDDEN];
		// iesiri neuroni din stratul aut (cele calculate):
		double yOut[] = new double[NR_NEURONI_OUT];
		// intrarile pt. testarea retelei:
		int inTest[] = new int[NR_NEURONI_IN];
		double xTotal_i;
		Scanner sc = new Scanner(System.in);

		System.out.println(" ");
		
		int alegere;
		
		for (;;) {
			System.out.println(
					"Doriti sa introduceti pixelii manual sau dintr-un fisier cu teste? apasati 1 pentru manual 2 pentru teste: .. ");
			
			alegere = sc.nextInt();
			
			if (alegere == 1) {
				System.out.println("Ati ales sa introduceti pixelii manual.. ");
				break;
			}

			if (alegere == 2) {
				System.out.println("Ati ales sa introduceti pixelii din fisierul de teste.. ");
				break;
			}
		}
		
		if (alegere == 1) {
			System.out.println("Dati cele " + NR_NEURONI_IN + " valori binare de pe intrari: ");
			
			for (i = 0; i < NR_NEURONI_IN; i++) {
				if (i % DIMENSIUNE == 0) {
					System.out.print("\n\n");
				}

				System.out.print("Valoarea pixelului " + i + " : ");
				
				inTest[i] = sc.nextInt();
			}
		}
		
		if (alegere == 2) {
			System.out.println("introduceti linia din fisier de pe care doriti sa incarcati testul");
	
			int linie;
	
			linie = sc.nextInt();
	
			System.out.println("Ati ales linia" + linie);
	
			String teste_din_fisier = citire_teste(linie);
	
			for (int jjj = 0; jjj < teste_din_fisier.length(); jjj++) {
				inTest[jjj] = Character.getNumericValue(teste_din_fisier.charAt(jjj));
			}
		}
		
		for (i = 0; i < NR_NEURONI_HIDDEN; i++) {
			xTotal_i = 0;

			for (j = 0; j < NR_NEURONI_IN; j++) {
				xTotal_i = xTotal_i + inTest[j] * w1[i][j];
			}

			yHidden[i] = 1 / (1 + Math.exp(-xTotal_i));
		}
		
		for (i = 0; i < NR_NEURONI_OUT; i++) {
			xTotal_i = 0;
			
			for (j = 0; j < NR_NEURONI_HIDDEN; j++) {
				xTotal_i = xTotal_i + yHidden[j] * w2[i][j];
			}

			yOut[i] = 1 / (1 + Math.exp(-xTotal_i));
		}

		System.out.println("+ : " + yOut[0]);
		System.out.println("- : " + yOut[1]);
		System.out.println("x : " + yOut[2]);
		System.out.println("/ : " + yOut[3]);
	}
}