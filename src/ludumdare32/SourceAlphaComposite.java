package ludumdare32;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * A {@link Composite} implementation that uses the destination RGB and the source alpha.
 * This can be used to perform alpha gradients.
 */
public class SourceAlphaComposite implements Composite, CompositeContext {
  public static SourceAlphaComposite createComposite(final BufferedImage bimage) throws UnsupportedBufferException {
    return createComposite(bimage.getType());
  }
  /**
   * This factory currently supports <code>TYPE_4BYTE_ABGR</code> and <code>TYPE_INT_ARGB</code>.
   */
  public static SourceAlphaComposite createComposite(int type) throws UnsupportedBufferException {
    switch ( type ) {
      case BufferedImage.TYPE_4BYTE_ABGR:
      case BufferedImage.TYPE_INT_ARGB:
        return new SourceAlphaComposite( 3 );
      default:
        throw new UnsupportedBufferException();
    }
  }
  private final int alphaIndex;
  public SourceAlphaComposite(final int alphaIndex) {
    if ( alphaIndex < 0 ) {
      throw new IllegalArgumentException( "There is no way a negative index will work." );
    }
    this.alphaIndex = alphaIndex;
  }
  public int getAlphaIndex() {
    return alphaIndex;
  }
  @Override
  public CompositeContext createContext(
    final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints
  ) {
    return this;
  }
  @Override
  public void dispose() {
      // Do nothing
    }
  @Override
    public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
      final int
        w = dstOut.getWidth(),
        h = dstOut.getHeight();
      final int n = src.getNumBands();
      final int[] spixel = new int[ n ], opixel = new int[ n ], dpixel = new int[ n ];
      for ( int x = 0; w > x; x++ )
        for ( int y = 0; h > y; y++ ) {
          src.getPixel( x, y, spixel );
          dstIn.getPixel( x, y, dpixel );
          // Use the destination color (except use the source alpha, below):
          System.arraycopy( dpixel, 0, opixel, 0, opixel.length );
          final int dalpha = dpixel[ alphaIndex ];
          if ( 0 != dalpha ) {
            // Use the source alpha:
            opixel[ alphaIndex ] = dalpha * spixel[ alphaIndex ] / 0xFF;
          }
          dstOut.setPixel( x, y, opixel );
        }
    }
    /**
     * Exception we throw for images that we don't support.
     */
    public static class UnsupportedBufferException extends Exception {
    public UnsupportedBufferException() {
    }
    public UnsupportedBufferException(final Exception cause) {
      super( cause );
    }
  }
}