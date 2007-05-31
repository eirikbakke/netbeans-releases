#
# Gererated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add custumized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=g77

# Include project Makefile
include Makefile

# Object Files
OBJECTFILES= \
	build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.common/src/CommonUtils.o \
	build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/UnixUtils.o \
	build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/jni_UnixNativeUtils.o

# C Compiler Flags
CFLAGS=-m32 -fPIC -shared -static-libgcc -mimpure-text -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE

# CC Compiler Flags
CCFLAGS=-m32 -fPIC -shared -static-libgcc -mimpure-text -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE
CXXFLAGS=-m32 -fPIC -shared -static-libgcc -mimpure-text -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS} dist/solaris-sparc.so

dist/solaris-sparc.so: ${OBJECTFILES}
	@${MKDIR} -p dist
	${LINK.c} -shared -static-libgcc -mimpure-text -o dist/solaris-sparc.so -s ${OBJECTFILES} ${LDLIBSOPTIONS} 

build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.common/src/CommonUtils.o: ../.common/src/CommonUtils.c 
	@${MKDIR} -p build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.common/src
	$(COMPILE.c) -s -I/usr/java/include -I/usr/java/include/solaris -o build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.common/src/CommonUtils.o ../.common/src/CommonUtils.c

build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/UnixUtils.o: ../.unix/src/UnixUtils.c 
	@${MKDIR} -p build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src
	$(COMPILE.c) -s -I/usr/java/include -I/usr/java/include/solaris -o build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/UnixUtils.o ../.unix/src/UnixUtils.c

build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/jni_UnixNativeUtils.o: ../.unix/src/jni_UnixNativeUtils.c 
	@${MKDIR} -p build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src
	$(COMPILE.c) -s -I/usr/java/include -I/usr/java/include/solaris -o build/Debug/GNU-Solaris-Sparc/_ext/export/home/ksorokin/Work/nbi-trunk/engine/native/solaris-sparc/../.unix/src/jni_UnixNativeUtils.o ../.unix/src/jni_UnixNativeUtils.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Debug
	${RM} dist/solaris-sparc.so

# Subprojects
.clean-subprojects:
