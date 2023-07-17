/**
 *
 */
package jp.tatakauashi.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author tatak
 *
 */
public class PickUpRatedJpg {

	private static final String JPG_FILE_EXTENSION = ".JPG";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("********** Start");

		if (args.length != 3 && args.length != 4) {
			System.err.println("param 1:fromDir 2:jpgDir 3:toDir [4:\"rate .. (-rate)\"]");
			System.exit(1);
		}
		String excludeRatesStr = null;
		if (args.length == 4) {
			excludeRatesStr = args[3];
		}
		int retVal = new PickUpRatedJpg().execute(args[0], args[1], args[2], excludeRatesStr);

		System.out.println("********** End " + retVal);
		System.exit(retVal);
	}

	public int execute(final String fromDirStr, final String jpgDirStr, final String toDirStr, final String excludeRatesStr) {

		Set<Integer> excludeRates = new HashSet<Integer>();
		if (excludeRatesStr == null) {
			// 評価なしの他は5だけ除外する。
			excludeRates.add(0);
			excludeRates.add(5);
		} else {
			// デフォルトはすべて除外
			excludeRates.add(0);
			excludeRates.add(1);
			excludeRates.add(2);
			excludeRates.add(3);
			excludeRates.add(4);
			excludeRates.add(5);

			String[] ratesSplits = excludeRatesStr.split(" +");
			boolean minus = false;
			for (String rateOne : ratesSplits) {
				int rate = Integer.parseInt(rateOne);
				if (rate < 0) {
					if (!minus) {
						// 初めてマイナス（除外）が出現した。
						minus = true;
						// 除外リストを初期化する
						excludeRates = new HashSet<Integer>();
						excludeRates.add(0);	// 評価なしは初めから除外しておく
					}
					excludeRates.add(rate * (-1));
				} else if (!minus) {
					// 含める評価を除外リストから除く
					excludeRates.remove(rate);
				}
			}
		}
		// 除外リストをチェック用の文字列に変更
		Set<String> excludeRateStrs = new HashSet<String>();
		for (Integer rate : excludeRates) {
			excludeRateStrs.add("\"" + (String.valueOf(rate)) + "\"");
		}

		File fromDir = new File(fromDirStr);
		if (!fromDir.exists() || !fromDir.isDirectory()) {
			System.err.println("１つ目の引数にRAWファイルがあるフォルダを指定してください。");
			return 1;
		}

		// ピックアップするファイルタイトルを取得
		List<String> pickupTitleList = new ArrayList<String>();
		for (File f : fromDir.listFiles()) {
			String name = f.getName();
			String lowerName = name.toLowerCase();
			if (f.isFile() && lowerName.endsWith(".xmp") && isPickUp(f, excludeRateStrs)) {
				pickupTitleList.add(FileUtils.getFileTitle(name));
			}
		}

		System.out.println("pickupTitleList.length = " + pickupTitleList.size());

		// JPGファイルをコピー
		File jpgDir = new File(jpgDirStr);
		if (!jpgDir.exists() || !jpgDir.isDirectory()) {
			System.err.println("２つ目の引数にJPEGファイルがあるフォルダを指定してください。");
			return 1;
		}
		File toDir = new File(toDirStr);
		if (!toDir.exists()) {
			if (!toDir.mkdirs()) {
				System.err.println("JEPGファイルの保存先のフォルダの作成に失敗しました。");
				return 1;
			}
		}
		try {
			String jpgDirPath = jpgDir.getCanonicalPath();
			String toDirPath = toDir.getCanonicalPath();

			for (String fileTitle : pickupTitleList) {
//				String fromPath = jpgDirPath + fileTitle + ".jpg";
//				String toPath = toDirPath + fileTitle + ".jpg";
				Path from = Paths.get(jpgDirPath, fileTitle + JPG_FILE_EXTENSION);
				Path to = Paths.get(toDirPath, fileTitle + JPG_FILE_EXTENSION);

				System.out.println("from = " + from.toString());

				Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

	private boolean isPickUp(final File file, final Set<String> excludeRateStrs) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String line = "";
			for (line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.contains("xmp:Rating=")) {
//					if (line.contains("\"0\"") || line.contains("\"5\"")) {
					boolean contains = false;
					for (String val : excludeRateStrs) {
						if (line.contains(val)) {
							contains = true;
							break;
						}
					}

					if (contains) {
						return false;
					}
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}

		// xmp:Rating="x" が見つからない場合
		return false;
	}

}
