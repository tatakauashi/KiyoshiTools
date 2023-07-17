/**
 *
 */
package jp.tatakauashi.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author KiyoshiPhotos
 */
public class ChangeFileNameToTime {

	/** 補正する時間（ミリ秒）。 */
	private int plusMillis = 0;

	private void printUsage() {
		System.out.println("ChangeFileNameToTime path [-mMinutes] [-sSeconds]");
		System.out.println("    (must) path: ファイルが格納されているフォルダのフルパス");
		System.out.println("    (optional) [-mMinutes]: そのフォルダのファイルのタイムスタンプががどのくらい進んでいるか（分）。");
		System.out.println("    (optional) [-sSeconds]: そのフォルダのファイルのタイムスタンプががどのくらい進んでいるか（秒）。");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		new ChangeFileNameToTime().execute(args);
	}

	private void execute(String[] args) {
		File rootDir = new File(args[0]);
		if (args.length < 1 || !rootDir.exists() || !rootDir.isDirectory()) {
			System.err.println("引数が不正です。");
			printUsage();
			System.exit(1);
		}

		// 引数からタイムスタンプを補正する差分を取得する
		if (readArgsToFixTimestamp(args)) {
			printUsage();
			System.exit(1);
		}

		String pattern = "yyyyMMddHHmmss";
		Map<String, SortedSet<String>> map = new HashMap<String, SortedSet<String>>();
		Map<String, List<String>> otherMap = new HashMap<String, List<String>>();
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		for (File file : rootDir.listFiles()) {
			try {
				if (!file.isFile()) {
					continue;
				}
				String rawExtension = FileUtils.getExtension(file.getName()).toLowerCase();
				if (rawExtension.endsWith(".cr2") || rawExtension.endsWith(".cr3")) {
					BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					FileTime time = attrs.creationTime();
					long creationTime = time.toMillis();
					long lastModified = file.lastModified();
					long timestamp = creationTime < lastModified ? creationTime : lastModified;
					// ファイル名になるタイムスタンプを変更する場合はここで行う（ミリ秒）
//					timestamp += -245000L;	// ←これは4分5秒進んだタイムスタンプを戻している。
					timestamp += plusMillis;

					String dateTimeStr = format.format(new Date(timestamp));

					SortedSet<String> set = null;

					if (!map.containsKey(dateTimeStr)) {
						map.put(dateTimeStr, new TreeSet<String>());
					}
					set = map.get(dateTimeStr);
					set.add(file.getName());
				}
				else {
					// JPG, XMPなどその他のファイル
					String fileName = file.getName();
					String fileTitle = FileUtils.getFileTitle(fileName);
					String extension = FileUtils.getExtension(fileName);
					if (!otherMap.containsKey(fileTitle)) {
						otherMap.put(fileTitle, new ArrayList<String>());
					}
					List<String> extensionList = otherMap.get(fileTitle);
					// その他のファイルの拡張子を登録
					extensionList.add(extension);
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		String rootDirStr = rootDir.getAbsolutePath();

		for (Map.Entry<String, SortedSet<String>> entry : map.entrySet()) {
			String dateTimeStr = entry.getKey();
			SortedSet<String> set = entry.getValue();
			int i = 0;
			for (String fileName : set) {
				String fileTitle = FileUtils.getFileTitle(fileName);
				String extension = FileUtils.getExtension(fileName);
				String newFileTitle = dateTimeStr + String.format("%03d", i++)
					+ "_" + FileUtils.getFileTitle(fileName);
				String newFileName = newFileTitle + extension;

				Path fromPath = Paths.get(rootDirStr, fileName);
				Path toPath = Paths.get(rootDirStr, newFileName);
				try {
					Files.move(fromPath, toPath);

					// その他のファイルがあればそれもリネームする
					if (otherMap.containsKey(fileTitle)) {
						for (String ext : otherMap.get(fileTitle)) {
							String jpgFileName = fileTitle + ext;
							fromPath = Paths.get(rootDirStr, jpgFileName);
							if (fromPath.toFile().exists()) {
								toPath = Paths.get(rootDirStr, newFileTitle + ext);
								Files.move(fromPath, toPath);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * コマンドライン引数からタイムスタンプの補正量を取得する。
	 * @param args
	 * @return
	 */
	private boolean readArgsToFixTimestamp(String[] args) {

		boolean error = false;
		try {
			long millisSum = 0;
			for (int i = 1; i < args.length; i++) {
				String arg = args[i].trim();
				long millis = 1000L;
				if (arg.indexOf("-m") == 0) {
					millis *= Long.valueOf(arg.substring(2)) * 60;
				} else if (arg.indexOf("-s") == 0) {
					millis *= Long.valueOf(arg.substring(2));
				}
				millisSum += millis;
			}
			plusMillis -= millisSum;
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
		}
		return error;
	}
}
