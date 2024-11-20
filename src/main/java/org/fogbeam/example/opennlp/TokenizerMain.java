package org.fogbeam.example.opennlp;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @class TokenizerMain
 * @brief Clase principal que procesa archivos de texto, tokeniza su contenido y guarda los resultados en un archivo de salida.
 */
public class TokenizerMain {

	private static final Logger logger = Logger.getLogger(TokenizerMain.class.getName());

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
		logger.info("Ingrese el nombre del archivo de salida (por ejemplo, output.txt): ");
		String outputFile = scanner.nextLine();

		// Ruta completa para el archivo de salida
		String outputFilePath = "output/" + outputFile;

		// Solicitar los archivos de entrada
		logger.info("Ingrese los nombres de los archivos de entrada separados por espacio: ");
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

				// Llamar al método de procesamiento de archivo
				processFile(inputFilePath, tokenizer, writer);
			}
		} catch (IOException e) {
			// Capturar cualquier excepción de IO y registrar un mensaje de error
			logger.log(Level.SEVERE, "Error al procesar los archivos", e);
		} finally {
			// Asegurarse de cerrar el InputStream del modelo
			try {
				if (modelIn != null) {
					modelIn.close();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error al cerrar el modelo de tokenización", e);
			}
		}

		// Mensaje indicando que la tokenización se completó con éxito
		logger.info(String.format("Tokenización completada. Resultados guardados en %s", outputFilePath));

		// Cerrar el scanner para liberar los recursos
		scanner.close();
	}

	/**
	 * Procesa un archivo de entrada: verifica si existe, tokeniza su contenido y guarda los tokens en el archivo de salida.
	 *
	 * @param inputFilePath Ruta completa del archivo de entrada.
	 * @param tokenizer Objeto Tokenizer para realizar la tokenización.
	 * @param writer FileWriter para escribir los tokens en el archivo de salida.
	 * @throws IOException Si ocurre un error durante la lectura o escritura de archivos.
	 */
	private static void processFile(String inputFilePath, Tokenizer tokenizer, FileWriter writer) throws IOException {
		if (Files.exists(Paths.get(inputFilePath))) {
			// Leer el contenido del archivo de entrada
			String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));

			// Obtener los tokens del contenido
			String[] tokens = tokenizer.tokenize(content);

			// Escribir los tokens en el archivo de salida solo si hay tokens
			if (tokens.length > 0) {
				for (String token : tokens) {
					// Solo escribir si hay tokens
					writer.write(String.format("%s%n", token)); // Escribir cada token en una nueva línea
				}
			}
		} else {
			// Solo registrar un mensaje de advertencia si el archivo no existe
			logger.warning(String.format("El archivo %s no se encuentra. Omite este archivo.", inputFilePath));
		}
	}
}
