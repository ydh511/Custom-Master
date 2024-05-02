package JuDBu.custommaster.domain.entity;

import JuDBu.custommaster.domain.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Setter
    private String name;

    @Setter
    private String address;

    private String phoneNumber;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Product> products = new ArrayList<>();

    public static Shop createShop(Account accountShop, String name, String address, String phoneNumber) {
        return Shop.builder()
                .account(accountShop)
                .name(name)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }

    public void updateShop(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
