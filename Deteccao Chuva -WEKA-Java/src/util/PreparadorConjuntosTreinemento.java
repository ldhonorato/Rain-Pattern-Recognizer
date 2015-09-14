package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

public class PreparadorConjuntosTreinemento {
	private Instances conjuntoTreinamento;
	private Instances conjuntoTeste;
	private String informacoes;
	private int chuva_teste, naoChuva_teste, chuva_treino, naoChuva_treino;
	private String pathSaida;
	
	public PreparadorConjuntosTreinemento(String pathARFFTreino, String pathARFFTeste) throws Exception
	{
		BufferedReader reader = new BufferedReader(new FileReader(pathARFFTreino));
		conjuntoTreinamento = new Instances(reader);
		reader.close();
		// setting class attribute
		conjuntoTreinamento.setClassIndex(conjuntoTreinamento.numAttributes() - 1);
		
		reader = new BufferedReader(new FileReader(pathARFFTeste));
		conjuntoTeste = new Instances(reader);
		reader.close();
		// setting class attribute
		conjuntoTeste.setClassIndex(conjuntoTeste.numAttributes() - 1);
		
		contarExemplos();
		
		informacoes += "Instancias para Treino:" + conjuntoTreinamento.size() + " Classe Chuva: " + chuva_treino + " Classe Não chuva: "	+ naoChuva_treino + "\n";
		informacoes += "Instancias para Teste:" + conjuntoTeste.size()	+ " Classe Chuva: " + chuva_teste + " Classe Não chuva: "+ naoChuva_teste + "\n";
		informacoes += "================================================================";
		System.out.println(informacoes);
	}

	private void contarExemplos() {
		chuva_teste = 0;
		naoChuva_teste = 0;
		for (int i = 0; i < conjuntoTeste.size(); i++) {
			if (conjuntoTeste.get(i).classValue() == 0) {
				chuva_teste++;
			} else {
				naoChuva_teste++;
			}
		}

		chuva_treino = 0; 
		naoChuva_treino = 0;
		for (int i = 0; i < conjuntoTreinamento.size(); i++) {
			if (conjuntoTreinamento.get(i).classValue() == 0) {
				chuva_treino++;
			} else {
				naoChuva_treino++;
			}
		}
	}
	
	public PreparadorConjuntosTreinemento(String pathARFF, int percentualTreino, int seed) throws Exception
	{
		BufferedReader reader = new BufferedReader(new FileReader(pathARFF));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		Random rand = new Random(seed);
		data.randomize(rand);//embaralha os exemplos
		
		RemovePercentage filtroRemocao = new RemovePercentage();
		filtroRemocao.setPercentage(75.0); // 75% do conjunto para treinamento
		filtroRemocao.setInvertSelection(true);
		filtroRemocao.setInputFormat(data);
		conjuntoTreinamento = Filter.useFilter(data, filtroRemocao);
		filtroRemocao.setInvertSelection(false);
		filtroRemocao.setInputFormat(data);
		conjuntoTeste = Filter.useFilter(data, filtroRemocao);

		contarExemplos();
		
		informacoes = "Total de instancias:" + data.size() + "\n";
		informacoes += "Instancias para Treino:" + conjuntoTreinamento.size() + " Classe Chuva: " + chuva_treino + " Classe Não chuva: "	+ naoChuva_treino + "\n";
		informacoes += "Instancias para Teste:" + conjuntoTeste.size()	+ " Classe Chuva: " + chuva_teste + " Classe Não chuva: "+ naoChuva_teste + "\n";
		informacoes += "================================================================";
		System.out.println(informacoes);
	}

	public Instances getConjuntoTreinamento() {
		return conjuntoTreinamento;
	}

	public Instances getConjuntoTeste() {
		return conjuntoTeste;
	}

	public String getInformacoes() {
		return informacoes;
	}

	public int getChuva_teste() {
		return chuva_teste;
	}

	public int getNaoChuva_teste() {
		return naoChuva_teste;
	}

	public int getChuva_treino() {
		return chuva_treino;
	}

	public int getNaoChuva_treino() {
		return naoChuva_treino;
	}

	public String getPathSaida() {
		return pathSaida;
	}

	public void setPathSaida(String pathSaida) {
		this.pathSaida = pathSaida;
	}
	
}
