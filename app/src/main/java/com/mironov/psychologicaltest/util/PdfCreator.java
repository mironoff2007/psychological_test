package com.mironov.psychologicaltest.util;


import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreator {

    private PdfDocument pdfDocument;
    private Paint paint;

    private Rect bounds;
    private int pageWidth;
    private int pageHeight;
    private int pathHeight;

    private int y;//canvas current Y coordinate
    
    private int pageNumber;

    private PdfDocument.PageInfo pageInfo ;

    private PdfDocument.Page currentPage;

    private Canvas canvas;


    private String filePath;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createpdf(String filePath, int pageWidth, int pageHeight) {
        this.filePath=filePath;
        bounds = new Rect();

        pathHeight = 2;

        this.pageWidth=pageWidth;
        this.pageHeight=pageHeight;

        pdfDocument = new PdfDocument();

        paint = new Paint();
        
        y=0;
        pageNumber=1;

        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        currentPage = pdfDocument.startPage(pageInfo);
        canvas = currentPage.getCanvas();
        canvas.save();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addLine(String text, Layout.Alignment alignment ){
        int tab=0;
        if(alignment.equals(Layout.Alignment.ALIGN_NORMAL)){tab=10;}

        TextPaint mTextPaint=new TextPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        DynamicLayout mTextLayout = new DynamicLayout(text, mTextPaint, canvas.getWidth()-tab, alignment, 1.0f, 0.0f, false);

        if(y+mTextLayout.getHeight()>pageHeight) {
            addPage(text, alignment);
        }

        canvas.translate(tab, y);
        mTextLayout.draw(canvas);
        y=y+mTextLayout.getHeight();
        canvas.restore();
        canvas.save();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addPage(String text, Layout.Alignment alignment){

        pdfDocument.finishPage(currentPage);
        pageNumber++;
        y=0;

        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        currentPage = pdfDocument.startPage(pageInfo);
        canvas = currentPage.getCanvas();
        canvas.save();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void writePDF(){
        pdfDocument.finishPage(currentPage);
        File file = new File(filePath);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("My_tag",e.toString());
        }
        pdfDocument.close();
    }
}
