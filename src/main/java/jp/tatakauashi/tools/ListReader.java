package jp.tatakauashi.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * リストを読むときのレコードの状態を管理する。
 *
 * @author tatak
 *
 */
@Getter
public class ListReader {
	private RecordStatus status;
	private List<AirConRecord> list;
	public ListReader() {
		this.status = RecordStatus.Start;
		this.list = new ArrayList<AirConRecord>();
	}

	public boolean readLine(final String line1) {
		if (line1 == null) {
			return false;
		}
		String line = line1.trim();
		AirConRecord current = null;
		switch (this.status) {
			case Start:
				if (line.startsWith("<li class=")) {
					// data-item-id
					Map<String, String> map = getKVPairs(line);
					current = new AirConRecord();
					this.list.add(current);
					current.setIndex(this.list.size());
					current.setId(map.get("data-item-id"));
					current.setName(map.get("data-item-name"));
					current.setBrand(map.get("data-item-brand"));
					this.status = RecordStatus.Href;
				}
				break;
			case Href:
				if (line.startsWith("<a data-item-click=\"data-item-click\"")) {
					// href
					Map<String, String> map = getKVPairs(line);
					current = list.get(list.size() - 1);
					current.setHref(map.get("href"));
					this.status = RecordStatus.Price;
				}
				break;
			case Price:
				if (line.startsWith("<p class=\"bcs_price\"><span class=\"val\">")) {
					// Price
					line = line.substring("<p class=\"bcs_price\"><span class=\"val\">".length());
					int endIndex = line.indexOf("</span>");
					current = list.get(list.size() - 1);
					String priceStr = line.substring(0, endIndex);
					priceStr = priceStr.endsWith("円") ? priceStr.substring(0, priceStr.length() - 1) : priceStr;
					current.setPrice(priceStr);
					this.status = RecordStatus.Stock;
				}
				break;
			case Stock:
				if (line.startsWith("<p class=\"label_")) {
					// Stock
					int beginIndex = line.indexOf("<span>");
					int endIndex = line.indexOf("</span>");
					String stock = line.substring(beginIndex + "<span>".length(), endIndex);
					current = list.get(list.size() - 1);
					current.setStock(stock);
					this.status = RecordStatus.Start;
				}
				break;
			default:
				return false;
		}

		return true;
	}

	private Map<String, String> getKVPairs(final String line1) {
		Map<String, String> map = new HashMap<String, String>();

		String line = line1;
		while (line != null && line.length() > 0) {
			line = line.trim();
			int spIndex = line.indexOf(" ");
			int sepIndex = line.indexOf("=\"");
			if (sepIndex < 0) {
				break;
			}
			if (spIndex >= 0 && spIndex < sepIndex) {
				line = line.substring(spIndex);
				continue;
			}
			String key = line.substring(0, sepIndex);
			line = line.substring(sepIndex + "=\"".length());
			int endIndex = line.indexOf("\"");
			if (endIndex < 0) {
				break;
			}
			String value = line.substring(0, endIndex);
			map.put(key, value);
			line = line.substring(endIndex);
		}

		return map;
	}
}