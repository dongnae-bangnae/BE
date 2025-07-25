package DNBN.spring.domain.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Color {
  RED, ORANGE, YELLOW, GREEN, SKYBLUE, INDIGO, PURPLE, BLACK;

  private static final Map<String, Color> BY_NAME =
      Arrays.stream(values())
          .collect(Collectors.toMap(
              color -> color.name().toLowerCase(),
              Function.identity()
          ));

  public static Color from(String value) {
    Color color = BY_NAME.get(value.toLowerCase());
    if (color == null) {
      throw new IllegalArgumentException("지원하지 않는 색상입니다: " + value);
    }
    return color;
  }
}