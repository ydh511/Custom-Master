package JuDBu.custommaster.domain.entity.account;

import lombok.Getter;

@Getter
public enum Authority {
    ROLE_INACTIVE_USER("비활성유저"),
    ROLE_ACTIVE_USER("일반유저"),
    ROLE_BUSINESS_USER("판매자"),
    ROLE_ADMIN("관리자");

    private String authority;

    Authority(String authority) {
        this.authority = authority;
    }
}
