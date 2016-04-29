package chau.bankingloan;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UploadProgress2 extends HttpEntityWrapper

{
	private final ProgressListener listener;

	public UploadProgress2(final HttpEntity entity, final ProgressListener listener) {
		super(entity);
		this.listener = listener;
	}	

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener, this.getContentLength()));
	}

	public interface ProgressListener {
//		void transferred(long num);
		/**
		 * Giá trị đã up lên được
		 * @author com08
		 * @param num Giá trị hiện tại
		 */
		void transferred(float num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;
		private long total;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener, long total) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
			this.total = total;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this._getCurrentProgress());
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this._getCurrentProgress());
		}
		
		private int _getCurrentProgress()
		{
			return (int) ((this.transferred / (float) this.total) * 100);
		}
	}
}


