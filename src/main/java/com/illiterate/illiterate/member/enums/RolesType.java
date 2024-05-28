package com.illiterate.illiterate.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RolesType {
	ROLE_ADMIN, // 관리자
	ROLE_USER, // 일반 회원
	ROLE_STORE_OWNER; // 가게 회원

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
