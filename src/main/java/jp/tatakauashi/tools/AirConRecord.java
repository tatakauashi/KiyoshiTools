package jp.tatakauashi.tools;

import lombok.Data;

/**
 * エアコンデータレコード。
 *
 * @author tatak
 */
@Data
public class AirConRecord {
	private int index;
	private String id;
	private String name;
	private String brand;
	private String href;
	private String price;
	private String stock;
}
