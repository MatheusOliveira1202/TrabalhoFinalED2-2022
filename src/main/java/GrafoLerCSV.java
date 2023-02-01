import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.*;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.*;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.util.Gremlin;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.io.*;

public class GrafoLerCSV {

	private TinkerGraph grafo;
	private GraphTraversalSource grafoTraversal;

	// Cria a instancia do grafo
	public boolean criarGrafo()
	{
		grafo = TinkerGraph.open() ;
		grafoTraversal = grafo.traversal();

		if (grafo == null || grafoTraversal==null)
		{
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void criarVerticeEAresta(String campo1, String nomeDoCampo1, String campo2, String nomeDoCampo2)
	{
		Vertex vertice1 = 
				grafoTraversal.V().has(nomeDoCampo1,campo1).fold().
				coalesce(__.unfold(),__.addV().property(nomeDoCampo1,campo1)).next();
		
		Vertex vertice2 = 
				grafoTraversal.V().has(nomeDoCampo2,campo2).fold().
				coalesce(__.unfold(),__.addV().property(nomeDoCampo2,campo2)).next();
		
		grafoTraversal.V().has(nomeDoCampo1,campo1).out(campo2).has(nomeDoCampo2,campo2).fold().
		coalesce(__.unfold(),
				__.addE(campo2).from(__.V(vertice1)).to(__.V(vertice2))).iterate();
		
		grafoTraversal.V().has(nomeDoCampo2,campo2).out(campo1).has(nomeDoCampo1,campo1).fold().
		coalesce(__.unfold(),
				__.addE(campo1).from(__.V(vertice2)).to(__.V(vertice1))).iterate();
	}

	// Adiciona vertice e aresta. Não adiciona se ja existir
	@SuppressWarnings("unchecked")
	public boolean adicionarElemento(String artista, String musica, String duracao, String ehExplicito, String ano,
									String popularidade, String fatorDancante, String energia, String chave, String sonoridade,
									String modo, String vocal, String acustica, String instrumental, String vivacidade,
									String equivalencia, String bpm, String genero)
	{
		if (grafo == null || grafoTraversal==null)
		{
			return false;
		}
		
		String[] camposEmString = {"artista", "musica", "duracao", "ehExplicito", "ano",
				"popularidade", "fatorDancante", "energia", "chave", "sonoridade",
				"modo", "vocal", "acustica", "instrumental", "vivacidade",
				"equivalencia", "bpm", "genero"};
		
		String[] camposEmVariavel = {artista, musica, duracao, ehExplicito, ano,
				popularidade, fatorDancante, energia, chave, sonoridade,
				modo, vocal, acustica, instrumental, vivacidade,
				equivalencia, bpm, genero};
		
		for(int i = 0; i < camposEmString.length; i++)
		{
			for(int j = 0; j < camposEmString.length; j++)
			{
				criarVerticeEAresta(camposEmVariavel[i], camposEmString[i], camposEmVariavel[j], camposEmString[j]);
			}
		}
		
		return true;
	}

	public void mostrarGrafo()
	{
		Long numeroVertices, numeroArestas;
		numeroVertices = grafoTraversal.V().count().next();
		numeroArestas = grafoTraversal.E().count().next();
		System.out.println("O grafo tem " + numeroVertices + " vertices");
		System.out.println("O grafo tem " + numeroArestas + " arestas");

		List<Path> caminhos = grafoTraversal.V().outE().inV().path().toList();
		
		//System.out.println(caminhos);
		
		for (Path caminho : caminhos)
		{
			System.out.println(caminho);
		}
	}
	
	public void mostrarGrafoFiltradoTexto(String filtro, String valorDoFiltro, String campoBuscado) 
	{
		Long numeroVertices, numeroArestas;
		numeroVertices = grafoTraversal.V().count().next();
		numeroArestas = grafoTraversal.E().count().next();
		System.out.println("O grafo tem " + numeroVertices + " vertices");
		System.out.println("O grafo tem " + numeroArestas + " arestas");
		
		List<Object> elementosFiltrados = grafoTraversal.V().has(filtro, valorDoFiltro).out().values(campoBuscado).toList();
		//List<Object> elementosFiltrados = grafoTraversal.V().has("bpm",P.gte("200")).out().values("musica").toList();
		//List<Object> elementosFiltrados = grafoTraversal.V().has("bpm", "122.979").out()/*.out("artista")*/.values("musica").toList();
		//List<Object> elementosFiltrados = grafoTraversal.V().has("artista", "Eminem").out().values("bpm").toList();
		//List<Object> elementosFiltrados = grafoTraversal.V().has("bpm",P.gte("200")).out().values("musica").toList();
		//List<Object> elementosFiltrados = grafoTraversal.V().has("popularidade",P.gte("70")).out().has("artista", "Eminem").out().has("musica", "The Real Slim Shady").out().values("ano").toList();
		
		for (Object elemento : elementosFiltrados)
		{
			System.out.println(elemento);
		}
	}
	
	public void mostrarGrafoFiltradoNumero(String filtro, String valorDoFiltro, String tipoDePredicado, String campoBuscado) 
	{
		Long numeroVertices, numeroArestas;
		numeroVertices = grafoTraversal.V().count().next();
		numeroArestas = grafoTraversal.E().count().next();
		System.out.println("O grafo tem " + numeroVertices + " vertices");
		System.out.println("O grafo tem " + numeroArestas + " arestas");
		List<Object> elementosFiltrados = grafoTraversal.V().out().values().toList();
		if(tipoDePredicado == "gte")
		{
			elementosFiltrados = grafoTraversal.V().has(filtro,P.gte(valorDoFiltro)).out().values(campoBuscado).toList();
		}
		else if(tipoDePredicado == "gt")
		{
			elementosFiltrados = grafoTraversal.V().has(filtro,P.gt(valorDoFiltro)).out().values(campoBuscado).toList();
		}
		else if(tipoDePredicado == "lt")
		{
			elementosFiltrados = grafoTraversal.V().has(filtro,P.lt(valorDoFiltro)).out().values(campoBuscado).toList();
		}
		else if(tipoDePredicado == "lte")
		{
			elementosFiltrados = grafoTraversal.V().has(filtro,P.lte(valorDoFiltro)).out().values(campoBuscado).toList();
		}
		else if(tipoDePredicado == "eq")
		{
			elementosFiltrados = grafoTraversal.V().has(filtro,P.eq(valorDoFiltro)).out().values(campoBuscado).toList();
		}
		
		for (Object elemento : elementosFiltrados)
		{
			System.out.println(elemento);
		}
	}

	public static void main(String[] args) 
	{
		GrafoLerCSV grafoDoCSV = new GrafoLerCSV();

		if (grafoDoCSV.criarGrafo())
		{
			try 
			{
				String linhaDoCSV;
				String [] colunasCSV;

				FileReader arquivoCSV = new FileReader("croped_test_songs_normalize.csv");

				BufferedReader leitor = new BufferedReader(arquivoCSV);

				while((linhaDoCSV = leitor.readLine()) != null) 
				{
					colunasCSV = linhaDoCSV.split(",");
					grafoDoCSV.adicionarElemento(colunasCSV[0], colunasCSV[1], colunasCSV[2], colunasCSV[3], colunasCSV[4],
												colunasCSV[5], colunasCSV[6], colunasCSV[7], colunasCSV[8], colunasCSV[9],
												colunasCSV[10],colunasCSV[11],colunasCSV[12], colunasCSV[13], colunasCSV[14],
												colunasCSV[15], colunasCSV[16], colunasCSV[17]);
				}
				leitor.close();
				//grafoDoCSV.mostrarGrafo();
				//grafoDoCSV.mostrarGrafoFiltradoTexto("artista", "Eminem", "musica");
				//grafoDoCSV.mostrarGrafoFiltradoNumero("ano", "2019", "eq", "musica");
				
				String filtro, valorDoFiltro, predicado, campoDesejado; 
				int opcao1;
				
				Scanner scanner= new Scanner(System.in);
				System.out.println("Digite 1 pra ver a base toda, 2 pra filtrar apenas com texto e 3 pra filtra com algum numero");
				opcao1 = scanner.nextInt();
				if(opcao1 == 1)
				{
					grafoDoCSV.mostrarGrafo();
				}else if(opcao1 == 2)
				{
					Scanner scanner2 = new Scanner(System.in);
					System.out.println("Digite o filtro");
					filtro = scanner2.nextLine();
					System.out.println("Digite o valor do filtro");
					valorDoFiltro = scanner2.nextLine();
					System.out.println("Digite o campo desejado");
					campoDesejado = scanner2.nextLine();
					grafoDoCSV.mostrarGrafoFiltradoTexto(filtro, valorDoFiltro, campoDesejado);
				}else if(opcao1 == 3)
				{
					filtro = "";
					valorDoFiltro = "";
					predicado = "";
					campoDesejado = "";
					Scanner scanner3 = new Scanner(System.in);
					System.out.println("Digite o filtro");
					filtro = scanner3.nextLine();
					System.out.println("Digite o valor do filtro");
					valorDoFiltro = scanner3.nextLine();
					System.out.println("Digite o predicado desejado");
					predicado = scanner3.nextLine();
					System.out.println("Digite o campo desejado");
					campoDesejado = scanner3.nextLine();
					grafoDoCSV.mostrarGrafoFiltradoNumero(filtro.trim(), valorDoFiltro.trim(), predicado.trim(), campoDesejado.trim());
					//grafoDoCSV.mostrarGrafoFiltradoNumero("ano", "2019", "eq", "musica");
				}
				
				
			}
			catch( Exception e ) 
			{
				System.out.println("Não deu pra abrir o arquivo, deu o seguinte problema ::: " + e.toString());
			}
		}  
	}      

}
