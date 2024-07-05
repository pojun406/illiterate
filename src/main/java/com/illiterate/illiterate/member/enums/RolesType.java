package com.illiterate.illiterate.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RolesType {
	ADMIN, // 관리자
	USER; // 일반 회원

	@JsonCreator
	public static RolesType fromRequest(String inputString) {
		for (RolesType rolesType : RolesType.values()) {
			if (rolesType.toString().equals(inputString.toUpperCase())) {
				return rolesType;
			}
		}
		return null;
	}
}
