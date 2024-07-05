package com.illiterate.illiterate.common.util;

import com.illiterate.illiterate.member.enums.RolesType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ConvertUtil {
	/**
	 * domain <-> dto
	 */
	public static <S, T> T toDtoOrEntity(S source, Class<T> targetType) {
		Class<?> sourceClass = source.getClass();
		T instance = createInstance(targetType);

		for(Field targetField: targetType.getDeclaredFields()) {
			try {
				Class<?> fieldType = targetField.getType();

				Field sourceField = sourceClass.getDeclaredField(targetField.getName());

				// Access 접근제한 먼저 true 처리 후, sourceFile에 get, set 접근할 수 있음.
				sourceField.setAccessible(true);
				targetField.setAccessible(true);

				// 비어있지 않고, ArrayList의 경우 편의성 메서드를 통해 양방향 매핑한 경우가 아닐때 처리
				if(Optional.ofNullable(sourceField.get(source)).isEmpty()
					|| List.class.isAssignableFrom(fieldType)
					|| List.class.isAssignableFrom(sourceField.getType())
				) {

				}else if(fieldType.isEnum()) {
					// enumType 변환
					Object[] enumConstants = fieldType.getEnumConstants();

					String prefix = fieldType.isAssignableFrom(RolesType.class) ? "ROLE_" : "";

					for (Object enumConstant : enumConstants) {
						System.out.println("sourceField name: " + sourceField.get(source));
						if (enumConstant.toString().equals(prefix + sourceField.get(source).toString())) {
							targetField.set(instance, enumConstant);
							break;
						}
					}
				}else {
					Object value = sourceField.get(source);
					targetField.set(instance, value);
				}

			}catch(NoSuchFieldException | IllegalAccessException e){
				// 해당 필드 없음 또는 접근할 수 없는 경우 무시
			}
		}

		return instance;
	}

	/**
	 * 클래스 인스턴스 생성
	 */
	private static <T> T createInstance(Class<T> classType) {
		try{
			return classType.getConstructor(null).newInstance();
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("인스턴스 생성 중 오류가 발생하였습니다.");
		}
	}
}