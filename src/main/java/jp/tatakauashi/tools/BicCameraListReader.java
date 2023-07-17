/**
 *
 */
package jp.tatakauashi.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author tatak
 *
 */
public class BicCameraListReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		String filePath = "C:\\pleiades\\workspace\\KiyoshiTools\\AirConList.txt";
		ListReader listReader = new ListReader();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				listReader.readLine(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}

		for (AirConRecord record : listReader.getList()) {
			System.out.println(String.format("%d\t%s\t%s\t%s\t%s\t%s\t%s",
					record.getIndex(),
					record.getId(),
					record.getName(),
					record.getBrand(),
					record.getHref(),
					record.getPrice(),
					record.getStock()
					));
		}
	}

}
