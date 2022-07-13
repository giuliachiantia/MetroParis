package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	Map <Integer, Fermata> fermateIdMap; 
	
	
	public List<Fermata> getFermate(){
		if(this.fermate==null) {
			MetroDAO dao= new MetroDAO();
			this.fermate=dao.getAllFermate();
			this.fermateIdMap= new HashMap<Integer, Fermata>();
			for(Fermata f:this.fermate) {
				this.fermateIdMap.put(f.getIdFermata(), f);
			}
		}
		
		return this.fermate;
	}
	
	public List <Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){ //fermate del percorso
		creaGrafo();
		Map<Fermata, Fermata> alberoInverso= visitaGrafo(partenza);
		Fermata corrente=arrivo;
		List <Fermata> percorso= new ArrayList <>();
		
		while(corrente!=null) {
			percorso.add(0,corrente); //aggiungo in testa alla lista non in coda
			corrente=alberoInverso.get(corrente); //ho vertice di corrente e devo passare al precedente
			//se mi servono i cammini esite .getParent che mi da albero inverso
		}
		return percorso;
		
	
	}
	
	public void creaGrafo() {
		//orientato, non pesato e semplice
		this.grafo= new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		//devo prendere le fermate e convertirle in vertici
		MetroDAO dao= new MetroDAO();
		//fa tutto questo in getFermata
		/*List <Fermata> fermate= dao.getAllFermate();
		//non si puo fare allAll se non ho fatto hashcode e equal
		
		Map<Integer, Fermata> fermateIdMap= new HashMap<>();
		for(Fermata f: fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}*/
		Graphs.addAllVertices(this.grafo, getFermate()); //carico lista che viene convertita in un set
		//METODO 1
		/*for(Fermata partenza: fermate) {
			for(Fermata arrivo: fermate) {
				if(dao.isFermataConnessa(partenza, arrivo)) { //se esiste connessione tra le due
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}*/
		//METODO 2: dato ciascun vertice chiedo quali stazioni collegate
		//itero sulle fermate di partenza e per ciascuna fermata voglio lista id fermate connesse
		//variante 2b: dao restituisce elenco oggetti fermata
		//variante 2c : il dao restituisce oggetti che converto in oggetti 
		//fermata tramite Map <Integer, Fermata> identity map
		/*for(Fermata partenza: fermate) {
			List <Integer> idConnesse= dao.getIdFermateConnesse(partenza);
			for(Integer id: idConnesse) {
				Fermata arrivo=fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		//variante 3 faccio una sola query che restituosce le coppie di fermate collegate
		//Variante preferita 2c con identity map
		/*for(Fermata partenza: fermate) {
			List <Integer> idConnesse= dao.getIdFermateConnesse(partenza);
			for(Integer id: idConnesse) {
				
				Fermata arrivo=null;
				for(Fermata f: fermate) {
					if(f.getIdFermata()==id) {
						arrivo=f;
					}
				}
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		/*for(Fermata partenza:fermate) {
			List <Fermata> arrivi= dao.getFermateConnesse(partenza);
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		
		List <CoppiaId> fermateDaCollegare= dao.getAllFermateConnesse();
		for(CoppiaId coppia: fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.idPartenza), fermateIdMap.get(coppia.idArrivo));
		}
		
	//	System.out.println(this.grafo);
	//	System.out.println("Vertici= " +this.grafo.vertexSet().size());
	//	System.out.println("Archi= " +this.grafo.edgeSet().size());
	//	visitaGrafo(fermate.get(0));
	}
	
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
		//se metto un solo parametro parte da vertice a caso
		GraphIterator<Fermata, DefaultEdge> visita= new BreadthFirstIterator<>(this.grafo, partenza);
		
		Map<Fermata, Fermata> alberoInverso= new HashMap<>();
		alberoInverso.put(partenza, null); //radice albero
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo));
		while(visita.hasNext()) {
			Fermata f=visita.next();
			//System.out.println(f);
		}
		//non va bene perchè ce solo la partenza
		//List<Fermata> percorso= new ArrayList<>();
		/*while(fermata!=null) {
	      fermata=alberoInverso.get(fermata);
		percorso.add(fermata);
		}*/
		return alberoInverso;
		
	}

}
