package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.lang.System;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Wikipedia {
	
	private static final Scanner scanner = new Scanner(System.in);
	private static final String encoding = "UTF-8";	
	
	public static void main(String[] args) {
		
		boolean exit = false;
		
		while (!exit) {
			
			try {
				String searchText;
				
				System.out.println("Escribe algo para buscar en Internet...");
				String nextLine = scanner.nextLine();
				if (nextLine.toLowerCase().equals("salir"))
					return;
				searchText = nextLine;
				
				System.out.println("Buscando....");
				//Busco en google el Link de Wikipedia
				Document google = Jsoup.connect("https://www.google.com.ar/search?q=" + URLEncoder.encode(searchText + " wikipedia", encoding)).userAgent("Mozilla/5.0").get();

				//Obtengo el primer link de Wikipedia
				String wikipediaURL = google.getElementsByTag("cite").get(0).text();

				//Use Wikipedia API to get JSON File
				String wikipediaApiJSON = "https://es.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
						+ URLEncoder.encode(wikipediaURL.substring(wikipediaURL.lastIndexOf("/") + 1, wikipediaURL.length()), encoding);
				
				//"extract":" the summary of the article
				HttpURLConnection httpcon = (HttpURLConnection) new URL(wikipediaApiJSON).openConnection();
				httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
				BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
				
				//Read line by line
				String responseSB = in.lines().collect(Collectors.joining());
				in.close();
				
				//Print the result for us to see
				if(!responseSB.contains("extract")) {
					System.out.println("Resultado de Google");
					
					//Get the first link
					google = Jsoup.connect("https://www.google.com.ar/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();
					String googleURL = google.getElementsByTag("cite").get(0).text();
					System.out.println(googleURL);
					
				}
				else {
					System.out.println("Resultado de Wikipedia");
					System.out.println(wikipediaURL);
					String result = responseSB.split("extract\":\"")[1];
					result = result.replace("\\n"," ");
					result = result.replace("\\u00e1","a");
					result = result.replace("\\u00e9","é");
					result = result.replace("\\u00ed","í");
					result = result.replace("\\u00f3","ó");
					result = result.replace("\\u00fa","ú");
					result = result.replace("\\u00f1","ñ");
					result = result.replace("\\u00fc","ü");
					result = result.replace("\\u200b","");
					result = result.replace("\"}}}}","");
					result = result.replace("\\u00ab","\"");
					result = result.replace("\\u00bb","\"");
					result = result.replace("\\u00ba","°");
					
					System.out.println(result);
				}

			} catch (Exception ex) {
				ex.printStackTrace();

			}
			
		}
		
	}
	
}
