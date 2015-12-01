package org.erc.jms;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Util {

	
	public static String getStringFromInputStream(String file) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			Log.err(e);
		} finally {
			if (br != null) {
				try { br.close(); } catch (IOException e) {}
			}
		}
		String content = sb.toString();
		content = content.replace("\r", "");
		content = content.replace("\n", "");
		content = content.replace("\t", "");
		return content;
	}
}
