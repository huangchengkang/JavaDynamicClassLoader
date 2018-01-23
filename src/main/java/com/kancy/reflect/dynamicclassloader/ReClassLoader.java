package com.kancy.reflect.dynamicclassloader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public abstract class ReClassLoader extends ClassLoader{

	private static String classNameReg = "^([a-zA-Z\\_]{1}[a-zA-Z0-9\\_]*.{0,1}){0,10}";

	public ReClassLoader() {
		super(ReClassLoader.class.getClassLoader());
	}
	public ReClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public Class loadClass(String className) throws ClassNotFoundException {
		//加载完当期类还会去加载该类的父类
		if(className.startsWith("java")||className.startsWith("javax")){
			return super.loadClass(className);
		}
		//过滤不重载的类
		if(!className.matches(classNameReg)){
			return super.loadClass(className);
		}
		//判断是否重新编译class
		complieClass(className);

		//重新加载class
		Class<?> clazz = null;
		ByteArrayOutputStream buffer = null;
		InputStream input = null;
		try {
			input = new FileInputStream(JavaHelper.getClassFile(className));
			buffer = new ByteArrayOutputStream();
			int data = input.read();
			while(data != -1){
				buffer.write(data);
				//buffer.flush();
				data = input.read();
			}
			byte[] classData = buffer.toByteArray();
			clazz = defineClass(className, classData, 0, classData.length);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(input!=null)
					input.close();
				if(buffer!=null)
					buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	protected abstract void complieClass(String className);

	public static void setClassNameReg(String classNameReg) {
		ReClassLoader.classNameReg = classNameReg;
	}

}

