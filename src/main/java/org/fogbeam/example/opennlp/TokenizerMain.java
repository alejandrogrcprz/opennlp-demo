/**
 * @file TokenizerMain.java
 * @brief Aplicación que tokeniza archivos de texto en inglés utilizando OpenNLP.
 *
 * Este programa procesa archivos de texto ubicados en la carpeta "inputs" y guarda los resultados tokenizados en la carpeta "output".
 * Utiliza un modelo preentrenado de OpenNLP para la tokenización.
 */

package org.fogbeam.example.opennlp;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @class TokenizerMain
 * @brief Clase principal que procesa archivos de texto, tokeniza su contenido y guarda los resultados en un archivo de salida.
 */
public class TokenizerMain {

	/**
	 * Método principal que ejecuta la aplicación.
	 *
	 * Este método solicita al usuario los archivos de entrada y salida,
	 * tokeniza el contenido de los archivos de texto de entrada y guarda
	 * los tokens generados en el archivo de salida.
	 *
	 * @param args Argumentos de la línea de comandos (no utilizados en esta implementación).
	 * @throws Exception Si ocurre un error durante el procesamiento de los archivos o la carga del modelo.
	 */
	public static void main(String[] args) throws Exception {

		// Crear un objeto Scanner para leer la entrada del usuario
		Scanner scanner = new Scanner(System.in);

		// Solicitar el nombre del archivo de salida
		System.out.print("Ingrese el nombre del archivo de salida (por ejemplo, output.txt): ");
		String outputFile = scanner.nextLine();

		// Ruta completa para el archivo de salida
		String outputFilePath = "output/" + outputFile;

		// Solicitar los archivos de entrada
		System.out.print("Ingrese los nombres de los archivos de entrada separados por espacio: ");
		String inputFilesString = scanner.nextLine();
		String[] inputFiles = inputFilesString.split(" "); // Dividir los archivos por espacio

		// Cargar el modelo de tokenización
		InputStream modelIn = new FileInputStream("models/en-token.model");
		TokenizerModel model = new TokenizerModel(modelIn);
		Tokenizer tokenizer = new TokenizerME(model);

		// Crear un FileWriter para el archivo de salida
		try (FileWriter writer = new FileWriter(outputFilePath)) {
			// Procesar cada archivo de entrada
			for (String inputFile : inputFiles) {
				// Crear la ruta completa del archivo de entrada
				String inputFilePath = "inputs/" + inputFile;

				// Verificar si el archivo de entrada existe
				if (Files.exists(Paths.get(inputFilePath))) {
					// Leer el contenido del archivo de entrada
					String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));

					// Obtener los tokens del contenido
					String[] tokens = tokenizer.tokenize(content);

					// Escribir los tokens en el archivo de salida
					for (String token : tokens) {
						writer.write(token + "\n"); // Escribir cada token en una nueva línea
					}
				} else {
					// Si el archivo no existe, mostrar un mensaje de advertencia
					System.out.println("El archivo " + inputFilePath + " no se encuentra. Omite este archivo.");
				}
			}
		} catch (IOException e) {
			// Capturar cualquier excepción de IO y mostrar un mensaje de error
			e.printStackTrace();
		} finally {
			// Asegurarse de cerrar el InputStream del modelo
			try {
				if (modelIn != null) {
					modelIn.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Mensaje indicando que la tokenización se completó con éxito
		System.out.println("Tokenización completada. Resultados guardados en " + outputFilePath);

		// Cerrar el scanner para liberar los recursos
		scanner.close();
	}
}
