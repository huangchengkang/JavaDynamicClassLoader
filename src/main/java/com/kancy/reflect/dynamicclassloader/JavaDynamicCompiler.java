package com.kancy.reflect.dynamicclassloader;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileFilter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 需要引入 java_home/lib/tools.jar
 * java脚本动态编译
 */
public class JavaDynamicCompiler {
	private static Logger logger = Logger.getLogger(JavaDynamicCompiler.class.getName());
	private static com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();
	private static JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

	public static boolean javaComplie(String ... srcFiles) {
		return javaComplieToBin(JavaHelper.getClassPath(), srcFiles);
	}
	public static boolean javaComplieToBin(String binDir , String ... srcFiles) {
		File dir = new File(binDir);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		List params = Arrays.asList(new String[] { "-d", binDir });
		params.add("-cp");
		params.add(System.getProperty("java.class.path"));
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager((DiagnosticListener) null, (Locale) null,
				(Charset) null);

		List<File> list = new ArrayList<File>();
		for (int i = 0; i < srcFiles.length; i++) {
			File file = new File(srcFiles[0]);
			if(!file.exists()){
				throw new RuntimeException("src file[" + file + "] is not exist!");
			}
			if(file.isFile()){
				list.add(file);
			}
			if (file.isDirectory()) {
				addDirectoryFiles(list, file);
			}
		}

		Iterable it = fileManager.getJavaFileObjectsFromFiles(list);
		return javaCompiler.getTask((Writer) null, fileManager, (DiagnosticListener) null, params, (Iterable) null, it)
				.call().booleanValue();
	}

	public static void complie(String... javas) throws Exception {
		complieToBin(JavaHelper.getClassPath(), javas);
	}
	public static void complieToBin(String binDir, String... javas) throws Exception {
		if (javas != null && javas.length != 0) {
			List<File> javaFileList = getAllComplieFiles(binDir, javas);
			if (javaFileList != null && javaFileList.size() != 0) {
				String[] params = new String[] { "-d", "", "","-cp", System.getProperty("java.class.path") }; //-verbose
				Iterator<File> it = javaFileList.iterator();
				while (it.hasNext()) {
					File file = it.next();
					params[1] = binDir;
					params[2] = file.getAbsolutePath();
					javac.compile(params);
				}
				logger.log(Level.INFO, "complie sucessful!");
			} else {
				logger.log(Level.INFO, "No complie tasks!");
			}
		} else {
			logger.log(Level.INFO, "No complie tasks!");
		}
	}

	private static List<File> getAllComplieFiles(String binDir, String[] srcFiles) {
		ArrayList list = new ArrayList();
		File file = new File(binDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		for (String filePath : srcFiles) {
			file = new File(filePath);
			if (!file.exists()) {
				throw new RuntimeException("src file[" + file + "] is not exist!");
			}
			if (file.isFile()) {
				list.add(file);
			}
			if (file.isDirectory()) {
				addDirectoryFiles(list, file);
			}
		}
		return list;
	}

	private static void addDirectoryFiles(final List<File> list, File fileDir) {
		fileDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (!file.exists()) {
					throw new RuntimeException("src file[" + file + "] is not exist!");
				} else {
					if (file.isFile()) {
						list.add(file);
					}
					if (file.isDirectory()) {
						addDirectoryFiles(list, file);
					}
					return false;
				}
			}
		});
	}
}
