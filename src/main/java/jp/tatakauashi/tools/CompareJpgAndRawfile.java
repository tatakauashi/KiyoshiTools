/**
 *
 */
package jp.tatakauashi.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author tatak
 *
 */
public class CompareJpgAndRawfile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage: java jp.tatakauashi.tools.CompareJpgAndRawfile dir");
			System.exit(1);
		}

		File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println("Argument is dir.");
			System.exit(2);
		}

		SortedSet<String> jpgs = new TreeSet<String>();
		SortedSet<String> raws = new TreeSet<String>();
		for (File f : dir.listFiles()) {
			if (!f.isFile()) {
				continue;
			}
			String fileName = f.getName().toUpperCase();
			String ext = getExtension(fileName);
			SortedSet<String> set = null;
			if (".JPG".equals(ext)) {
				set = jpgs;
			} else {
				set = raws;
			}
			set.add(fileName.substring(0, fileName.length() - ext.length()));
		}

		// テキストファイルに書き込む
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir.getAbsolutePath() + "\\..\\" + "jpg.txt")))) {
			for (String fileTitle : jpgs) {
				writer.write(fileTitle + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir.getAbsolutePath() + "\\..\\" + "raw.txt")))) {
			for (String fileTitle : raws) {
				writer.write(fileTitle + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(4);
		}
	}

	private static String getExtension(final String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(lastIndex);
	}
}
