/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fucker.gson;

import com.fucker.gson.internal.$Gson$Preconditions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A class representing a Json primitive value. A primitive value
 * is either a String, a Java primitive, or a Java primitive
 * wrapper type.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class JsonPrimitive extends JsonElement {
  private static final Class<?>[] PRIMITIVE_TYPES = { int.class, long.class, short.class,
      float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class,
      Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };

  private static final BigInteger INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
  private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

  private Object value;

  /**
   * Create a primitive containing a boolean value.
   *
   * @param bool the value to create the primitive with.
   */
  public JsonPrimitive(Boolean bool) {
    setValue(bool);
  }

  /**
   * Create a primitive containing a {@link Number}.
   *
   * @param number the value to create the primitive with.
   */
  public JsonPrimitive(Number number) {
    setValue(number);
  }

  /**
   * Create a primitive containing a String value.
   *
   * @param string the value to create the primitive with.
   */
  public JsonPrimitive(String string) {
    setValue(string);
  }

  /**
   * Create a primitive containing a character. The character is turned into a one character String
   * since Json only supports String.
   *
   * @param c the value to create the primitive with.
   */
  public JsonPrimitive(Character c) {
    setValue(c);
  }

  /**
   * Create a primitive using the specified Object. It must be an instance of {@link Number}, a
   * Java primitive type, or a String.
   *
   * @param primitive the value to create the primitive with.
   */
  JsonPrimitive(Object primitive) {
    setValue(primitive);
  }

  void setValue(Object primitive) {
    if (primitive instanceof Character) {
      // convert characters to strings since in JSON, characters are represented as a single
      // character string
      char c = ((Character) primitive).charValue();
      this.value = String.valueOf(c);
    } else {
      $Gson$Preconditions.checkArgument(primitive instanceof Number
              || isPrimitiveOrString(primitive));
      this.value = primitive;
    }
  }

  /**
   * Check whether this primitive contains a boolean value.
   *
   * @return true if this primitive contains a boolean value, false otherwise.
   */
  public boolean isBoolean() {
    return value instanceof Boolean;
  }

  /**
   * convenience method to get this element as a {@link Boolean}.
   *
   * @return get this element as a {@link Boolean}.
   */
  @Override
  Boolean getAsBooleanWrapper() {
    return (Boolean) value;
  }

  /**
   * convenience method to get this element as a boolean value.
   *
   * @return get this element as a primitive boolean value.
   */
  @Override
  public boolean getAsBoolean() {
    return isBoolean() ? getAsBooleanWrapper().booleanValue() : Boolean.parseBoolean(getAsString());
  }

  /**
   * Check whether this primitive contains a Number.
   *
   * @return true if this primitive contains a Number, false otherwise.
   */
  public boolean isNumber() {
    return value instanceof Number;
  }

  /**
   * convenience method to get this element as a Number.
   *
   * @return get this element as a Number.
   * @throws NumberFormatException if the value contained is not a valid Number.
   */
  @Override
  public Number getAsNumber() {
    return value instanceof String ? stringToNumber((String) value) : (Number) value;
  }

  static Number stringToNumber(String value) {
    try {
      long longValue = Long.parseLong(value);
      if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
        return (int) longValue;
      }
      return longValue;
    } catch (NumberFormatException ignored) {
    }

    try {
      return new BigDecimal(value);
    } catch (NumberFormatException ignored) {
      return Double.parseDouble(value); // probably NaN, -Infinity or Infinity
    }
  }

  /**
   * Check whether this primitive contains a String value.
   *
   * @return true if this primitive contains a String value, false otherwise.
   */
  public boolean isString() {
    return value instanceof String;
  }

  /**
   * convenience method to get this element as a String.
   *
   * @return get this element as a String.
   */
  @Override
  public String getAsString() {
    if (isNumber()) {
      return getAsNumber().toString();
    } else if (isBoolean()) {
      return getAsBooleanWrapper().toString();
    } else {
      return (String) value;
    }
  }

  /**
   * convenience method to get this element as a primitive double.
   *
   * @return get this element as a primitive double.
   * @throws NumberFormatException if the value contained is not a valid double.
   */
  @Override
  public double getAsDouble() {
    return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
  }

  /**
   * convenience method to get this element as a {@link BigDecimal}.
   *
   * @return get this element as a {@link BigDecimal}.
   * @throws NumberFormatException if the value contained is not a valid {@link BigDecimal}.
   */
  @Override
  public BigDecimal getAsBigDecimal() {
    return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
  }

  /**
   * convenience method to get this element as a {@link BigInteger}.
   *
   * @return get this element as a {@link BigInteger}.
   * @throws NumberFormatException if the value contained is not a valid {@link BigInteger}.
   */
  @Override
  public BigInteger getAsBigInteger() {
    return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
  }

  /**
   * convenience method to get this element as a float.
   *
   * @return get this element as a float.
   * @throws NumberFormatException if the value contained is not a valid float.
   */
  @Override
  public float getAsFloat() {
    return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
  }

  /**
   * convenience method to get this element as a primitive long.
   *
   * @return get this element as a primitive long.
   * @throws NumberFormatException if the value contained is not a valid long.
   */
  @Override
  public long getAsLong() {
    return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
  }

  /**
   * convenience method to get this element as a primitive short.
   *
   * @return get this element as a primitive short.
   * @throws NumberFormatException if the value contained is not a valid short value.
   */
  @Override
  public short getAsShort() {
    return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
  }

 /**
  * convenience method to get this element as a primitive integer.
  *
  * @return get this element as a primitive integer.
  * @throws NumberFormatException if the value contained is not a valid integer.
  */
  @Override
  public int getAsInt() {
    return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
  }

  @Override
  public byte getAsByte() {
    return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
  }

  @Override
  public char getAsCharacter() {
    return getAsString().charAt(0);
  }

  /**
   * convenience method to get this element as an Object.
   *
   * @return get this element as an Object that can be converted to a suitable value.
   */
  @Override
  Object getAsObject() {
    if (value instanceof BigInteger) {
      BigInteger big = (BigInteger) value;
      if (big.compareTo(INTEGER_MAX) < 0) {
        return big.intValue();
      } else if (big.compareTo(LONG_MAX) < 0) {
        return big.longValue();
      }
    }
    // No need to convert to float or double since those lose precision
    return value;
  }

  @Override
  protected void toString(Appendable sb, Escaper escaper) throws IOException {
    if (isString()) {
      sb.append('"');
      sb.append(escaper.escapeJsonString(value.toString()));
      sb.append('"');
    } else {
      sb.append(value.toString());
    }
  }

  private static boolean isPrimitiveOrString(Object target) {
    if (target instanceof String) {
      return true;
    }

    Class<?> classOfPrimitive = target.getClass();
    for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
      if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (value == null) {
      return 31;
    }
    // Using recommended hashing algorithm from Effective Java for longs and doubles
    if (isIntegral(this)) {
      long value = getAsNumber().longValue();
      return (int) (value ^ (value >>> 32));
    }
    if (isFloatingPoint(this)) {
      long value = Double.doubleToLongBits(getAsNumber().doubleValue());
      return (int) (value ^ (value >>> 32));
    }
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    JsonPrimitive other = (JsonPrimitive)obj;
    if (value == null) {
      return other.value == null;
    }
    if (isIntegral(this) && isIntegral(other)) {
      return getAsNumber().longValue() == other.getAsNumber().longValue();
    }
    if (isFloatingPoint(this) && isFloatingPoint(other)) {
      double a = getAsNumber().doubleValue();
      // Java standard types other than double return true for two NaN. So, need
      // special handling for double.
      double b = other.getAsNumber().doubleValue();
      return a == b || (Double.isNaN(a) && Double.isNaN(b));
    }
    return value.equals(other.value);
  }

  /**
   * Returns true if the specified number is an integral type
   * (Long, Integer, Short, Byte, BigInteger)
   */
  private static boolean isIntegral(JsonPrimitive primitive) {
    if (primitive.value instanceof Number) {
      Number number = (Number) primitive.value;
      return number instanceof BigInteger || number instanceof Long || number instanceof Integer
      || number instanceof Short || number instanceof Byte;
    }
    return false;
  }

  /**
   * Returns true if the specified number is a floating point type (BigDecimal, double, float)
   */
  private static boolean isFloatingPoint(JsonPrimitive primitive) {
    if (primitive.value instanceof Number) {
      Number number = (Number) primitive.value;
      return number instanceof BigDecimal || number instanceof Double || number instanceof Float;
    }
    return false;
  }
}
