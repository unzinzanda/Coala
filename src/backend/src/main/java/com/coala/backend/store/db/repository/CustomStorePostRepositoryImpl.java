package com.coala.backend.store.db.repository;

import com.coala.backend.store.db.entity.StorePost;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class CustomStorePostRepositoryImpl implements CustomStorePostRepository{


    EntityManager entityManager;

    public CustomStorePostRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<StorePost> findAllFilter(Map<String, String> info, Integer page) {
        StringBuilder sb = new StringBuilder();
        sb.append("select s from StorePost s");

        if(info.get("category").equals("") &&  info.get("Period").equals("") &&  info.get("minRentalCost").equals("") &&  info.get("maxRentalCost").equals("") && !info.get("status").equals("1") ){
            sb.append(" order by s.id desc");
            System.out.println(sb);
            return entityManager.createQuery(sb.toString(), StorePost.class).setFirstResult(page * 10).setMaxResults(10).getResultList();
        }

        sb.append(" where ");

        String and = " and ";

        int plus = 0;

        // 분류(코드로 분류)
        if(!info.get("category").equals("")){
            sb.append("s.category like ").append(info.get("category"));
            plus++;
        }

        // 대여기간(필터 최소 대여 기간 기준)
        if(!info.get("Period").equals("")){
            if(plus != 0){
                sb.append(and);
            }
            sb.append("s.minRentalPeriod <= ").append(info.get("Period"));
            plus++;
        }

        // 대여기간(필터 최소 대여 기간 기준)
        if(!info.get("Period").equals("")){
            if(plus != 0){
                sb.append(and);
            }
            sb.append("s.maxRentalPeriod >= ").append(info.get("Period"));
            plus++;
        }

        // 가격(이상)
        if(!info.get("minRentalCost").equals("")){
            if(plus != 0){
                sb.append(and);
            }
            sb.append("s.rentalCost >= ").append(info.get("minRentalCost"));
            plus++;
        }

        // 가격(이하)
        if(!info.get("maxRentalCost").equals("")){
            if(plus != 0){
                sb.append(and);
            }
            sb.append("s.rentalCost <= ").append(info.get("maxRentalCost"));
            plus++;
        }

        if(info.get("status").equals("1")){
            if(plus != 0){
                sb.append(and);
            }
            sb.append("s.status like 1");
        }

        sb.append(" order by s.id desc");
        System.out.println(sb);
        List<StorePost> list = entityManager.createQuery(sb.toString(),StorePost.class).setFirstResult(page * 12).setMaxResults(12).getResultList();
        return list;
    }


}
