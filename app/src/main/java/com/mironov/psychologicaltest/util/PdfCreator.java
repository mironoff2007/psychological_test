package com.mironov.psychologicaltest.util;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
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

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, pageNumber).create();

        PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = documentPage.getCanvas();
        int y = 25; // x = 10,
        //int x = (canvas.getWidth() / 2);
        int x = 10;

        paint.getTextBounds(questionText ,0, questionText.length(), bounds);
        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        canvas.drawText(questionText, x, y, paint);


        //blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

        //answer
        paint.getTextBounds(userAnswer, 0, userAnswer.length(), bounds);
        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        y += paint.descent() - paint.ascent();
        canvas.drawText(userAnswer, x, y, paint);

        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);



        //horizontal line
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);
        canvas.drawLine(0, y, pageWidth, y, paint2);

        //blank space
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

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
