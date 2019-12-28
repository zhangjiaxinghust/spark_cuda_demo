#在Spark中使用JNI调用C&amp;C++代码

-----

Spark使用SCALA语言编写，所以本例子使用SCALA语言编写，文章所用demo已经同步到github(https://github.com/zhangjiaxinghust/spark_cuda_demo )，该例子简单的介绍了如何在SCALA编写的Spark应用程序中通过JNI技术调用cuda程序。  
本文测试环境如下：  
```
System: Ubuntu16.04  
Spark : Spark3.0-preview  
hadoop: hadoop3.2  
Spark主节点：192.168.1.19 用户名：hadoop  密码：hadoop 
spark安装目录：  /usr/local/spark
集群一共有三个结点，采用standalone方式运行
```
关于在SCALA&amp;JAVA中使用JNI调用C++动态库的例子在前面文章已经给出，在此不做赘述！
## 方案
在集群当中使用C++动态库的首要问题就是解决各个机器之间的环境问题，因为C++动态库运行在JVM外面失去了平台的可移植性。最保险的方式就是：将动态库在每个work结点所在的机器上重新编译将动态库输出到不同节点的同一个目录下面，保证系统依赖库不会出现问题（集群很大的时候使用脚本统一编译）。由于我各个节点所在的机器的环境基本一致所以我在一个机器上编译动态库然后将编译好的动态库拷贝到不同节点的同一目录下（我指定动态库编译输出的绝对路径是`/usr/local/spark/dynamic`），也没有出现问题，这一部分可以查看cmake文件。  
spark Scala程序源代码使用sbt打包工具进行打包，生成jar包使用`spark-submit`提交到spark集群就可以了。我的提交命令是`./spark-submit --master spark://Masterhadoop:7077  --class SimpleApp /home/czp/work/spark_java_test/target/scala-2.12/spark_java_test_2.12-0.1.jar`。(切换到主节点/usr/local/spark/bin目录可以直接运行)
## 踩坑：Scala没有static方法
Java之前编写的时候有static方法，将静态库导入直接写为static方法，方便还好用。但是Scala没有提供类似的方法，我们采取的方法是，在类中写一个导入库的函数，每次新建对象之后调用这个函数。如下：  
```
class Sample {
  // --- Native methods
  def loadlibrary: Unit ={
    System.load("/usr/local/spark/dynamic/libadd.so")
  }

  @native def add(n: Int,m: Int): Int
}
```
如果不在新建对象之后执行调用库的函数的话，会出现以下错误：
````
Exception in thread "main" java.lang.UnsatisfiedLinkError: Sample.add(II)I
   at Sample.add(Native Method)
   at SimpleApp$.main(SimpleApp.scala:13)
   at SimpleApp.main(SimpleApp.scala)
   at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
   at java.lang.reflect.Method.invoke(Method.java:498)
   at org.apache.spark.deploy.JavaMainApplication.start(SparkApplication.scala:52)
   at org.apache.spark.deploy.SparkSubmit.org$apache$spark$deploy$SparkSubmit$$runMain(SparkSubmit.scala:928)
   at org.apache.spark.deploy.SparkSubmit.doRunMain$1(SparkSubmit.scala:180)
   at org.apache.spark.deploy.SparkSubmit.submit(SparkSubmit.scala:203)
   at org.apache.spark.deploy.SparkSubmit.doSubmit(SparkSubmit.scala:90)
   at org.apache.spark.deploy.SparkSubmit$$anon$2.doSubmit(SparkSubmit.scala:1007)
   at org.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:1016)
   at org.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)

```
不要相信网上使用`--file`导入动态库，然后`--conf`指定Java的库的路径，个人感觉不如直接将路径写死传入绝对路径来的实在！

