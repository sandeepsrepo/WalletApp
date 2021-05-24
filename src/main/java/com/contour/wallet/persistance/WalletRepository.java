package com.contour.wallet.persistance;

import com.contour.wallet.model.CoinEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends CrudRepository<CoinEntity, Integer> {

    @Query("SELECT SUM(w.coinValue * w.coinCount) as total from CoinEntity w")
    Integer getCurrentBalance();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE CoinEntity c SET c.coinCount = :coinCount where c.coinValue = :coinValue")
    Integer updateByCoinValue(@Param("coinValue") Integer coinValue, @Param("coinCount") Integer coinCount);
}
