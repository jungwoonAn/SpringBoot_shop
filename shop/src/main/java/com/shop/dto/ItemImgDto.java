package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {

    private Long id;

    private String imgName;  // 이미지 파일명

    private String oriImgName;  // 원본 이미지 파일명

    private String imgUrl;  // 이미지 조회 경로

    private String repImgYn;  // 대표 이미지 여부

    // 멤버 변수로 ModelMapper 객체를 추가
    private static ModelMapper modelMapper = new ModelMapper();

    // ItemImg 엔티티를 받아서, ItemImgDto로 변환
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class);
    }


}
