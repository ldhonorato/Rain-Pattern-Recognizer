package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class OrganizadorResultados {
	public static String CABECALHO_CSV = "Dia;Mes;Ano;Mb3;Mb2;Mb1;Mb0;"
			+ "T0;T1;T2;T3;T4;T5;T6;T7;T8;T9;T10;T11;T12;T13;T14;T15;T16;T17;T18;T19;T20;T21;T22;T23;"
			+ "U0;U1;U2;U3;U4;U4;U6;U7;U8;U9;U10;U11;U12;U13;U14;U15;U16;U17;U18;U19;U20;U21;U22;U23;"
			+ "LabelChuva;ResultadoClassificador";
	
	public static String CABECALHO_CSV_DIA = "Dia;Mes;Ano;Mb3;Mb2;Mb1;Mb0;"
			+ "T9;T10;T11;T12;T13;T14;T15;T16;T17;T18;"
			+ "U9;U10;U11;U12;U13;U14;U15;U16;U17;U18;"
			+ "LabelChuva;ResultadoClassificador";
	
	public static String CABECALHO_CSV_NOITE = "Dia;Mes;Ano;Mb3;Mb2;Mb1;Mb0;"
			+ "T19;T20;T21;T22;T23;T0;T1;T2;T3;T4;T5;T6;T7;T8;"
			+ "U19;U20;U21;U22;U23;U0;U1;U2;U3;U4;U4;U6;U7;U8;"
			+ "LabelChuva;ResultadoClassificador";

	public void ImprimirEvaluation(String pathSaida, String nomeArquivo,
			Evaluation eval) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(pathSaida + "//"
				+ nomeArquivo));

		out.write("\n");
		out.write(eval.toSummaryString());
		out.write("\n");
		out.write("TP chuva: " + (eval.truePositiveRate(0) * 100) + " %");
		out.write("\n");
		out.write("TP não chuva: " + (eval.truePositiveRate(1) * 100) + " %");
		out.write("\n");
		out.write("F-Measure chuva: " + eval.fMeasure(0));
		out.write("\n");
		out.write("F-Measure não-chuva: " + eval.fMeasure(1));
		out.write("\n");
		
		// Get the confusion matrix
		out.write("------------Matriz de confusão-----------------");
		out.write("\n");
		out.write("    0    1");// 0 é sim, 1 é não
		out.write("\n");
		double[][] cmMatrixComplete = eval.confusionMatrix();
		for (int row_i = 0; row_i < cmMatrixComplete.length; row_i++) {
			out.write(row_i + ": ");
			for (int col_i = 0; col_i < cmMatrixComplete.length; col_i++) {
				out.write(cmMatrixComplete[row_i][col_i] + "");
				out.write("|");
			}
			out.write("\n");
		}

		out.close();
	}

	public void GerarArquivoClassificacao(AbstractClassifier classificado,
			Instances dataCompleto, Instances dataRemovido, String pathSaida,
			String nomeArquivo, TipoEntrada tipoConjunto) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(pathSaida + "//"
				+ nomeArquivo));
		
		switch(tipoConjunto)
		{
			case CONJUNTO_COMPLETO:
				out.write(CABECALHO_CSV);
				break;
			case APENAS_DIA:
				out.write(CABECALHO_CSV_DIA);
				break;
			case APENAS_NOITE:
				out.write(CABECALHO_CSV_NOITE);
				break;
		}
		
		out.write("\n");
		for (int i = 0; i < dataCompleto.size(); i++) {
			out.write(dataCompleto.get(i).toString().replace(',',';'));

			double pred = classificado.classifyInstance(dataRemovido
					.instance(i));
			
			if(pred == 0)
			{
				out.write(";Sim");
			}
			else
			{
				out.write(";Nao");
			}
			out.write("\n");

		}
		out.close();
	}
	
	static public double calcularMedia(double [] valores)
	{
		double media = 0;
				
		for(int i = 0; i< valores.length; i++)
		{
			media += valores[i];
		}
		
		return (media / valores.length);
	}
	
	static public double calularDesvioPadrao(double [] valores)
	{
		double desvioPadrao = 0;
		double media = calcularMedia(valores);
		double diferenca;
		
		for(int i = 0; i < valores.length; i++)
		{
			diferenca = valores[i] - media;
			desvioPadrao = desvioPadrao + (diferenca * diferenca);
		}
		
		desvioPadrao = desvioPadrao / valores.length;
		
		return Math.sqrt(desvioPadrao);
	}

}
