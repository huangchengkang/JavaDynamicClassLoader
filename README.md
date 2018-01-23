http://tutorials.jenkov.com/java-reflection/dynamic-class-loading-reloading.html#classloader

Java动态重新加载Class 

    项目中使用到了动态重新加载Class的机制，作用是让一些代码上线之前可以在线上环境测试一下，当然，这是非常不好的测试机制，我刚来的时候也为这种机制感到惊讶—怎么可以在线上环境运行测试代码！
	后来经过了解，这么做的原因有以下两个： 有些代码没有办法在本地进行测试，本地没有线上的环境我们弱到连测试机都没有（这是重点）

ClassLoader 

    顾名思义，ClassLoader就是用来Load Class的，当一个Class被加载的时候，这个Class所引用到的所有Class也会被加载，而且这种加载是递归的。
	也就是说，如果A引用到B，B 引用到C，那么当A被加载的时候，B也会被加载，而B被加载的时候，C也会加载。如此递归直到所有需要的Class都加载好。 
    常见的ClassLoader： 

	* Bootstrap class loader：虚拟机运行时必须要用到的类的加载器，比如java.*。它通常是在虚拟机种用本地代码（如C）实现，在系统中用null表示。 
	* Extension class loader：负责加载ext目录下的Class。 
	* Application class loader：负责加载CLASSPATH上的类。

ClassLoader的代理层次关系 
    ClassLoader是以层次关系组织起来的，当你创建一个标准的Java ClassLoader的时候，你必须提供一个父ClassLoader。
	当一个ClassLoader需要加载一个Class的时候，它首先会让父 ClassLoader去加载这个Class，如果父ClassLoader不能加载这个Class，那么当前的ClassLoader才会自己去加载。 
    ClassLoader加载Class的步骤： 
	
		* 检查这个Class是否已经被加载过了
		* 如果没有被加载过，那么让父ClassLoader尝试去加载
		* 如果父ClassLoader无法加载，那么尝试使用当前ClassLoader加载

所以，如果你需要ClassLoader重新加载一个Class，重写findClass方法是起不到效果的，因为findClass在父 ClassLoader加载失败之后才会执行 
	// First, check if the class has already been loaded
    Class c = findLoadedClass(name);
    if (c == null) {
		try {
        if (parent != null) {
            c = parent.loadClass(name, false);
        } else {
            c = findBootstrapClass0(name);
        }
        } catch (ClassNotFoundException e) {
            // If still not found, then invoke findClass in order
            // to find the class.
            c = findClass(name);
        }
    }
	
 必须重写loadClass方法才能达到效果。
 
动态重新加载Class 
 
    Java内置的ClassLoader总会在加载一个Class之前检查这个Class是否已经被加载过，已经被加载过的Class不会加载第二次。因此要想重新加载Class，我们需要实现自己的ClassLoader。 
    另外一个问题是，每个被加载的Class都需要被链接(link)，这是通过执行ClassLoader.resolve()来实现的，这个方法是 final的，因此无法重写。Resove()方法不允许一个ClassLoader实例link一个Class两次，
	因此，当你需要重新加载一个 Class的时候，你需要重新New一个你自己的ClassLoader实例。 
	
刚才说到一个Class不能被一个ClassLoader实例加载两次，但是可以被不同的ClassLoader实例加载，这会带来新的问题：
	
	MyObject object = (MyObject)
    myClassReloadingFactory.newInstance("com.jenkov.MyObject");
	
	这段代码会导致一个ClassCastException，因为在一个Java应用中，Class是根据它的全名（包名+类名）和加载它的 ClassLoader来唯一标识的。
	在上面的代码中object对象对应的Class和newInstance返回的实例对应的Class是有区别的： 

	
								全名					ClassLoader实例
	Object对象的Class			com.jenkov.MyObject 	AppClassLoader实例
	newInstance返回对象的Class	com.jenkov.MyObject		自定义ClassLoader实例


    解决的办法是使用接口或者父类，只重新加载实现类或者子类即可。

	MyObjectInterface object = (MyObjectInterface) myClassReloadingFactory.newInstance("com.jenkov.MyObject");

	MyObjectSuperclass object = ( MyObjectSuperclass) myClassReloadingFactory.newInstance("com.jenkov.MyObject");
	
	在自己实现的ClassLoader中，当需要加载MyObjectInterface或者MyObjectSuperclass的时候，要代理给父 ClassLoader去加载。 

    实例代码就不贴了，可以去原作者网站上去看，动态重新加载Class可以做成当Class文件有修改的时候就重新加载(比如根据文件大小+修改时间或者算个文件md5值)。 