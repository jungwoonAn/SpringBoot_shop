package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public Long saveItem(ItemFormDto itemFormDto,
             List<MultipartFile> itemImgFileList) throws Exception {
        //상품 등록
        Item item = itemFormDto.createItem();  // 폼으로 입력받은 데이터로 item 객체 생성
        itemRepository.save(item);  // DB에 저장

        //이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i==0){  // 첫번째 이미지를 대표 이미지로 세팅
                itemImg.setRepImgYn("Y");
            }else{
                itemImg.setRepImgYn("N");
            }
            // 이미지 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }
    
    // 상품 불러오는 메서드
    @Transactional(readOnly = true)  // 상품 데이터 읽어오는 트랜잭션을 읽기 전용으로 설정
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    // 상품을 업데이트할 때더 변경 감지 기능 사용
    public Long updateItem(ItemFormDto itemFormDto,
               List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())  // 전달받은 상품 아이디로 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Item updatedItem = itemFormDto.updateItem(itemFormDto);  // 상품 엔티티 업데이트
        itemRepository.save(updatedItem);

        // 이미지 등록
        List<Long> itemImgIds = itemFormDto.getItemImgIds();  // 상품 이미지 아이디 리스트 조회

        for(int i = 0; i < itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }

    // 상품 조회 조건과 페이지 정보를 받아 상품 데이터 조회
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    // 메인 페이지에 보여줄 상품 데이터 조회
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
