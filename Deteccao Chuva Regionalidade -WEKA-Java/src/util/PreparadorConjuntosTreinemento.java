package util;

import java.io.BufferedReader;
import java.io.FileReader;
import weka.core.Instances;

public class PreparadorConjuntosTreinemento {
	static public final int QUANTIDADE_CONJUNTOS = 5;
	private Instances conjuntosDados[];
	private String informacoes[];
	private String pathSaida;
	
	public PreparadorConjuntosTreinemento(String pathsARFFs[]) throws Exception
	{
		conjuntosDados = new Instances[QUANTIDADE_CONJUNTOS];
		informacoes = new String[QUANTIDADE_CONJUNTOS];
		
		for (int i = 0; i < pathsARFFs.length; i++)
		{
			BufferedReader reader = new BufferedReader(new FileReader(pathsARFFs[i]));
			conjuntosDados[i] = new Instances(reader);
			conjuntosDados[i].setClassIndex(conjuntosDados[i].numAttributes() - 1);
			reader.close();
		}
		
		contarExemplos();


	}

	private void contarExemplos() {
		int chuva;
		int naoChuva;
		
		for (int i = 0; i < QUANTIDADE_CONJUNTOS; i++)
		{
			chuva = 0;
			naoChuva = 0;
			for (int j = 0; j < conjuntosDados[i].size(); j++) {
				if (conjuntosDados[i].get(j).classValue() == 0) {
					chuva++;
				} else {
					naoChuva++;
				}
			}
			informacoes[i] += "Instancias para Treino:" + conjuntosDados[i].size() + " Classe Chuva: " + chuva + " Classe Não chuva: "	+ naoChuva + "\n";
		}
	}
	
	public Instances getConjuntoDados(int indice) {
		return conjuntosDados[indice];
	}
	
	public String getInformacoes(int indice) {
		return informacoes[indice];
	}

	public String getPathSaida() {
		return pathSaida;
	}

	public void setPathSaida(String pathSaida) {
		this.pathSaida = pathSaida;
	}
	
}
