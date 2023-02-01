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

		//List<Path> caminhos = grafoTraversal.V().has("artista", "Eminem").outE().inV().path().toList();
		//List<Object> caminhos = grafoTraversal.V().has("bpm", "122.979").out()/*.out("artista")*/.values("musica").toList();
		//List<Object> caminhos = grafoTraversal.V().has("artista", "Eminem").out().values("bpm").toList();
		List<Object> caminhos = grafoTraversal.V().has("bpm",P.gte("200")).out().values("musica").toList();
		
		//System.out.println(caminhos);
		
		for (Object caminho : caminhos)
		{
			System.out.println(caminho);
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

				grafoDoCSV.mostrarGrafo();
				leitor.close();         
			}
			catch( Exception e ) 
			{
				System.out.println("Não deu pra abrir o arquivo, deu o seguinte problema ::: " + e.toString());
			}
		}  
	}      

}
