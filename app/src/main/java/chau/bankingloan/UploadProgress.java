//package chau.bankingloan;
//
//import android.os.RecoverySystem;
//
//import java.io.File;
//import java.io.IOException;
//
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okhttp3.internal.Util;
//import okio.BufferedSink;
//import okio.Okio;
//import okio.Source;
//
///**
// * Created on 11-May-16 by com08.
// */
//public class UploadProgress extends RequestBody
//{
//    private static final int SEGMENT_SIZE = 2048;
//    private final File file;
//    private final ProgressListener listener;
//    private final String contentType;
//
//    public UploadProgress(File file, String contentType, ProgressListener listener) {
//        this.file = file;
//        this.contentType = contentType;
//        this.listener = listener;
//    }
//
//    @Override
//    public long contentLength() throws IOException {
//        return file.length();
//    }
//
//    @Override
//    public MediaType contentType() {
//        return MediaType.parse(contentType);
//    }
//
//    @Override
//    public void writeTo(BufferedSink sink) throws IOException {
//        Source source = null;
//        try
//        {
//            source = Okio.source(file);
//            long total = 0;
//            long read;
//
//            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
//                total += read;
//                sink.flush();
//                this.listener.transferred(total);
//
//            }
//        } finally {
//            Util.closeQuietly(source);
//        }
//    }
//
//    public interface ProgressListener {
//        void transferred(long num);
//    }
//}
