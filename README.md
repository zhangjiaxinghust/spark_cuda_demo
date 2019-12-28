# 使用介绍
首先运行`javah_build.sh`生成工程所需的头文件`Sample.h`
运行`build.sh`生成c++动态库
运行`copy_build.sh`将动态库拷贝到各个节点
运行`sbt package`打包