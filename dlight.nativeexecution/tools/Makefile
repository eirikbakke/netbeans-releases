# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
#
# Oracle and Java are registered trademarks of Oracle and/or its affiliates.
# Other names may be trademarks of their respective owners.
#
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common
# Development and Distribution License("CDDL") (collectively, the
# "License"). You may not use this file except in compliance with the
# License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html
# or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
# specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header
# Notice in each file and include the License file at
# nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
# particular file as subject to the "Classpath" exception as provided
# by Oracle in the GPL Version 2 section of the License file that
# accompanied this code. If applicable, add the following below the
# License Header, with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# Contributor(s):
#
# The Original Software is NetBeans. The Initial Developer of the Original
# Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
# Microsystems, Inc. All Rights Reserved.
#
# If you wish your version of this file to be governed by only the CDDL
# or only the GPL Version 2, indicate your decision by adding
# "[Contributor] elects to include this software in this distribution
# under the [CDDL or GPL Version 2] license." If you do not indicate a
# single choice of license, a recipient has the option to distribute
# your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above.
# However, if you add GPL Version 2 code and therefore, elected the GPL
# Version 2 license, then the option applies only if the new code is
# made subject to such option by the copyright holder.

INSTALLDIR = /dev/null

testit :sh= uname -s

#CFLAGS-Windows_XP := -mno-cygwin
CFLAGS-Linux := -fPIC -Wl,--hash-style=sysv
CFLAGS-SunOS := -fPIC
CFLAGS-Mac_OS_X := -fPIC
LDFLAGS-SunOS := -lposix4
ifeq (armv, $(shell expr substr `arch` 1 4))
    FLAG64:=
else	
    FLAG64:=$(shell (echo ${PLATFORM_DIR} | grep 64 >/dev/null && echo -m64) || echo -m32)
endif

_OS = $(shell uname -s)
_OS:sh = uname -s
ifeq (CYGWIN, $(shell expr substr `uname -s` 1 6))
    OS=Windows
else
    OS=$(_OS)
endif
ARCH = $(shell arch)
ARCH:sh = arch

ifeq (armv, $(shell expr substr `arch` 1 4))
    OS_ARCH = $(OS)-arm
else 
  ifeq (sun4, $(ARCH))
    OS_ARCH = $(OS)-sparc
  else 
    ifeq (sparc64, $(ARCH))
      OS_ARCH = $(OS)-sparc
    else
      OS_ARCH = $(OS)-x86
    endif
  endif
endif

CF_Linux-x86      = -DLINUX   -D_GNU_SOURCE -D_XOPEN_SOURCE=700
CF_Linux-sparc    = -DLINUX   -D_GNU_SOURCE -D_XOPEN_SOURCE=700
CF_Linux-arm      = -DLINUX   -D_GNU_SOURCE -D_XOPEN_SOURCE=700 -std=c99
CF_MacOSX-x86     = -DMAXOSX  -D_DARWIN_C_SOURCE
CF_SunOS-sparc    = -DSOLARIS -D__EXTENSIONS__
CF_SunOS-x86      = -DSOLARIS
CF_Windows-x86    = -DWINDOWS

LF_Windows-x86    = --static-libgcc
LF_Windows-x86_64 = --static-libgcc

CFLAGS := $(CF_$(OS_ARCH)) $(CFLAGS_EXTRA)
LDFLAGS := $(LF_$(OS_ARCH)) $(LDFLAGS_EXTRA)

CC=gcc

CFLAGS += $(CFLAGS-$(OSFAMILY))
LDFLAGS += $(LDFLAGS-$(OSFAMILY))

ifndef NOSTRIP
    CFLAGS += -s 
else 
    CFLAGS += -g
endif

DIST=../release/bin/nativeexecution
BUILD=./build

APP_PATH=${DIST}/${PLATFORM_DIR}

all: init privp process_start pty_open sigqueue stat

privp: init
	${CC} ${CFLAGS} ${FLAG64} -o ${APP_PATH}/$@ $@.c

process_start: init
	${CC} ${CFLAGS} ${FLAG64} -o ${APP_PATH}/$@ $@.c

pty_open: init
	${CC} ${CFLAGS} ${FLAG64} -o ${APP_PATH}/$@ $@.c

sigqueue: init
	${CC} ${CFLAGS} ${FLAG64} ${LDFLAGS} -o ${APP_PATH}/$@ $@.c

stat: init
	${CC} ${CFLAGS} ${FLAG64} -o ${APP_PATH}/$@ $@.c

clean:
	rm -rf ${APP_PATH} ${DIST}/${PLATFORM_DIR}

cleanall:
	rm -rf ${BUILD} ${DIST}

init: check
	@mkdir -p ${APP_PATH} ${DIST}/${PLATFORM_DIR}

check:
	@if [ -n "$(testit)" ]; \
	then \
		echo "Error: You must use GNU make"; \
		exit 1; \
	fi
	
	@if [ "${PLATFORM_DIR}" = "" ]; \
	then \
		echo "PLATFORM_DIR undefined"; \
		exit 1; \
	fi

