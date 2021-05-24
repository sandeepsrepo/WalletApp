package com.contour.wallet.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "WALLET")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CoinEntity {

    @Id
    @Column(name = "COIN_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer coinId;

    @NonNull
    @Column(name = "COIN_VALUE", nullable = false, unique = true)
    private Integer coinValue;

    @NonNull
    @Column(name = "COINS_COUNT", nullable = false)
    private Integer coinCount;

}
