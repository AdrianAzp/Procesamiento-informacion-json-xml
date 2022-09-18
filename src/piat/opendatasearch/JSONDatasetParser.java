package piat.opendatasearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/* En esta clase se comportará como un hilo */

/**
 * @author Adrian Azpilicueta Pomar 02573134D
 * 
 */

public class JSONDatasetParser implements Runnable {

	private boolean debug = false;

	private String fichero;
	private List<String> lConcepts;
	List<Graph> listGraph;
	private Map<String, HashMap<String, String>> mDatasetConcepts;
	private String nombreHilo;
	private int contador;
	private Graph graph;

	public JSONDatasetParser(String archivo, List<String> lConcepts,
			Map<String, HashMap<String, String>> mDatasetConcepts) {
		this.fichero = archivo;
		this.lConcepts = lConcepts;
		this.mDatasetConcepts = mDatasetConcepts;
		contador = 0;
		graph = new Graph();
		listGraph = new ArrayList<Graph>();
		run();
	}

	@Override
	public void run() {
		List<Map<String, String>> graphs = new ArrayList<Map<String, String>>(); // Aquí se almacenarán todos los
																					// graphs de un dataset cuyo objeto
																					// de nombre @type se corresponda
																					// con uno de los valores pasados en
																					// el la lista lConcepts
		boolean finProcesar = false; // Para detener el parser si se han agregado a la lista graphs 5 graph

		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";
		// System.out.println(nombreHilo + fichero);
		try {
			InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8");
			// TODO:
			// - Crear objeto JsonReader a partir de inputStream
			// - Consumir el primer "{" del fichero
			// - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta
			// que finProcesar=true
			// Si se encuentra el objeto @graph, invocar a procesar_graph()
			// Descartar el resto de objetos
			// - Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			// - Cerrar el objeto JsonReader
			// System.out.println("Hilo " + nombreHilo);
			JsonReader reader = new JsonReader(inputStream);
			// System.out.println("JSONDatasetParser tiene estos archivos: " +
			// reader.getPath() + lConcepts.toString() + mDatasetConcepts.size());
			procesar_graph(reader, graphs, lConcepts, fichero);
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo + "El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo + "Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}
		// mDatasetConcepts.put(fichero, graphs); // Se añaden al Mapa de concepts de
		// los Datasets

	}

