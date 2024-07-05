package com.illiterate.illiterate.security.Repository;


import com.illiterate.illiterate.security.Entity.RefreshEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends CrudRepository<RefreshEntity,String> {

    Boolean existsByRefresh(String refresh);

    RefreshEntity findByRefresh(String refresh);
}
