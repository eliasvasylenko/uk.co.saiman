package uk.co.saiman.webmodule.commonjs.repository;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.co.saiman.webmodule.commonjs.repository.CommonJsJar;

public class JavaNamePartConverterTest {
  @Test
  public void splitOnDot() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void splitOnUnderscore() {
    List<String> parts = CommonJsJar.getJavaNameParts("a_b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void splitOnHyphen() {
    List<String> parts = CommonJsJar.getJavaNameParts("a-b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void prependWithUnderscoreWhenStartWithNumber() {
    List<String> parts = CommonJsJar.getJavaNameParts("1abc", false).collect(toList());

    Assert.assertEquals(Arrays.asList("_1abc"), parts);
  }

  @Test
  public void prependSecondPartWithUnderscoreWhenStartWithNumberAndSeparated() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.1abc", true).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "_1abc"), parts);
  }

  @Test
  public void doNotPrependSecondPartWhenStartWithNumberAndNotSeparated() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.1abc", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "1abc"), parts);
  }
}
