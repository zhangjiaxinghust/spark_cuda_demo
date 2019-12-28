cmake_minimum_required(VERSION 3.15)

PROJECT(test_add)

set(CMAKE_POSITION_INDEPENDENT_CODE ON)
SET(LIBRARY_OUTPUT_PATH /usr/local/spark/dynamic)
if(NOT DEFINED ENV{JAVA_HOME})
    message(FATAL_ERROR "not defined environment variable:JAVA_HOME")
endif()

find_package(CUDA)

if(${CUDA_FOUND})
	include_directories(${CUDA_INCLUDE_DIRS})
	link_directories($ENV{CUDA_PATH}/lib/x64)
else(${CUDA_FOUND})
	MESSAGE(STATUS "cuda not found!")
endif(${CUDA_FOUND})

set(JNI_INCLUDE "$ENV{JAVA_HOME}/include")
include_directories(${JNI_INCLUDE})
include_directories("${JNI_INCLUDE}/linux")
INCLUDE_DIRECTORIES(
src/_include
_include
/usr/local/spark/dynamic
)
add_subdirectory(src)
AUX_SOURCE_DIRECTORY(. DIR_INTERFACE)
SET(TEST_ADD
${DIR_INTERFACE}
)

ADD_LIBRARY(add SHARED ${TEST_ADD})

TARGET_LINK_LIBRARIES( add Test1 )
