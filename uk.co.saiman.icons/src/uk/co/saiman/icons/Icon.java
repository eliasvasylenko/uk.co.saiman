package uk.co.saiman.icons;

public interface Icon {
  IconSize getSize();

  int getAlpha(int x, int y);

  int getRed(int x, int y);

  int getGreen(int x, int y);

  int getBlue(int x, int y);
}
