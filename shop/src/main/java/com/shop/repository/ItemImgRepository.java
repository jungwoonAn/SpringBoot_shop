package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
    
    // itemId에 해당하는 대표 이미지 반환
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);
}
