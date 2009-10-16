/*
 * Copyright (c) 2009, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdio.h>

#include "bmp.h"


// Constructor

Bitmap::Bitmap() {
}


// Constructor that loads bitmap from file

Bitmap::Bitmap(string name) {
    Load(name);
}


// Copy constructor

Bitmap::Bitmap(const Bitmap& orig) {
}


// Destructor

Bitmap::~Bitmap() {
    delete[] bitmap;
}


// Load from file (is not public. use constructor instead)

void Bitmap::Load(string filename) {
    FILE* file = fopen(filename.c_str(), (char *) &"rb");

    // obtaining file size
    fseek(file, 0, SEEK_END);
    int fileSize = ftell(file);
    rewind(file);

    // reading header
    fread(&fileHeader, 1, sizeof (fileHeader), file);

    // initializing data structures
    if (fileHeader.bfType != 0x4d42) {
        printf("It's not BMP!\n");
    } else {
        // allocating memory
        bitmapSize = fileSize - sizeof (fileHeader);
        bitmap = new unsigned char[bitmapSize];

        // reading data
        fread(bitmap, 1, bitmapSize, file);
        bitmapInfo = (BITMAPINFO*) bitmap;
        bitmapHeader = (BITMAPINFOHEADER*) bitmap;
        int usedColorNumber = (int) bitmapHeader->biClrUsed;
        int colotTableSize = usedColorNumber * sizeof (RGBQUAD);
        data = bitmap + bitmapHeader->biSize + colotTableSize;
    }
    fclose(file);
}


// Save to file

void Bitmap::Save(string filename) {
    FILE* file = fopen(filename.c_str(), (char *) &"wb+");

    // writing header
    fwrite(&fileHeader, 1, sizeof (fileHeader), file);

    // writing data
    fwrite(bitmap, 1, bitmapSize, file);
    fclose(file);
}


// Prints bitmap info to console

void Bitmap::PrintInfo() {
    printf("BMP Image:\n");
    printf("    Width: %d\n", GetWidth());
    printf("    Height: %d\n", GetHeight());
    printf("    Bits per color: %d\n", GetBitCount());
}


// Bitmap bit count getter

int Bitmap::GetBitCount() {
    return bitmapInfo->bmiHeader.biBitCount;
}


// Bitmap bit width getter

int Bitmap::GetWidth() {
    return bitmapInfo->bmiHeader.biWidth;
}


// Bitmap bit height getter

int Bitmap::GetHeight() {
    return bitmapInfo->bmiHeader.biHeight;
}


// Returns pointer to image data

unsigned char* Bitmap::GetData() {
    return data;
}


// Bitmap size getter

int Bitmap::GetImageSize() {
    return bitmapInfo->bmiHeader.biSizeImage;
}


// Initializes bitmap with test autogenerated image

void Bitmap::GenerateTestImage() {
    int width = 2048;
    int height = 2048;
    int bitsPerColor = 24;

    int bitmapDataSize = width * height * bitsPerColor / sizeof (unsigned char);

    // setting file header
    fileHeader.bfType = 0x4d42; // 0x4d42 is bitmap file identifier
    fileHeader.bfOffBits = sizeof (BITMAPFILEHEADER) + sizeof (BITMAPINFOHEADER);
    fileHeader.bfReserved1 = 0;
    fileHeader.bfReserved2 = 0;
    fileHeader.bfSize = sizeof (BITMAPFILEHEADER) + sizeof (BITMAPINFOHEADER) +
            bitmapDataSize;

    // allocating memory
    bitmapSize = sizeof (BITMAPINFOHEADER) + bitmapDataSize;
    bitmap = new unsigned char[bitmapSize];
    bitmapInfo = (BITMAPINFO*) bitmap;
    bitmapHeader = (BITMAPINFOHEADER*) bitmap;

    // setting bitmap header
    bitmapHeader->biWidth = width;
    bitmapHeader->biHeight = height;
    bitmapHeader->biBitCount = bitsPerColor;
    bitmapHeader->biClrImportant = 0;
    bitmapHeader->biClrUsed = 0;
    bitmapHeader->biCompression = 0;
    bitmapHeader->biPlanes = 1;
    bitmapHeader->biSizeImage = bitmapDataSize;
    bitmapHeader->biXPelsPerMeter = 2835;
    bitmapHeader->biYPelsPerMeter = 2835;
    bitmapHeader->biSize = sizeof (BITMAPINFOHEADER);

    // initializing data
    int usedColorNumber = (int) bitmapHeader->biClrUsed;
    int colotTableSize = usedColorNumber * sizeof (RGBQUAD);
    data = bitmap + bitmapHeader->biSize + colotTableSize;

    unsigned char* d = data;

    // generating image
    for (int i = 0; i < GetWidth(); i++) {
        for (int j = 0; j < GetHeight(); j++) {
            d[0] = i % 255;
            d[1] = j % 255;
            d[2] = (i + j) % 255;

            d += 3;
        }
    }
}
