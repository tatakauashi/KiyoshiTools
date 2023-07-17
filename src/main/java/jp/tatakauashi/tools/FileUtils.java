/**
 *
 */
package jp.tatakauashi.tools;

/**
 * @author tatak
 *
 */
public final class FileUtils {

	/**
	 * ファイル名のうち、連番部分に相当する文字列を返す。
	 * 例） IMG_0000.JPG -> 0000
	 *
	 * @param fileName ファイル名
	 * @return
	 */
	public static String getNameHead(final String fileName) {
		int index = fileName.indexOf("_");
		int index2 = fileName.lastIndexOf(".");
		return fileName.substring(index + 1, index2);
	}

	/**
	 * ファイル名の拡張子の直前までの文字列を返す。
	 * 例）IMG_0000.JPG - > IMG_0000
	 *
	 * @param fileName ファイル名
	 * @return
	 */
	public static String getFileTitle(final String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, lastIndex);
	}

	/**
	 * ファイル名のうち、その拡張子を返す。
	 * 例）IMG_0000.JPG -> .JPG
	 *
	 * @param fileName ファイル名
	 * @return
	 */
	public static String getExtension(final String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(lastIndex);
	}

}
