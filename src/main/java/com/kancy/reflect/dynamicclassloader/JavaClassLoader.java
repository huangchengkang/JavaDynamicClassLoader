package com.kancy.reflect.dynamicclassloader;

public class JavaClassLoader {
	public static Class complieAndLoad(String srcFile) throws Exception{
		JavaDynamicCompiler.complie(srcFile);
		String className = JavaHelper.getClassName(srcFile);
		return Class.forName(className);
	}
	public static Class complieAndLoad(String srcFile , ClassLoader classLoader) throws Exception{
		JavaDynamicCompiler.complie(srcFile);
		String className = JavaHelper.getClassName(srcFile);
		return classLoader.loadClass(className);
	}
	
}
