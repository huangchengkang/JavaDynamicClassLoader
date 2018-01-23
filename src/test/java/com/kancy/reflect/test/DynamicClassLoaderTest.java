package com.kancy.reflect.test;

import com.kancy.reflect.dynamicclassloader.JavaDynamicCompiler;
import com.kancy.reflect.dynamicclassloader.JavaHelper;
import com.kancy.reflect.dynamicclassloader.ReClassLoader;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by cango on 2018/1/23.
 */

public class DynamicClassLoaderTest {

    @Test
    public void testComplie(){
        try {
            JavaDynamicCompiler.complie("src/test/java/com/kancy/reflect/test/TestObject.java");
            // 查看文件的修改时间可以看出结果
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDynamicClassLoader(){
        for (int i = 0; i < 10; i++) {
            String className = JavaHelper.getClassName("src/test/java/com/kancy/reflect/test/TestObject.java");
            System.out.println("className is :"+className);

            ReClassLoader classLoader = new ReClassLoader(){
                protected void complieClass(String className) {
                    // 可以根据面md5，修改时间等因素决定是否重新编译class
                    testComplie();
                }
            };

            try {
                Class aClass = classLoader.loadClass(className);
                Method method = aClass.getDeclaredMethod("say", String.class);
                method.invoke(aClass.newInstance(),"good body!");

                // 延迟等待3秒
                // 这时去修改TestObject代码，发现动态变化
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
