package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;


public class ValidacaoCruzada {

	public void ExecutarValidacaoCruzada(AbstractClassifier classificador, Instances data, String pathSaida,String nomeArquivo, int folds, int runs) throws Exception
	{
		double [] array_TP_rain = new double[runs];
		double [] array_TP_NO_rain = new double[runs];
		String path = pathSaida + "//" + nomeArquivo;
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		
		for (int i = 0; i < runs; i++) {
			// randomize data
			int seed = i + 1;
			Random rand = new Random(seed);
			Instances randData = new Instances(data);
			randData.randomize(rand);
			if (randData.classAttribute().isNominal())
				randData.stratify(folds);

			Evaluation evalTreinamento = new Evaluation(randData);

			MultilayerPerceptron clsCopy = null;
			for (int n = 0; n < folds; n++) {
				Instances train = randData.trainCV(folds, n);
				Instances test = randData.testCV(folds, n);
				// the above code is used by the StratifiedRemoveFolds
				// filter, the
				// code below by the Explorer/Experimenter:
				// Instances train = randData.trainCV(folds, n, rand);

				// build and evaluate classifier
				clsCopy = (MultilayerPerceptron)AbstractClassifier.makeCopy(classificador);
				clsCopy.buildClassifier(train);
				evalTreinamento.evaluateModel(clsCopy, test);
			}
			
			array_TP_rain[i] = evalTreinamento.truePositiveRate(0);
			array_TP_NO_rain[i] = evalTreinamento.truePositiveRate(1);
			
			System.out.println(evalTreinamento.truePositiveRate(0) +
					";" + evalTreinamento.truePositiveRate(1) +
					";" + evalTreinamento.fMeasure(0) +
					";" + evalTreinamento.fMeasure(1));
			out.write("\n");
			out.write("=== Setup run " + (i + 1) + " ===");
			out.write("\n");
			out.write("Classifier: Naive Bayes" );
			out.write("\n");
			out.write("Folds: " + folds);
			out.write("\n");
			out.write("Seed: " + seed);
			out.write("\n");
			out.write(evalTreinamento.toSummaryString("=== " + folds
					+ "-fold Cross-validation run " + (i + 1) + "===",
					false));
			out.write("\n");
			out.write("TP chuva: "
					+ (evalTreinamento.truePositiveRate(0) * 100) + " %");
			out.write("\n");
			out.write("TP não chuva: "
					+ (evalTreinamento.truePositiveRate(1) * 100) + " %");
			out.write("\n");
			// Get the confusion matrix
			out.write("------------Matriz de confusão-----------------");
			out.write("\n");
			out.write("    0    1");// 0 é sim, 1 é não
			out.write("\n");
			double[][] cmMatrixComplete = evalTreinamento.confusionMatrix();
			for (int row_i = 0; row_i < cmMatrixComplete.length; row_i++) {
				out.write(row_i + ": ");
				for (int col_i = 0; col_i < cmMatrixComplete.length; col_i++) {
					out.write(cmMatrixComplete[row_i][col_i] + "");
					out.write("|");
				}
				out.write("\n");
			}
		}
		System.out.println("TP RAIN: " + OrganizadorResultados.calcularMedia(array_TP_rain) + " (" + OrganizadorResultados.calularDesvioPadrao(array_TP_rain) + ")");
		System.out.println("TP NO RAIN: " + OrganizadorResultados.calcularMedia(array_TP_NO_rain)+ " (" + OrganizadorResultados.calularDesvioPadrao(array_TP_NO_rain) + ")");
		out.close();
	}
}