	/*
	 * procesar_graph() Procesa el array @graph Devuelve true si ya se han añadido 5
	 * objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts, String resource)
			throws IOException {
		boolean finProcesar = false;
		// TODO:
		// - Consumir el primer "[" del array @graph
		// - Procesar todos los objetos del array, hasta el final de fichero o hasta que
		// finProcesar=true
		// - Consumir el primer "{" del objeto
		// - Procesar un objeto del array invocando al método procesar_un_graph()
		// - Consumir el último "}" del objeto
		// - Ver si se han añadido 5 graph a la lista, para en ese caso poner la
		// variable finProcesar a true
		// - Si se ha llegado al fin del array, consumir el último "]" del array

		jsonReader.beginObject(); // INICIO DEL OBJETO GENERAL

		while (jsonReader.hasNext()) {
			String etiqueta = jsonReader.nextName();

			if ("@context".equals(etiqueta)) {
				jsonReader.beginObject(); // INICIO DEL OBJETO CONTEXT
				while (jsonReader.hasNext()) {
					jsonReader.skipValue();
				}
				jsonReader.endObject(); // FINAL DEL OBJETO CONTEXT
			}

			else if ("@graph".equals(etiqueta)) {
				procesar_un_graph(jsonReader, graphs, lConcepts, resource);
				if (debug) {
					System.out.println("finProcesar est�: " + finProcesar);
					System.out.println("Contador est�: " + contador);
				}
				if (contador == 5) {
					finProcesar = true;
					break;
				} else
					jsonReader.endArray(); // FINAL DEL ARRAY GENERAL (y unico)
			}
		}
		return finProcesar;
	}

	/*
	 * procesar_un_graph() Procesa un objeto del array @graph y lo añade a la lista
	 * graphs si en el objeto de nombre @type hay un valor que se corresponde con
	 * uno de la lista lConcepts
	 */

	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts, String resource)
			throws IOException {

		// TODO:
		// - Procesar todas las propiedades de un objeto del array @graph, guardándolas
		// en variables temporales
		// - Una vez procesadas todas las propiedades, ver si la clave @type tiene un
		// valor igual a alguno de los concept de la lista lConcepts. Si es así
		// guardar en un mapa Map<String,String> todos los valores de las variables
		// temporales recogidas en el paso anterior y añadir este mapa al mapa graphs

		jsonReader.beginArray(); // INICIO DEL ARRAY GENERAL (y unico)
		while (jsonReader.hasNext() && contador < 5) {
			jsonReader.beginObject(); // INICIO DE CADA OBJETO DE DENTRO DEL ARRAY
			graph = new Graph();
			while (jsonReader.hasNext()) {
				graph.setResourceID(resource);
				String etiquetaGraph = jsonReader.nextName();

				if ("@id".equals(etiquetaGraph))
					graph.setIdGeneral(jsonReader.nextString());

				else if ("@type".equals(etiquetaGraph))
					graph.setType(jsonReader.nextString());

				else if ("dtstart".equals(etiquetaGraph))
					graph.setTimetableStart(jsonReader.nextString());
				else if ("dtend".equals(etiquetaGraph))
					graph.setTimetableEnd(jsonReader.nextString());

				else if ("link".equals(etiquetaGraph))
					graph.setLink(jsonReader.nextString());

				else if ("event-location".equals(etiquetaGraph))
					graph.setEventLocation(jsonReader.nextString());

				else if ("relation".equals(etiquetaGraph)) {
					jsonReader.beginObject(); // INICIO DEL OBJETO RELAITON
					while (jsonReader.hasNext()) {
						String etiquetaLocation = jsonReader.nextName();
						if ("@id".equals(etiquetaLocation))
							graph.setRelation(jsonReader.nextString());
						else
							jsonReader.skipValue();
					}
					jsonReader.endObject(); // FINAL DEL OBJETO RELAITON
				}

				else if ("title".equals(etiquetaGraph))
					graph.setTitle(jsonReader.nextString());

				else if ("location".equals(etiquetaGraph)) {
					jsonReader.beginObject(); // INICIO DEL OBJETO LOCATION
					while (jsonReader.hasNext()) {
						String etiquetaLocation = jsonReader.nextName();

						/*
						 * if ("area".equals(etiquetaLocation))
						 * graph.setIdArea(jsonReader.nextString());
						 */
						if ("latitude".equals(etiquetaLocation))
							graph.setLatitude(String.valueOf(jsonReader.nextDouble()));
						else if ("longitude".equals(etiquetaLocation))
							graph.setLongitude(String.valueOf(jsonReader.nextDouble()));

						else
							jsonReader.skipValue();
					}
					jsonReader.endObject(); // FIANL DEL OBJETO LOCATION
				}

				else if ("address".equals(etiquetaGraph)) {
					jsonReader.beginObject(); // INICIO DEL OBJETO ADDRESS
					while (jsonReader.hasNext()) {
						String etiquetaAddress = jsonReader.nextName();
						if ("area".equals(etiquetaAddress)) {
							jsonReader.beginObject(); // INICIO DEL OBJETO AREA
							while (jsonReader.hasNext()) {
								String etiquetaArea = jsonReader.nextName();
								if ("@id".equals(etiquetaArea))
									graph.setIdArea(jsonReader.nextString());
								else
									jsonReader.skipValue();
							}
							jsonReader.endObject(); // FINAL DEL OBJETO AREA
						} else
							jsonReader.skipValue();
					}
					jsonReader.endObject(); // FINAL DEL OBJETO ADDRESS
				}

				else if ("organization".equals(etiquetaGraph)) {
					jsonReader.beginObject(); // INICIO DEL OBJETO ORGANIZATION
					while (jsonReader.hasNext()) {
						String etiquetaOrganization = jsonReader.nextName();
						if ("accesibility".equals(etiquetaOrganization))
							graph.setAccesibility(jsonReader.nextString());
						else if ("organization-name".equals(etiquetaOrganization))
							graph.setOrganizationName(jsonReader.nextString());
						else
							jsonReader.skipValue();
					}
					jsonReader.endObject(); // FINAL DEL OBJETO ORGANIZATION
				}

				else if ("description".equals(etiquetaGraph)) {
					graph.setDescription(jsonReader.nextString());
				}

				else {
					jsonReader.skipValue();
				}
			}

			if (lConcepts.contains(graph.getType())) {
				contador++;
				listGraph.add(graph);
			}

			jsonReader.endObject(); // FINAL DE CADA OBJETO DE DENTRO DEL ARRAY
		}

	}

	public List<Graph> get_lista_graphs() {
		return listGraph;
	}

}
