import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object SimpleApp {
  def main(args:Array[String]){
    val logFile = "hdfs:/test/ceshi2.csv"
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile,2).cache()
    val sample = new Sample
    sample.loadlibrary
    val result = sample.add(333,333)
    print(result)
    println("! You succeed!")
    val numAs = logData.filter(line => line.contains(result.toString)).count()
    println("Lines with a: %s".format(numAs))
  }
}