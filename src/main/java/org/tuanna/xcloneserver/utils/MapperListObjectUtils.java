package org.tuanna.xcloneserver.utils;

import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MapperListObjectUtils {
    public static <T> List<T> mapListOfObjects(List<Tuple> tuples, Class<T> targetType) {
        List<T> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            try {
                Constructor<T> constructor = targetType.getDeclaredConstructor();
                T targetObject = constructor.newInstance();
                Field[] fields = targetType.getDeclaredFields();
                for (int i = 0; i < tuple.getElements().size(); i++) {
                    String alias = tuple.getElements().get(i).getAlias();
                    if (alias != null) {
                        String camelCaseAlias = null;
                        try {
                            camelCaseAlias = toCamelCase(alias);
                            Object value = tuple.get(i);
                            setField(targetObject, fields, value, camelCaseAlias);
                        } catch (Exception e) {
                            log.error("error set filed {}", camelCaseAlias);
                        }

                    }
                }
                result.add(targetObject);
            } catch (Exception e) {
                log.error("error mapper dto " + e.getMessage());
            }
        }
        return result;

    }

    public static String toCamelCase(String input) {
        String[] words = input.split("[_\\- ]+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                if (i == 0) {
                    result.append(word.substring(0, 1).toLowerCase());
                    result.append(word.substring(1));
                } else {

                    result.append(word.substring(0, 1).toUpperCase());
                    result.append(word.substring(1));
                }
            }
        }
        return result.toString();
    }

    @SuppressWarnings("java:S3011")
    private static <T> void setField(T targetObject, Field[] fields, Object value, String fileName) throws IllegalAccessException {
        Field field = getField(fields, fileName);
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            if (!field.getType().isInstance(value)) {
                value = castValueToFieldType(value, field.getType());
            }
            field.set(targetObject, value);

        }
    }

    private static Object castValueToFieldType(Object value, Class<?> fieldType) {
        if (value != null) {
            if (fieldType == int.class || fieldType == Integer.class) {
                return Integer.parseInt(value.toString());
            } else if (fieldType == double.class || fieldType == Double.class) {
                return Double.parseDouble(value.toString());
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                return Boolean.parseBoolean(value.toString());
            } else if (fieldType == String.class) {
                return value.toString();
            } else if (fieldType == float.class || fieldType == Float.class) {
                return Float.parseFloat(value.toString());
            } else if (fieldType == BigDecimal.class) {
                return new BigDecimal(value.toString());
            } else if (fieldType == long.class || fieldType == Long.class) {
                return Long.parseLong(value.toString());
            } else if (fieldType == LocalDateTime.class) {
                Timestamp timestamp = (Timestamp) value;
                return timestamp.toLocalDateTime();
            } else if (fieldType == LocalDate.class) {
                try {
                    Timestamp timestamp = (Timestamp) value;
                    return timestamp.toLocalDateTime().toLocalDate();
                } catch (Exception e) {
                    return ((Date) value).toLocalDate();
                }

            } else if (fieldType == OffsetDateTime.class) {
                Timestamp timestamp = (Timestamp) value;
                Instant instant = timestamp.toInstant();
                return instant.atOffset(ZoneOffset.UTC);
            }
        }
        return null;
    }

    private static Field getField(Field[] fields, String alias) {
        for (Field field : fields) {
            if (field.getName().equals(alias)) {
                return field;
            }
        }
        return null;
    }


}
