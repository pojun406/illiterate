package com.illiterate.illiterate.security.Entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh_token")
public class RefreshEntity {

    @Id
    private String memberId;

    @Indexed
    private String refresh;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshEntity(String memberId, String refresh, Long expiration){
        this.memberId = memberId;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
