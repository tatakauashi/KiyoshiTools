package jp.tatakauashi.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tatak
 *
 */
public final class DateUtils {

	private static final DateTimeFormatter _yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static String getYmdhms() {
		String s = LocalDateTime.now().format(_yyyyMMddHHmmss);
		return s;
	}
}
