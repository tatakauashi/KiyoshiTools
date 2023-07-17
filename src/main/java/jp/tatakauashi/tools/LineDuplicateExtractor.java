/**
 *
 */
package jp.tatakauashi.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tatak
 *
 */
public class LineDuplicateExtractor {

	private static final Logger logger = LoggerFactory.getLogger(LineDuplicateExtractor.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.exit(1);
		}
		String filePath = args[0];
		Path inputFilePath = Paths.get(filePath);
		Path parent = inputFilePath.getParent();
		String outFilePath = Paths.get(parent.toString(), "outFile_" + DateUtils.getYmdhms() + ".txt").toString();
		Set<String> lineSet = new HashSet<String>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilePath)))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!lineSet.contains(line)) {
						logger.info(line);
						lineSet.add(line);
						writer.write(line);
						writer.newLine();
					}
				}
			}
		} catch (IOException e) {
			logger.error("ファイルの読み書きに失敗しました。: " + e.getLocalizedMessage(), e);
		}

	}

}
