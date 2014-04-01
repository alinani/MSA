package com.example.buzz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Decompress {

	private String zip;
	private String loc;

	public Decompress(String zipFile, String location) {
		zip = zipFile;
		loc = location;

		dirChecker("");
	}

	public void unzip() {
		try {
			FileInputStream fin = new FileInputStream(zip);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				Log.v("Decompress", "Unzipping " + ze.getName());

				if (ze.isDirectory()) {
					dirChecker(ze.getName());
				} else {
					FileOutputStream fout = new FileOutputStream(loc
							+ ze.getName());
					for (int c = zin.read(); c != -1; c = zin.read()) {
						fout.write(c);
					}

					zin.closeEntry();
					fout.close();
				}

			}
			zin.close();
		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
		}

	}

	private void dirChecker(String dir) {
		File f = new File(loc + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

}
