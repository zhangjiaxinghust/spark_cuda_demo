class Sample {
  // --- Native methods
  def loadlibrary: Unit ={
    System.load("/usr/local/spark/dynamic/libadd.so")
  }

  @native def add(n: Int,m: Int): Int
}
