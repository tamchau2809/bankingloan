package chau.bankingloan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.print.PrintHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created on 09-May-16 by com08.
 */
public class ApproveFragment extends Fragment {
    View rootView;
    ImageButton btnPrint;
    PrintedPdfDocument mPdfDocument;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_approve, container, false);

        btnPrint = (ImageButton)rootView.findViewById(R.id.imgBtnPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                PrintManager printManager = (PrintManager) getActivity()
//                        .getSystemService(Context.PRINT_SERVICE);
//
//                String jobName = getActivity().getString(R.string.app_name) +
//                        " Document";
//
//                printManager.print(jobName, new CustomDocumentPrinting(getContext()),
//                        null);

                PrintHelper photoPrinter = new PrintHelper(getContext());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tezt);
                photoPrinter.printBitmap("droids.jpg - test print", bitmap);
            }
        });

//        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
//        Glide.with(this).load(R.raw.test).into(imageViewTarget);

        return rootView;
    }
}
