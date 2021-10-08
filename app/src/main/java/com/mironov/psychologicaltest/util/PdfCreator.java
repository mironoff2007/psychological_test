package com.mironov.psychologicaltest.util;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreator {

    private PdfDocument myPdfDocument;
    private Paint paint;
    private Paint paint2;
    private Path path;

    private Rect bounds;
    private int pageWidth;
    private int pageheight;
    private int pathHeight;

    String filePath;
    public void createpdf(String filePath) {
        this.filePath=filePath;
        bounds = new Rect();
        pageWidth = 300;
        pageheight = 470;
        pathHeight = 2;


        myPdfDocument = new PdfDocument();
        paint = new Paint();
        paint2 = new Paint();
        path = new Path();
    }

    public void addPage(String questionText,String userAnswer,int pageNumber){

        int y=0;
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, pageNumber).create();

        PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = documentPage.getCanvas();

        canvas.save();

        TextPaint mTextPaint=new TextPaint();
        paint.getTextBounds(questionText, 0, questionText.length(), bounds);


        DynamicLayout mTextLayout = new DynamicLayout(questionText, mTextPaint, canvas.getWidth()-10, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(10,0);
        mTextLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        //blank space

        int h=mTextLayout.getHeight();
        Log.d("My_tag","layout_height="+mTextLayout.getHeight()*3);
        //answer
        mTextLayout=new DynamicLayout(userAnswer, mTextPaint, canvas.getWidth()-10, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(0,h);
        mTextLayout.draw(canvas);
        canvas.restore();
        myPdfDocument.finishPage(documentPage);
    }


    public void writePDF(){
        File file = new File(filePath);

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("My_tag",e.toString());
        }

        myPdfDocument.close();
    }
}
