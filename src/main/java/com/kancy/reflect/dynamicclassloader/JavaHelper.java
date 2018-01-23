package com.kancy.reflect.dynamicclassloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaHelper {
	private static String fileSplitChar = "/";

	public static String getProjectPath() {
		File directory = new File("");// 参数为空
		String courseFile = null;
		try {
			courseFile = directory.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return courseFile;
	}

	public static String getClassPath() {
		String classPath = null;
		File file = new File(JavaClassLoader.class.getResource("/").getPath());
		classPath = file.getAbsolutePath().replace("\\", fileSplitChar);
		if(!classPath.endsWith(fileSplitChar)){
			classPath += fileSplitChar;
		}
		return classPath;
	}

	public static File getClassFile(String className) {
		File file = new File(getClassFilePath(className));
		if(file.exists()&&file.isFile()){
			return file;
		}
		return null;
	}

	public static String getClassFilePath(String className) {
		return getClassPath()+className.replace(".", "/") + ".class";
	}

	public static URL getClassFileURL(String className) throws MalformedURLException {
		String classFilePath = getClassFilePath(className);
		URL url = new URL("file:"+classFilePath);
		return url;
	}

	public static String getPackageName(String srcFile) {
		String reg = "\\s*package\\s+([a-zA-Z\\.]+)\\s*;";
		String packageName = getRegValue(srcFile , reg);
		return packageName == null?"":packageName;
	}

	public static String getPackagePath(String srcFile) {
		String value = null;
		String arg1 = getPackageName(srcFile);
		if (arg1 != null) {
			value = arg1.replace(".", "/") + "/";
		}
		return value;
	}

	/**
	 * 根据src文件解析classSimpleName
	 * @param srcFile
	 * @return
	 */
	public static String getClassSimpleName(String srcFile) {
		String reg = "\\s*[public|private|protected]{0,9}\\s*class\\s+([a-zA-Z\\_]+)\\s*\\{";
		return getRegValue(srcFile , reg);
	}

	/**
	 * 根据src文件解析classname
	 * @param srcFile
	 * @return
	 */
	public static String getClassName(String srcFile) {
		String packageName = getPackageName(srcFile);
		String classSimpleName = getClassSimpleName(srcFile);
		if(packageName!=null ){
			if(classSimpleName!=null){
				return packageName + "." + classSimpleName;
			}
		}
		return classSimpleName;
	}

	private static String getRegValue(String srcFile,String reg) {
		String value = null;

		Pattern p = Pattern.compile(reg);
		File file = new File(srcFile);
		if (file.exists() && file.isFile()) {
			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;

				while ((line = br.readLine()) != null) {
					if (line.matches(reg)) {
						Matcher m = p.matcher(line);
						if (m.find()) {
							value = m.group(1);
							break;
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return value;
	}

	public static void main(String[] arg) throws IOException {
		System.out.println(getPackageName("my_src/Person.java"));
		System.out.println(getClassName("my_src/Person.java"));
		System.out.println(getClassSimpleName("my_src/Person.java"));
		System.out.println(JavaHelper.getClassPath());
		System.out.println(getProjectPath());

	}
}
