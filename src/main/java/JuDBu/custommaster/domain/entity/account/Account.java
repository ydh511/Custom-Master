package JuDBu.custommaster.domain.entity.account;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;    // 유저 아이디
    @Column(nullable = false)
    private String password;    // 비밀번호
    @Column(nullable = false)
    private String name;        // 유저 이름

    @Column(nullable = false, unique = true)
    private String email;       // 이메일
    private String businessNumber;  // 사업자 등록번호
    @Enumerated(EnumType.STRING)
    @Setter
    private Authority authority;    // 권한

    public void updateInfo(
            String password,
            String email 
    ){
        this.password = password;
        this.email = email;
    }

    public void updateBusinessNumber(
            String businessNumber
    ) {

        this.businessNumber = businessNumber;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", businessNumber='" + businessNumber + '\'' +
                ", authority=" + authority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(getId(), account.getId()) && Objects.equals(getUsername(), account.getUsername()) && Objects.equals(getPassword(), account.getPassword()) && Objects.equals(getName(), account.getName()) && Objects.equals(getEmail(), account.getEmail()) && Objects.equals(getBusinessNumber(), account.getBusinessNumber()) && getAuthority() == account.getAuthority();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getName(), getEmail(), getBusinessNumber(), getAuthority());
    }
}
